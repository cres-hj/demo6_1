package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.validation.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

@Service
public class MemberService {
  @Autowired
  private MemberDao memberDao;
  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  private JavaMailSender mailSender;  // 메일 보내는거

  // 메일 보내는 코드: 사실상 정해져있어
  public void sendMail(String 보낸이, String 받는이, String 제목, String 내용) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();  // mime: 이메일 형식 정도로 생각해
    try {
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
      helper.setFrom(보낸이); // 누가보냈니. 그런데 이건 안바뀜 적어둔 메일 주소가 보낸이가 됨
      helper.setTo(받는이);
      helper.setSubject(제목);
      // 두번째 파라미터는 html 활성화 여부 ex)
      // <a href='aaa'>링크</a> 이렇게 적었을 때 true면 링크로 날아가고 false면 걍 저 글자가 날아가
      // 우리는 회원가입 링크를 보내야하기 때문에 html 설정 true로 활성화해야해 
      helper.setText(내용, true);
    } catch(MessagingException e) {
      e.printStackTrace();
    }
    mailSender.send(mimeMessage);
  }

  public boolean checkUsername(MemberDto.UsernameCheck dto) {
    return !memberDao.existsByUsername(dto.getUsername());
  }

  public Member signup(MemberDto.Create dto) {
    // 1. 비밀번호 암호화
    String encodedPassword = encoder.encode(dto.getPassword());
    // 2. 프사 업로드했으면 인코딩, 업로드 안했으면 기본프사 저장
    MultipartFile profile = dto.getProfile();
    // 프론트에 <input type='file' name='profile'>이 없다면 profile이 null이 된다
    // 이 경우 profile.isEmpty()는 null pointer exception(NPE)

    boolean 프사_존재 = profile !=null && !profile.isEmpty();

    String base64Image = "";
      try {
        // 사용자가 업로드한 이미지를 base64로 바꾸는 함수는 실패 가능
        if(프사_존재) {
          base64Image = Demo6Util.convertToBase64(profile);
        } else {
          base64Image = Demo6Util.getDefaultBase64Profile();  // 이건 서버쪽에서 사진 넣어둔거라 실패힐 확률이 없지
        }
      } catch(IOException e) {
        e.printStackTrace();
      }

    // 3. 암호화된 비밀번호, base64이미지를 가지고 dto를 member로 변환
    Member member = dto.toEntity(encodedPassword, base64Image);
    memberDao.save(member);
    return member;
  }

  public Optional<String> searchUseraname(String email) {
    return memberDao.findUsernameByEmail(email);
  }

  public Optional<String> getTemporaryPassword(MemberDto.GeneratePassword dto) {
    // 1. 아이디와 이메일이 일치하는 사용자가 있는 지 확인
    // 2. 사용자가 없을 경우 비어있는 Optional을 리턴 -> 컨트롤러에서 if문으로 처리
    // 3. 있을 경우 임시비밀번호를 생성
    // 4. 임시비밀번호를 암호화해서 업데이트
    // 5. 비밀번호를 Optional로 리턴
    boolean isExist = memberDao.existsByUsernameAndEmail(dto);
    if(!isExist)
      return Optional.empty();
    String newPassword = RandomStringUtils.secure().nextAlphanumeric(20);
    memberDao.updatePassword(dto.getUsername(), newPassword);
    return Optional.ofNullable(newPassword);
  }

  public MemberDto.Read read(String loginId) {
    Member member = memberDao.findByUsername(loginId);
    return member.toRead();
  }

  public boolean changePassword(MemberDto.PasswordChange dto, String loginId) {
    // 기존의 암호화된 비밀번호를 읽어와 비밀번호가 맞는지 확인 -> 틀리면 false
    String encodedPassword = memberDao.findPasswordByUsername(loginId);
    if(!encoder.matches(dto.getCurrentPassword(), encodedPassword))
      return false;
    // 비밀번호가 일치한 경우 새 비밀번호로 업데이트
    return memberDao.updatePassword(loginId, encoder.encode(dto.getNewPassword()))==1;
  }

  public void resign(String loginId) {
    memberDao.delete(loginId);
  }
}







