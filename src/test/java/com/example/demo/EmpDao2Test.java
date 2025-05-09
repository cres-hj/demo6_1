package com.example.demo;

import com.example.demo.exercise.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class EmpDao2Test {
  @Autowired
  private EmpDao2 empDao2;

  @Test
  public void findByDeptnoTest() {
    System.out.println(empDao2.findByDeptno(10));
  }
}
