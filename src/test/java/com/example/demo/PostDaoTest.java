package com.example.demo;

import com.example.demo.dao.*;
import com.example.demo.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class PostDaoTest {
  @Autowired
  private PostDao postDao;

  //@Test
  public void 글때려박기() {
    for(int i=0; i<1; i++) {
      Post p = Post.builder().title(i+"번째글").content("내용없음").writer("spring").build();
      postDao.save(p);
    }
  }

  //@Test
  public void 페이징쿼리테스트() {
    // pageno가 1이므로 123~114번까지 출력
    postDao.findAll(1,10).forEach(post->System.out.println(post.getPno()));

    // pageno가 13이면 3~1번까지 출력
    postDao.findAll(13,10).forEach(post->System.out.println(post.getPno()));
  }

  @Test
  public void findByPnoWithCommentsTest() {
    System.out.println(postDao.findByPnoWithComments(10));
  }
}
