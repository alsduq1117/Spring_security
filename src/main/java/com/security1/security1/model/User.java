package com.security1.security1.model;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;


@Entity
@Data
public class User {
    @Id // pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; //Role_User, Role_Admin

    private String provider;  //google
    private String providerId;  //google sub 번호
    @CreationTimestamp
    private Timestamp createDate;
}
