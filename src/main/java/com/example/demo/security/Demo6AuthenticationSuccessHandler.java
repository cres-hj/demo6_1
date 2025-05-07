package com.example.demo.security;

import com.example.demo.dao.*;
import com.example.demo.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;

import java.io.*;

@Component
public class Demo6AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  @Autowired
  private MemberDao memberDao;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    // 로그인 실패 횟수 리셋
    String loginId = authentication.getName();
    memberDao.reset로그인실패횟수(loginId);
    // 세션 확인 (/login에서는 로그인했다뜨고 /내정보보기 에서는 principal이 null뜰 때 확인용)
    // 내정보보기에도 세션 찍을거야 (지금 저렇게 뜨는 원인이 세션이 바뀌어서 그렇다고 예상해서)
    HttpSession session = request.getSession();
    System.out.println("로그인 했을 때 세션 번호: " + session.getId());

    ResponseUtil.sendJsonResponse(response, 200, loginId);
  }
}
