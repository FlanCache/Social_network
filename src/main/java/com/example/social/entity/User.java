package com.example.social.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User_tb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_fullname")
    private String userFullName;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "user_phone")
    private  String userPhone;

    @Column(name = "user_otp")
    private String userOtp;

    @Column(name = "user_otp_created_at")
    private LocalDateTime userOtpCreatedTime;

    @Column(name = "user_birthday")
    private String userBirthday;

    @Column(name = "user_address")
    private String userAddress;

    @Column(name = "user_roles")
    private String userRoles;
}
