package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.*;

// @Validated가 있으면 스프링 검증, 없으면 자바 표준 검증 수행
// 스프링 검증이 조금 더 간략해
@Validated
@RestController
public class PostController {
  @Autowired
  private PostService service;

  @Operation(summary="페이징", description="기본 페이지번호 1, 페이지크기 10으로 페이징")
  @GetMapping("/posts")
  public ResponseEntity<PostDto.Pages> findAll(@RequestParam(defaultValue="1") int pageno, @RequestParam(defaultValue="10") int pagesize) {
    // requestparam 쓰는 이유: defaultvalue 주려고
    return ResponseEntity.ok(service.findAll(pageno, pagesize));
  }

  @Operation(summary="글읽기", description="글읽기")
  @GetMapping("/posts/post")
  public ResponseEntity<Map<String,Object>> findByPno(@RequestParam int pno, Principal principal) {
    String loginId = principal==null? null : principal.getName();
    return ResponseEntity.ok(service.findByPno(pno, loginId));
  }

  @Operation(summary = "글쓰기")
  // @PreAuthorize("isAuthenticated()") //<- 로그인 되어있으면 글쓰기 가능
  @Secured("ROLE_USER") // <- 로그인했고, user여야 글쓰기 가능. 실제 개발이면 이거 쓰는게 더 나을거야
  @PostMapping("/posts/new")
  public ResponseEntity<Post> write(@ModelAttribute @Validated PostDto.Write dto, BindingResult br, Principal principal) {
    // principal: 사용자 아이디 꺼내는 친구.
    // principal이라는 객체는 로그인하지 않았으면 null. 읽기쪽에서는 null 체크했어(secured 안걸려있으니까). 근데 지금은 secured 어노테이션 걸려있어 = 로그인해야 글쓰기 가능 = 굳이 principal null 검사안해도 ㄱㅊ
    Post post = service.write(dto, principal.getName());
    return ResponseEntity.ok(post);
  }

  @Secured("ROLE_USER")
  @PostMapping("/posts/post")
  @Operation(summary = "글변경", description = "글번호로 제목과 내용 변경")
  public ResponseEntity<String> update(@ModelAttribute @Valid PostDto.Update dto, BindingResult br, Principal principal) {
    // 글쓴 사람이 맞나 확인해야하니까 principal
    service.update(dto, principal.getName());
    return ResponseEntity.ok("글을 변경했습니다");
  }

  @Secured("ROLE_USER")
  @Operation(summary = "삭제")
  @DeleteMapping("/posts/post")
  public ResponseEntity<String> delete(@RequestParam @NotNull Integer pno, Principal principal) {
    // 글 번호 받아와야지 -> requestparam + pno 안비었는지 할거면 @Notnull로 검증. 검증 귀찮으면 dto 만들면 됨;;
    // 원래 자바 검증은 파라미터 검증 안되고 스프링에서 확장한거야 -> 맨 위에 validated 안적으면 notnull로 검증 못함(notnull은 스프링검증)
    service.delete(pno, principal.getName());
    return ResponseEntity.ok("글을 삭제했습니다");
  }

  @Secured("ROLE_USER")
  @PutMapping("/posts/good")
  @Operation(summary = "글추천", description = "이미 추천한 글은 재추천 불가")
  public ResponseEntity<Integer> 추천(@RequestParam @NotNull Integer pno, Principal principal) {
    int newGoodCnt = service.추천(pno, principal.getName());
    return ResponseEntity.ok(newGoodCnt);
  }

  @Secured("ROLE_USER")
  @PutMapping("/posts/bad")
  @Operation(summary = "비추천", description = "이미 비추천했다면 비추천 취소 / 추천한 글을 비추천하면 추천 취소 후 비추천")
  public ResponseEntity<Integer> 비추천(@RequestParam @NotNull Integer pno, Principal principal) {
    int newBadCnt = service.비추천(pno, principal.getName());
    return ResponseEntity.ok(newBadCnt);
  }
}
