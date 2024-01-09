package com.sefaunal.umbrellaauth.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-09-17
 */
@Data
@Document
public class User implements UserDetails {
    @Id
    private String ID;

    private String username;

    private String firstName;

    private String lastName;

    private String role;

    private String email;

    private String password;

    private String profilePictureURI;

    private String mfaSecret;

    private boolean mfaEnabled;

    private boolean oauth2Account;

    private String oauth2ID;

    private String oauth2Provider;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
