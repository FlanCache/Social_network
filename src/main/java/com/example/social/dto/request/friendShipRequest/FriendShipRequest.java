package com.example.social.dto.request.friendShipRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipRequest {
    private int userSenderId;
    private int userReceiverId;
}
