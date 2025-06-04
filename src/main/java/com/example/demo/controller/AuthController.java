package com.example.demo.controller;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class AuthController {  // 0604 추가: 로그인 상태 저장용
  // 프론트엔드에서 현재 로그인 상태를 물어오면 응답
  // 로그인한 경우: 200 + 로그인 아이디
  // 비로그인: 409 + null
  @GetMapping(path = "/api/auth/check")
  public ResponseEntity<Map<String, Object>> checkLogin(Principal principal) {

    if(principal!=null)
      return ResponseEntity.ok(Map.of("username", principal.getName()));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
  }
}
