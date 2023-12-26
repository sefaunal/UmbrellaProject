package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author github.com/sefaunal
 * @since 2023-12-25
 */
@Service
public class OAuth2Service {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String GITHUB_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String GITHUB_SECRET;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String GITHUB_REDIRECT_URL;

    private final LoginHistoryService loginHistoryService;

    private final UserService userService;

    private final JWTService jwtService;

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Service.class);

    public OAuth2Service(LoginHistoryService loginHistoryService, UserService userService, JWTService jwtService) {
        this.loginHistoryService = loginHistoryService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse authenticateViaGithub(HttpServletRequest servletRequest, String token) {
        String accessToken = getGithubAccessToken(token);
        String email = getGithubUserEmail(accessToken);
        Map<String, String> userDetails = getGithubUserDetails(accessToken);

        // Throw error if details about user couldn't be fetched from GitHub
        if (email == null || userDetails.isEmpty()) {
            throw new BadCredentialsException("Error occurred during authentication!");
        }

        // Update user email if their id match but their email does not
        Optional<User> userByOAuth2 = userService.findByOauth2ID(userDetails.get("id"));
        if (userByOAuth2.isPresent() && !userByOAuth2.get().getEmail().equals(email)) {
            return updateUserViaGithubCredentials(email, userByOAuth2.get());
        }

        // Create a new user on the database if email is not in use
        if (userService.findUserByMail(email).isEmpty()) {
            return createUserViaGithubCredentials(email, userDetails);
        }

        // Fetch user from the database if email is already in use
        User user = userService.findUserByMail(email).orElseThrow();

        // Throw error if user is not an OAuth user and the email is already in use
        if (!user.isOauth2Account()) {
            throw new BadCredentialsException("Email is already in use with different authentication method!");
        }

        // Throw error if user's id fetched from GitHub doesn't match with our own record
        if (!user.getOauth2ID().equals(userDetails.get("id"))) {
            throw new OAuth2AuthenticationException("Verification failed");
        }

        String JWT = jwtService.generateToken(user);

        CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, user.getEmail()));
        return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
    }

    private AuthenticationResponse updateUserViaGithubCredentials(String email, User user) {
        user.setEmail(email);
        userService.saveUser(user);

        String JWT = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
    }

    private AuthenticationResponse createUserViaGithubCredentials(String email, Map<String, String> userDetails) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(userDetails.get("login"));
        user.setLastName(null);
        user.setPassword(null);
        user.setRole("OAUTH2_USER");
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        user.setOauth2Account(true);
        user.setOauth2ID(userDetails.get("id"));
        user.setOauth2Provider("Github");
        userService.saveUser(user);

        String JWT = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
    }

    private String getGithubAccessToken(String token) {
        String accessToken = null;

        // Set the request URL
        String url = "https://github.com/login/oauth/access_token";

        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("Accept", "application/json")
                .build();

        // Set the request body
        RequestBody requestBody = new FormBody.Builder()
                .add("code", token)
                .add("client_id", GITHUB_CLIENT_ID)
                .add("client_secret", GITHUB_SECRET)
                .add("redirect_uri", GITHUB_REDIRECT_URL)
                .build();

        // Create the POST request
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        // Create the OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Send the request and get the response
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            accessToken = responseJson.get("access_token").asText();
        } catch (Exception e) {
            LOG.error("An error occurred: ", e);
        }

        return accessToken;
    }

    private Map<String, String> getGithubUserDetails(String accessToken) {
        Map<String, String> oauth2Details = new HashMap<>();

        // Set the request URL
        String url = "https://api.github.com/user";

        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Authorization", "Bearer " + accessToken)
                .build();

        // Create the GET request
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();

        // Create the OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Send the request and get the response
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            oauth2Details.put("id", responseJson.get("id").asText());
            oauth2Details.put("login", responseJson.get("login").asText());
        } catch (Exception e) {
            LOG.error("An error occurred: ", e);
        }

        return oauth2Details;
    }

    private String getGithubUserEmail(String accessToken) {
        String primaryEmail = null;

        // Set the request URL
        String url = "https://api.github.com/user/emails";

        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Authorization", "Bearer " + accessToken)
                .build();

        // Create the GET request
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();

        // Create the OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Send the request and get the response
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Find the primary email
            for (JsonNode jsonNode : responseJson) {
                if (jsonNode.get("primary").asBoolean()) {
                    primaryEmail = jsonNode.get("email").asText();
                    break;  // Exit the loop once we find the primary email
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred: ", e);
        }

        return primaryEmail;
    }
}