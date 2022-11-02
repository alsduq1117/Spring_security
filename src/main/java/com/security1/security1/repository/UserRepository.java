package com.security1.security1.repository;

import com.security1.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


// CRUD 함수를 JpaRepository가 들고있음
// @Repository라는 어노테이션이없어도 IoC 된다. 이유는 JPARepository를 상속했기때문에..
public interface UserRepository extends JpaRepository<User, Integer> {

    //findBy 규칙 -> Username 문법
    //select * from user where username = 1?
    public User findByUsername(String username); //Jpa 쿼리 메서드

    //select * from user where email=?
    //public User findByEmail();
}
