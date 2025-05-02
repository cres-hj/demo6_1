package com.example.demo.dto;

import com.example.demo.entity.*;
import lombok.*;

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
  public static class Create {

  }

  // 기타 DTO들...
}
