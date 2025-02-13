package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberResponseDto {
    private long memberId;
    private String email;
    private String name;
    private String phone;
    private List<String> roles;
    private Member.Status status;
}
