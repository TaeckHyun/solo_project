package com.springboot.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdAndEmailPrincipal {
    private final String email;
    private final Long memberId;
}
