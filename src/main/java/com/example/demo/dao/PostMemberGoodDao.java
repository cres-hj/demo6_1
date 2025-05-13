package com.example.demo.dao;


import org.apache.ibatis.annotations.*;

@Mapper
public interface PostMemberGoodDao {
  // 메소드 4개 만들어
  // 추천수 +1 / 추천수 가져와 => postDto랑 관련된거니까 postDao에 만들거야!

  // posts_members_good 기록
  @Insert("insert into posts_members_good values(#{pno}, #{username})")
  int save(int pno, String username);

  // 추천 여부 확인
  @Select("select count(*) from posts_members_good where pno=#{pno} and username=#{username} and rownum=1")
  boolean existByPnoAndUsername(int pno, String username);
  // 한명의 유저가 여러글에 추천을 남기기 가능 + 하나의 글에 여러명의 유저가 추천 남기기 가능
  // => pno, username 모두 가져와야해
  // count(*)로 존재 여부 확인하는구나
  // 0이 아니면 참! 0이면 ㅁ거짓으로 리턴

  // 비추천 관련
  // 추천테이블에서 삭제
  @Delete("delete from posts_members_good where pno=#{pno} and username=#{username}")
  int remove(int pno, String username);



}
