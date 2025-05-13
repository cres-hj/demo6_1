package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PostService {
  @Autowired
  private PostDao postDao;
  @Autowired
  private CommentDao commentDao;
  @Autowired
  private PostMemberGoodDao postMemberGoodDao;
  @Autowired
  private PostMemberBadDao postMemberBadDao;

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

  public int 추천(int pno, String loginID) {
    // 비로그인은 추천x(@PreAutorize로 필터링해서 여기까진 안옴)
    // 본인이 작성한 글은 추천할 수 없다
    // 본인이 작성하지 않은 글은 추천할 수 있다

    // 0. 글이 없으면 예외처리
    // 1. 본인이 작성한 글이면 예외처리해서 오류메시지 날리자
    // 2. 이미 추천한 글이면 예외처리
    // 3. 추천하지 않은 글이면 추천 후 새로운 추천수를 리턴
    Post post = postDao.findByPno(pno).orElseThrow(()->new EntityNotFoundException("글을 찾을 수 없습니다"));  // 글이 존재하는지 확인
    if(post.getWriter().equals(loginID))
      throw new JobFailException("자신의 글은 추천할 수 없습니다");

    boolean 추천했니 = postMemberGoodDao.existByPnoAndUsername(pno, loginID);
    if(추천했니)
      throw new JobFailException("이미 추천했습니다");

    postMemberGoodDao.save(pno, loginID);  // 추천했으니까 posts_members_good에 추가
    postDao.increaseGoodCnt(pno);
    return postDao.findGoodCntByPno(pno).get();  // findgoodcntbypno가 Optional인데 지금 추천메소드의 리턴타입이 int라 .get()으로 값 꺼내는거야
  }

  public int 비추천(int pno, String loginId) {
    // 글 없으면 예외처리
    Post post = postDao.findByPno(pno).orElseThrow(()->new EntityNotFoundException("글을 찾을 수 없습니다"));
    // 본인이 작성한 글이면 비추천 불가
    if(post.getWriter().equals(loginId))
      throw new JobFailException("자신의 글은 비추천할 수 없습니다");
    // 비추천 했는지 유무 확인
    boolean 비추천여부 = postMemberBadDao.existByUsernameAndPno(pno, loginId);
    boolean 추천여부 = postMemberGoodDao.existByPnoAndUsername(pno, loginId);
    // 추천하지 않았으면 비추테이블 추가 + 비추+1
    if(!비추천여부) {
      // 만약 추천을 했으면 추천테이블에서 삭제 + 추천-1
      if(추천여부) {
        postMemberGoodDao.remove(pno, loginId);
        postDao.decreaseGoodCnt(pno);
      }
      postMemberBadDao.save(pno, loginId);
      postDao.increaseBadCnt(pno);
    }
    else {// 추천했으면 비추테이블 삭제 + 비추-1
      postMemberBadDao.remove(pno, loginId);
      postDao.decreaseBadCnt(pno);
    }
    // 새로운 비추천 수 리턴
    return postDao.findBadCntByPno(pno).get();
    // findbadcntbypno 리턴타입이 optional인데 지금 메소드의 리턴타입은 int니까 get()으로 값 꺼내주기
  }
}












