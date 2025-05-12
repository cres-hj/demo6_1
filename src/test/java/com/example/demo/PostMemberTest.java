package com.example.demo;

import com.example.demo.dao.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class PostMemberTest {
  @Autowired
  private PostMemberGoodDao postMemberGoodDao;
  @Autowired
  private PostDao postDao;

}
