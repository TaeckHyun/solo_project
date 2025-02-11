package com.springboot.member.controller;

import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/members")
public class MemberController {
    @PostMapping
    public ResponseEntity postMember(@RequestBody MemberPostDto memberPostDto) {

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity patchMember(@RequestBody MemberPatchDto memberPatchDto) {
        return new ResponseEntity(HttpStatus.OK);
    }
}