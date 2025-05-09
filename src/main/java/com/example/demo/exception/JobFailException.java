package com.example.demo.exception;

import lombok.*;

// 어떤 이유든 작업 실패하면 다 이거로 때려박겠다
// ex) 글 update -> 글이 없을수도, 글쓴이가 아닐수도 ... -> boolean 안쓰고 걍 예외로 처리하겠다
@AllArgsConstructor
@Getter
public class JobFailException extends RuntimeException {
  private String message;
}
