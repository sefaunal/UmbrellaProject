package com.sefaunal.umbrellasecurity.Model;

import lombok.Builder;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-12-28
 */
@Data
@Builder
public class OAuth2UserDetails {
    private String oAuth2ID;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePictureURI;

    private String oAuth2Provider;
}
