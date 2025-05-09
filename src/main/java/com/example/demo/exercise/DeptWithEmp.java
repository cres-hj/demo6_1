package com.example.demo.exercise;

import lombok.*;

import java.util.*;

@Data
public class DeptWithEmp {
  private int deptno;
  private String dname;
  private String loc;
  private List<Emp> emps;
}
