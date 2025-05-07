package com.example.demo.exercise;

import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface EmpDao {
  // emp에서 job을 출력하는 쿼리와 메소드  ! distinct좀 잊지마 + 정렬
  @Select("select distinct job from emp order by job asc")
  List<String> findJob1();
  // 이게 정상작동하는지 확인해야지 -> /test/패키지명/EmpDaoTest

  // 부서번호를 입력받아 그 부서의 사원을 출력
  @Select("select * from emp where deptno=#{deptno}")
  List<Map<String, Object>> findEmpByDeptno1(int deptno);  // Map: python의 dictionary와 동일한거(이름:값)

  // comm를 수령하는 사원 출력
  @Select("select * from emp where comm is not null")
  List<Map<String, Object>> findEmpByCommIsNotNull1();


  // 여기서부턴 xml에 쓸거야
  List<String> findJob2();

  // 일정 급여 이하를 출력: select * from emp where sal<=?
  List<Map<String, Object>> findEmpBySalLessThen2(int sal);

  List<Map<String, Object>> findEmpByDname(String dname);

  // 사번으로 검색: 하나만 나오니까 Map + 하나일땐 값이 안나올 수 있으니까 Optional도
  Optional<Map<String, Object>> findEmpByEmpno(int empno);

  // emp2에서 직위를 출력(position)
  List<String> findPositionFromEmp2();

  // emp2에서 emp_type으로 검색해 사원을 출력
  List<Map<String, Object>> findEmp2ByEmpType(String empType);

  // emp2에서 사번을 입력받아 사원을 출력
  Optional<Map<String, Object>> findEmp2ByEmpno(int empno);
}
