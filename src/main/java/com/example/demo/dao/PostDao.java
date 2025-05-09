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
}








