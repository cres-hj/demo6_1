package com.example.demo.dto;

import com.example.demo.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.*;
import java.util.*;

// PostDto는 Dto들을 담는 클래스다 -> Dto 클래스 개수 줄여 PostDto.Pages, PostDto.Create....
public class PostDto {
  // 페이징 출력 DTO
  @Data
  @AllArgsConstructor
  public static class Pages {
    private int prev;
    private int start;
    private int end;
    private int next;
    private int pageno;
    private List<Post> posts;
  }

  // 글을 작성하는 DTO
  @Data
  public static class Write {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;

    public Post toEntity(String loginId) {
      return Post.builder().title(title).content(content).writer(loginId).build();
    }
  }

  // 기타 DTO들...
  @Data
  public static class Update{
    @NotNull
    private Integer pno;  // notnull 쓰려면 Integer 써야해 왜?
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    // 검증은 필드 하나씩 검증하는데 업데이트이 경우는 하나만 채워져있으면 되잖아 이건아직 검증 못해 -> 내가 짜야해
  }
}
