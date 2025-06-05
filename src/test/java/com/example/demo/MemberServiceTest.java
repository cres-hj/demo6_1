package com.example.demo;

import com.example.demo.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class MemberServiceTest {
  @Autowired
  private MemberService service;

  @Test
  // 메일보내기
  public void mail() {
    String link = "<a href='#'>링크</a>";
    service.sendMail("cres.jhj@gmail.com", "cres.jhj@gmail.com", "제목입니다", link );
  }
}
