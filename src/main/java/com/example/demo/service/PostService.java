package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.util.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PostService {
  @Autowired
  private PostDao postDao;
  @Autowired
  private CommentDao commentDao;
  private static final int BLOCK_SIZE = 5;

  public PostDto.Pages findAll(int pageno, int pagesize) {
    int totalcount = postDao.count();
    List<Post> posts = postDao.findAll(pageno, pagesize);
    return Demo6Util.getPages(pageno, pagesize, BLOCK_SIZE, totalcount, posts);
  }

  public Map<String,Object> findByPno(int pno, String loginId) {
    // Consumer : 입력은 있고, 출력은 없다
    // Supplier : 입력은 없고, 출력은 있다 -> 예외를 발생
    Map<String,Object> post = postDao.findByPnoWithComments(pno).orElseThrow(()->new EntityNotFoundException("글을 찾을 수 없습니다"));
    if(loginId!=null && !post.get("writer").equals(loginId)) {
      postDao.increaseReadCnt(pno);
    }
    return post;
  }

  public Post write(PostDto.Write dto, String loginId) {
    Post post = dto.toEntity(loginId);
    postDao.save(post);
    return post;
  }

  public void update(PostDto.Update dto, String loginId) {
    // ex) 내용만 바꾸는 경우라서 dto의 title이 비어있다고 하자(검증안했어) -> update를 수행하면 title이 지워짐(∵title은 빈문자열 보내고 내용만 채워서 보냈으니까)
    // 그러면 검증을 수행하자~ 그러니까 dto의 title.content에 @NotEmpty를 걸자
    // 그러면 사용자가 변경할 때 반드시 제목, 내용을 모두 입력해야한다고? 어우..
    // update는 read가 일단 된 후에 할 수 있는거잖아. 읽기 화면에 출력했던 제목과 내용을 다시 받아오자. 변경한 항목은 변경한대로, 변경하지 않은 항목은 기존 내용을 받아오자

    // 글번호 읽어서 글이 없으면 예외
    Post post = postDao.findByPno(dto.getPno()).orElseThrow(()->new EntityNotFoundException("글을 찾을 수 없습니다"));  // optional은 이런 깔끔하게 예외 던지는게 있어서 이렇게 하면 됨
    // 글을 변경하려는 사용자가 글쓴이가 아니라면 예외
    if(!post.getWriter().equals(loginId))
      throw new JobFailException("잘못된 작업입니다");  // jobfailexception 새로 하나 만들거야~
    // 여기는 일반적인 자바코드라 optional처럼 던지는건 못해
    postDao.update(dto);  // entity로 안넘기고 그냥 두개만 넘기면 되니까 넘기자
  }

  public void delete(Integer pno, String loginId) {
    // 글 읽어와
    Post post = postDao.findByPno(pno).orElseThrow(()->new EntityNotFoundException("글을 찾을 수 없습니다"));
    // 비밀번호 확인해
    if(!post.getWriter().equals(loginId))
      throw new JobFailException("잘못된 작업입니다");
    // 글 삭제
    postDao.delete(pno);
  }
}












