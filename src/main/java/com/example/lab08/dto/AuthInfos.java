package com.example.lab08.dto;

import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.security.Principal;

public record AuthInfos(Principal principal, Authentication auth) {
    @Override
    public String toString() {
        return "AuthInfos{" +
                "principal=" + principal.getName() +
                ", auth=" + StringUtils.collectionToDelimitedString(auth.getAuthorities(), ",")+
                '}';
    }
}
