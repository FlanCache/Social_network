package com.example.social.entity;

import com.example.social.common.RelationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Friendship_tb")
public class FriendShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "user_sender_id")
    private  int userSenderId;
    @Column(name = "user_receiver_id")
    private int userReceiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_ship")
    private RelationStatus relationShip;

    @Column(name = "create_time")
    private LocalDateTime relationCreateTime;
}
