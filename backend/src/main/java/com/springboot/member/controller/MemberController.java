package com.springboot.member.controller;

import com.springboot.auth.utils.IdAndEmailPrincipal;
import com.springboot.auth.utils.MemberDetailsService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
@Validated
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @PostMapping
    public ResponseEntity postMember(@RequestBody @Valid MemberPostDto memberPostDto) {
        // Mapper를 통해 받은 Dto 데이터 Member로 변환
        Member member = memberMapper.memberPostDtoToMember(memberPostDto);

        Member createdMember = memberService.createMember(member);

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId,
                                      @RequestBody @Valid MemberPatchDto memberPatchDto,
                                      @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        memberPatchDto.setMemberId(memberId);

        Member member = memberMapper.memberPatchDtoToMember(memberPatchDto);

        Member updateMember = memberService.updateMember(member, idAndEmailPrincipal.getMemberId());

        return new ResponseEntity(
                new SingleResponseDto<>(memberMapper.memberTomemberResponseDto(updateMember)),
                HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") @Positive long memberId,
                                    @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {

        Member member = memberService.findMember(memberId, idAndEmailPrincipal.getMemberId());

        return new ResponseEntity(
                new SingleResponseDto<>(memberMapper.memberTomemberResponseDto(member)),
                HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getMembers(@RequestParam int page,
                                     @RequestParam int size) {
        Page<Member> pageMembers = memberService.findMembers(page, size);
        List<Member> members = pageMembers.getContent();
        return new ResponseEntity(
                new MultiResponseDto<>(memberMapper.membersToMemberResponseDtos(members),
                pageMembers), HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive long memberId,
                                       @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        memberService.deleteMember(memberId, idAndEmailPrincipal.getMemberId());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}