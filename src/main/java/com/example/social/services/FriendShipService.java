package com.example.social.services;

import com.example.social.entity.FriendShip;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendShipService {
    ResponseEntity<?> addFriend(int userReceiverId);

    FriendShip checkFriendShip(int userSenderId, int userReceiverId);

    FriendShip checkFriendShipPresent(int userSenderId, int userReceiverId);

    Boolean checkIsFriend(int userSenderId, int userReceiverId);

    ResponseEntity<?> acceptFriendRequest(int userSenderId);

    ResponseEntity<?> declineFriendRequest(int userSenderId);

    List<Integer> getListFriendId(int userId);

    ResponseEntity<?> getAllFriendList();
}
