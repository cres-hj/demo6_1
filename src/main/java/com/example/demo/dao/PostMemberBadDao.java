package com.example.demo.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PostMemberBadDao {
  // 비추여부 확인
  @Select("select count(*) from posts_members_bad where pno=#{pno} and username=#{username} and rownum = 1")
  boolean existByUsernameAndPno(int pno, String username);
  // 테이블 추가
  @Insert("insert into posts_members_bad values(#{pno}, #{username})")
  int save(int pno, String username);
  // 테이블 삭제
  @Delete("delete from posts_members_bad where pno=#{pno} and username=#{username}")
  int remove(int pno, String username);
}
