package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Exception.OkHttpRequestException;
import com.sefaunal.umbrellachat.Model.OAuth2UserDetails;
import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Response.AuthenticationResponse;
import com.sefaunal.umbrellachat.Util.RandomUsernameGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${umbrella.variables.url.github.access_token}")
    private String GITHUB_ACCESS_TOKEN_URL;

    @Value("${umbrella.variables.url.github.user_details}")
    private String GITHUB_USER_DETAIL_URL;

    @Value("${umbrella.variables.url.github.user_email}")
    private String GITHUB_USER_EMAIL_URL;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_SECRET;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URL;

    @Value("${umbrella.variables.url.google.access_token}")
    private String GOOGLE_ACCESS_TOKEN_URL;

    @Value("${umbrella.variables.url.google.user_details}")
    private String GOOGLE_USER_DETAIL_URL;

    private final LoginHistoryService loginHistoryService;

    private final UserService userService;

    private final JWTService jwtService;

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Service.class);

    public OAuth2Service(LoginHistoryService loginHistoryService, UserService userService, JWTService jwtService) {
        this.loginHistoryService = loginHistoryService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse authenticateOAuth2(HttpServletRequest servletRequest, String provider, String token) {
        String accessToken;
        OAuth2UserDetails userDetails;

        // Get details about the user based on the OAuth2 provider
        if (provider.equalsIgnoreCase("GitHub")) {
            accessToken = getGithubAccessToken(token);
            userDetails = getGithubUserDetails(accessToken);
        } else if (provider.equalsIgnoreCase("Google")) {
            accessToken = getGoogleAccessToken(token);
            userDetails = getGoogleUserDetails(accessToken);
        } else {
            throw new IllegalArgumentException("Invalid OAuth2 provider specified!");
        }

        // Authenticate the user if they already have an account on the database
        Optional<User> user = userService.findByOauth2ID(userDetails.getOAuth2ID());
        if (user.isPresent()) {
            String JWT = jwtService.generateToken(user.get());

            CompletableFuture.runAsync(() -> loginHistoryService.saveLoginHistory(servletRequest, user.get().getEmail()));
            return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
        }

        // Throw error if the email is already in use
        if (userService.isEmailInUse(userDetails.getEmail())) {
            throw new OAuth2AuthenticationException("Email is already in use with different authentication method!");
        }

        return createUserWithOAuthCredentials(userDetails);
    }

    private AuthenticationResponse createUserWithOAuthCredentials(OAuth2UserDetails userDetails) {
        User user = new User();
        user.setUsername(generateUniqueUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setRole("OAUTH2_USER");
        user.setProfilePictureURI(userDetails.getProfilePictureURI());
        user.setMfaEnabled(false);
        user.setOauth2Account(true);
        user.setOauth2ID(userDetails.getOAuth2ID());
        user.setOauth2Provider(userDetails.getOAuth2Provider());
        userService.saveUser(user);

        String JWT = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(JWT).mfaEnabled(false).build();
    }

    private String generateUniqueUsername() {
        String username = RandomUsernameGenerator.generateRandomUsername();

        while (userService.isUsernameInUse(username)) {
            username = RandomUsernameGenerator.generateRandomUsername();
        }

        return username;
    }

    private String getGoogleAccessToken(String token) {
        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("Accept", "application/json")
                .build();

        // Set the request body
        RequestBody requestBody = new FormBody.Builder()
                .add("code", token)
                .add("client_id", GOOGLE_CLIENT_ID)
                .add("client_secret", GOOGLE_SECRET)
                .add("redirect_uri", GOOGLE_REDIRECT_URL)
                .add("grant_type", "authorization_code")
                .build();

        // Create the POST request
        Request request = new Request.Builder()
                .url(GOOGLE_ACCESS_TOKEN_URL)
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

            return responseJson.get("access_token").asText();
        } catch (Exception e) {
            LOG.error("Failed to get an Access Token from Google API: " + e.getMessage());
        }

        throw new OkHttpRequestException("Failed to get an Access Token from Google API");
    }

    private OAuth2UserDetails getGoogleUserDetails(String accessToken) {
        OkHttpClient client = new OkHttpClient();

        String url = GOOGLE_USER_DETAIL_URL + "?access_token=" + accessToken;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            return OAuth2UserDetails.builder()
                    .oAuth2ID(responseJson.get("id").asText())
                    .firstName(responseJson.get("given_name").asText())
                    .lastName(responseJson.get("family_name").asText())
                    .profilePictureURI(responseJson.get("picture").asText())
                    .email(responseJson.get("email").asText())
                    .oAuth2Provider("Google")
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to fetch user details from Google API: " + e.getMessage());
        }

        throw new OkHttpRequestException("Failed to fetch user details from Google API");
    }

    private String getGithubAccessToken(String token) {
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
                .url(GITHUB_ACCESS_TOKEN_URL)
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

            return responseJson.get("access_token").asText();
        } catch (Exception e) {
            LOG.error("Failed to get an Access Token from GitHub API: " + e.getMessage());
        }

        throw new OkHttpRequestException("Failed to get an Access Token from GitHub API");
    }

    private OAuth2UserDetails getGithubUserDetails(String accessToken) {
        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Authorization", "Bearer " + accessToken)
                .build();

        // Create the GET request
        Request request = new Request.Builder()
                .url(GITHUB_USER_DETAIL_URL)
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

            return OAuth2UserDetails.builder()
                    .oAuth2ID(responseJson.get("id").asText())
                    .firstName(responseJson.get("login").asText())
                    .email(getGithubUserEmail(accessToken))
                    .oAuth2Provider("GitHub")
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to fetch user details from GitHub API: " + e.getMessage());
        }

        throw new OkHttpRequestException("Failed to fetch user details from GitHub API");
    }

    private String getGithubUserEmail(String accessToken) {
        // Set the request headers
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Authorization", "Bearer " + accessToken)
                .build();

        // Create the GET request
        Request request = new Request.Builder()
                .url(GITHUB_USER_EMAIL_URL)
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
                    return jsonNode.get("email").asText();
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to fetch email from GitHub API: " + e.getMessage());
        }

        throw new OkHttpRequestException("Failed to fetch email from GitHub API");
    }
}