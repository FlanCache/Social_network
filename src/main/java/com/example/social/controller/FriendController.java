package com.example.social.controller;

import com.example.social.entity.FriendShip;
import com.example.social.services.FriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/friends")
@CrossOrigin(origins = "*")

public class FriendController {
    @Autowired
    FriendShipService friendShipService;
    @PostMapping("/addFriend/{userReceiverId}")
    public ResponseEntity<?> addFriend(@PathVariable int userReceiverId) {
        return friendShipService.addFriend(userReceiverId);
    }
    @PutMapping("/acceptFriend/{userSenderId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable int userSenderId) {
        return friendShipService.acceptFriendRequest(userSenderId);
    }
    @PutMapping("/declineFriend/{userSenderId}")
    public ResponseEntity<?> declineFriend(@PathVariable int userSenderId) {
        return friendShipService.declineFriendRequest(userSenderId);
    }
    @GetMapping("/FriendListId/{userId}")
    public List<Integer> getFriendListId(@PathVariable int userId) {
        return friendShipService.getListFriendId(userId);
    }
    @GetMapping("/friendship")
    public ResponseEntity<?> getFriendship() {
        return friendShipService.getAllFriendList();
    }
}
