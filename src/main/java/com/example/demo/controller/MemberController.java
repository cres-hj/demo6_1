package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.*;

// MVC 방식은 html로 응답 vs REST 방식은 데이터 + 상태코드로 응답
// @Controller는 MVC와 REST 방식을 모두 지원
// @RestController는 REST 방식만 지원(ModelAndView 리턴 불가)
@Validated
@RestController
public class MemberController {
  @Autowired
  private MemberService service;

  @PreAuthorize("isAnonymous()")  // 아이디 확인하려면 비로그인이여야해
  @Operation(summary= "아이디 확인", description="아이디가 사용가능한 지 확인")
  @GetMapping("/api/members/check-username")
  public ResponseEntity<String> checkUsername(@ModelAttribute @Valid MemberDto.UsernameCheck dto, BindingResult br) {
    boolean result = service.checkUsername(dto);
    if(result)
      return ResponseEntity.ok("사용가능합니다");
    return ResponseEntity.status(HttpStatus.CONFLICT).body("사용중인 아이디입니다");
  }

  @PreAuthorize("isAnonymous()")  // 회원가입하려면 비로그인이여야해
  @Operation(summary="회원가입", description="회원가입 및 프로필 사진 업로드")
  @PostMapping(value="/api/members/new", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Member> signup(@ModelAttribute @Valid MemberDto.Create dto, BindingResult br) {
    Member member = service.signup(dto);
    return ResponseEntity.status(200).body(member);
  }

  @PreAuthorize("isAnonymous()")  // 아이디 찾으려면 비로그인이여야해
  @Operation(summary="아이디 찾기", description="가입한 이메일로 아이디를 찾는다")
  @GetMapping("/api/members/username")
  public ResponseEntity<String> searchUsername(@RequestParam @Email String email) {
    Optional<String> result = service.searchUseraname(email);
    if(result.isPresent())
      return ResponseEntity.ok(result.get());
    return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자를 찾을 수 없습니다");
  }

  @PreAuthorize("isAnonymous()")  // 임시 비밀번호 발급하려면 비로그인이여야해
  @Operation(summary="임시비밀번호 발급", description="아이디와 이메일로 임시비밀번호를 발급")
  @PutMapping("/api/members/password")
  public ResponseEntity<String> getTemporaryPassword(@ModelAttribute @Valid MemberDto.GeneratePassword dto, BindingResult br) {
    Optional<String> 임시비밀번호 = service.getTemporaryPassword(dto);
    if(임시비밀번호.isPresent())
      return ResponseEntity.ok(임시비밀번호.get());
    return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자를 찾을 수 없습니다");
  }

  // 내 정보 보기
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "내 정보 보기", description = "내 정보 보기")
  @GetMapping("/api/members/member")
  public ResponseEntity<MemberDto.Read> read(Principal principal) {
    MemberDto.Read dto = service.read(principal.getName());// 로그인이 되어있기때문에 principal은 null이 되지 않아 -> 걍 내보내
    return ResponseEntity.ok(dto);
  }

  // 비밀번호 변경
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "비밀번호 변경", description = "기존 비밀번호, 새 비밀번호로 비밀번호 변경")
  @PatchMapping("/api/members/password")  // 변경하는거니까 put으로 하려다가 위쪽에 주소 겹치는 putmapping 있어서 여기는 patch로~(변경은 put/patch)
  public ResponseEntity<String> changePassword(@ModelAttribute @Valid MemberDto.PasswordChange dto, BindingResult br, Principal principal) {
    boolean result = service.changePassword(dto, principal.getName());
    if(result)
      return ResponseEntity.ok("비밀번호 변경");
    else
      return ResponseEntity.status(409).body("비밀번호 변경 실패");
  }

  // 회원 탈퇴
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "회원 탈퇴", description = "로그아웃 시킨 후 회원탈퇴")
  @DeleteMapping("/api/members/member")  // 내정보보기랑 주소 같아도 mapping 방법 다르니까 알아서 잘 됨
  public ResponseEntity<String> resign(Principal principal, HttpSession session) {
    service.resign(principal.getName());
    session.invalidate();
    return ResponseEntity.ok("회원 탈퇴");

  }
}
