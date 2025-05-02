package com.example.demo.dao;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import jakarta.validation.constraints.*;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Mapper
public interface MemberDao {
  @Select("select count(*) from members where username=#{username}")
  boolean existsByUsername(String username);

  int save(Member member);

  @Select("select username from members where email=#{email} and rownum=1")
  Optional<String> findUsernameByEmail(String email);

  @Select("select count(*) from members where username=#{dto.username} and email=#{dto.email} and rownum=1")
  boolean existsByUsernameAndEmail(@Param("dto") MemberDto.GeneratePassword dto);

  @Update("update members set password=#{newPassword} where username=#{username}")
  int updatePassword(String username, String newPassword);

  @Select("select username, password, role, is_lock from members where username=#{username}")
  Optional<Member> loadLoginData(String username);

  @Select("select failed_attempts from members where username=#{username}")
  Optional<Integer> 로그인실패횟수(String username);  // int는 null이 될 수 없어서 Integer
  // 아이디가 있으면 숫자가 올거고 아이디가 없으면 값이 없어

  @Update("update members set failed_attempts=failed_attempts+1 where username=#{username}")
  int 로그인실패횟수증가(String username);

  @Update("update members set is_Lock=1 where username=#{username}")
  int 계정블록(String username);

  @Update("update members set failed_attempts=0 where username=#{username}")
  int reset로그인실패횟수(String loginId);

  @Select("select username, email, profile, join_day, levels from members where username=#{loginId}")
  Member findByUsername(String loginId);

  @Select("select password from members where username=#{loginId}")
  String findPasswordByUsername(String loginId);

  @Delete("delete from members where username=#{loginId}")
  int delete(String loginId);

  // 로그인은 swagger로 테스트 못해(SeruciryConfig에 저렇게 만들어놔서)
}








