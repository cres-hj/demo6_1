package com.example.demo.dao;

import com.example.demo.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface CommentDao {
  @Select("Select * from comments where pno=#{pno}")
  List<Comment> findByPno(int pno);
}
