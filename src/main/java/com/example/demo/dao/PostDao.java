package com.example.demo.dao;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import jakarta.validation.constraints.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface PostDao {
  int save(Post post);

  List<Post> findAll(int pageno, int pagesize);

  int count();

  int increaseReadCnt(int pno);

  Optional<Post> findByPno(int pno);

  Optional<Map<String,Object>> findByPnoWithComments(int pno);

  @Update("update posts set title=#{title}, content=#{content} where pno=#{pno}")
  int update(PostDto.Update dto);

  @Delete("delete from posts where pno=#{pno}")
  int delete(Integer pno);

  // 추천수 +1
  @Update("update posts set good_cnt=good_cnt+1 where pno=#{pno}")
  int increaseGoodCnt(int pno);

  // 추천수 -1
  @Update("update posts set good_cnt=good_cnt-1 where pno=#{pno}")
  int decreaseGoodCnt(int pno);

  // 추천수 가져와
  @Select("select good_cnt from posts where pno=#{pno}")
  Optional<Integer> findGoodCntByPno(int pno);

  // 비추천 수 가져와
  @Select("select bad_cnt from posts where pno=#{pno}")
  Optional<Integer> findBadCntByPno(int pno);
  // 비추 +1
  @Update("update posts set bad_cnt = bad_cnt + 1 where pno=#{pno}")
  int increaseBadCnt(int pno);  // +1 하기 전에 비추천유무 확인하니까 이 때 로그인 거르기 가능(애초에 컨트롤러에 preauthorize 걸면 됨)
  // 비추 -1
  @Update("update posts set bad_cnt=bad_cnt-1 where pno=#{pno}")
  int decreaseBadCnt(int pno);
}








