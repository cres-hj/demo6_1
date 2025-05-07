package com.example.demo;

import com.example.demo.exercise.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmpDaoTest {
  @Autowired
  private EmpDao empDao;

  // @Test
  // test하는 함수는 항상 public void
  public void findJob1Test() {
    empDao.findJob1().forEach(a->System.out.println(a));  // lambda(화살표함수) 사용
    assertEquals(5, empDao.findJob1().size());  // assertEquals까지 해줘야 테스트했다 할 수 있음! 저 위까지만 하면 안돼
    // assert가 없는 test는 반드시 성공하기 때문에 test할땐 assert 꼭 넣어줘야해


  }
  // @Test
  public void findEmpByDeptno1Test() {
    empDao.findEmpByDeptno1(10).forEach(a->System.out.println(a));
    assertEquals(3, empDao.findEmpByDeptno1(10).size());
  }

  // @Test
  public void findEmpByCommIsNotNull1Test() {
    empDao.findEmpByCommIsNotNull1().forEach(a->System.out.println(a));
    assertEquals(4, empDao.findEmpByCommIsNotNull1().size());
  }

  // @Test
  public void findJob2Test() {
    assertEquals(5, empDao.findJob2().size());
  }

  // @Test
  public void findEmpBySalLessThen2Test() {
    empDao.findEmpBySalLessThen2(5000).forEach(a->System.out.println(a.get("JOB")));
    // 만약 a.get() 쓸거면 ()안에 무조건 대문자로 적어야해! ∵logback 파일 보면 다 대문자로 나오잖아 맞춰
    // Entity로 리턴을 받았다면 a.getJob();  장점: 오타는 절대 발생x  /  단점: 클래스를 만들어야해
    // Map으로 리턴을 받으면 a.get("JOB");  장점: 클래스를 만들 필요x(간편해)  /  단점: 오타나면 힘들어~
    // => 클래스를 만들어뒀다면 Entity로 받으면 되겠지. 근데 join하는게 많을 경우 매번 클래스를 만들 수는 없잖아 이럴때 map
    assertEquals(14, empDao.findEmpBySalLessThen2(5000).size());
  }

  // @Test
  public void findEmpByDnameTest() {
    assertEquals(6, empDao.findEmpByDname("SALES").size());
  }

  // @Test
  public void findEmpByEmpnoTest() {
    assertEquals(true, empDao.findEmpByEmpno(7369).isPresent());
    assertEquals(true, empDao.findEmpByEmpno(9000).isEmpty());
  }

  // @Test
  public void findPositionFromEmp2Test() {
    empDao.findPositionFromEmp2().forEach(a->System.out.println(a));
    assertEquals(5, empDao.findPositionFromEmp2().size());
  }

  // @Test
  public void findEmp2ByEmpTypeTest() {
    assertEquals(10, empDao.findEmp2ByEmpType("정규직").size());
    assertEquals(4, empDao.findEmp2ByEmpType("계약직").size());
    assertEquals(3, empDao.findEmp2ByEmpType("수습직").size());
    assertEquals(3, empDao.findEmp2ByEmpType("인턴직").size());
  }

  @Test
  public void findEmp2ByEmpnoTest() {
    System.out.println(empDao.findEmp2ByEmpno(19900101));
    assertEquals(true, empDao.findEmp2ByEmpno(19900101).isPresent());
    assertEquals(true, empDao.findEmp2ByEmpno(19990102).isEmpty());
  }



}
