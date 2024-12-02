package com.example.social.servicesImpl;

import com.example.social.common.CommonMessage;
import com.example.social.common.RelationStatus;
import com.example.social.dto.request.friendShipRequest.FriendShipRequest;
import com.example.social.dto.response.FriendShipResponse.FriendShipResponse;
import com.example.social.entity.FriendShip;
import com.example.social.entity.User;
import com.example.social.repository.FriendShipRepository;
import com.example.social.repository.UserRepository;
import com.example.social.services.FriendShipService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class FriendShipServiceImpl implements FriendShipService {
    @Autowired
    FriendShipRepository friendShipRepository;
    @Autowired
    UserRepository userRepository;
    @Override
    public ResponseEntity<?> addFriend(int userReceiverId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FriendShipResponse friendShipResponse = new FriendShipResponse();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username)
                    .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            User userReceiver = userRepository.findByUserId(userReceiverId)
                    .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));

            // Kiểm tra nếu người dùng tự gửi yêu cầu kết bạn cho chính mình
            if (currentUser.getUserId() == userReceiverId) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }

            // Tìm bản ghi FriendShip hiện tại
            FriendShip existingFriendShip = friendShipRepository
                    .findByUserSenderIdAndUserReceiverId(currentUser.getUserId(), userReceiverId)
                    .orElse(friendShipRepository.findByUserSenderIdAndUserReceiverId(userReceiverId, currentUser.getUserId())
                            .orElse(null));

            // Nếu đã tồn tại quan hệ bạn bè
            if (existingFriendShip != null) {
                RelationStatus status = existingFriendShip.getRelationShip();

                if (status == RelationStatus.FRIEND) {
                    friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY);
                    return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
                }

                if (status == RelationStatus.SENDING) {
                    if (existingFriendShip.getUserSenderId() == currentUser.getUserId()) {
                        friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_SEND);
                        return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
                    } else {
                        friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_RECEIVED);
                        return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
                    }
                }

                if (status == RelationStatus.STRANGER) {
                        existingFriendShip.setUserSenderId(currentUser.getUserId());
                        existingFriendShip.setUserReceiverId(userReceiverId);


                    // Cập nhật lại trạng thái quan hệ và lưu lại
                    existingFriendShip.setRelationShip(RelationStatus.SENDING); // Cập nhật trạng thái thành SENDING
                    friendShipRepository.save(existingFriendShip);

                    friendShipResponse.setMessage(CommonMessage.FRIEND_REQUEST_SUCCESS);
                    return new ResponseEntity<>(friendShipResponse, HttpStatus.OK);
                }
            }

            // Nếu chưa tồn tại quan hệ, tạo mới
            FriendShip newFriendShip = new FriendShip();
            newFriendShip.setUserSenderId(currentUser.getUserId());
            newFriendShip.setUserReceiverId(userReceiverId);
            newFriendShip.setRelationShip(RelationStatus.SENDING);
            friendShipRepository.save(newFriendShip);

            friendShipResponse.setMessage(CommonMessage.FRIEND_REQUEST_SUCCESS);
            return new ResponseEntity<>(friendShipResponse, HttpStatus.OK);
        }

        // Người dùng chưa đăng nhập
        friendShipResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(friendShipResponse, HttpStatus.UNAUTHORIZED);
    }

//    @Override
//    public ResponseEntity<?> addFriend(int userReceiverId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        FriendShipRequest friendShipRequest = new FriendShipRequest();
//        FriendShipResponse friendShipResponse = new FriendShipResponse();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
//            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
//            User userReceiver = userRepository.findByUserId(userReceiverId).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
//
//            if (currentUser.getUserId() == userReceiverId) {
//                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
//                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
//            }
//            FriendShip friendShip = checkFriendShip(currentUser.getUserId(), userReceiverId);
//            //Check xem da gui loi moi ket ban hay chua
//            if (friendShipRepository.existsFriendShipByUserSenderId(currentUser.getUserId())
//                    || friendShipRepository.existsFriendShipByUserReceiverId(currentUser.getUserId())) {
//                //Neu sender da la ban
//                if (friendShip.getRelationShip().equals(RelationStatus.FRIEND)) {
//                    friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY);
//                    return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
//                }
//                //Neu sender da gui yeu cau truoc do
//                if (friendShip.getRelationShip().equals(RelationStatus.SENDING) && friendShip.getUserSenderId() == currentUser.getUserId()) {
//                    friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_SEND);
//                    return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
//                }
//
//                if (friendShip.getRelationShip().equals(RelationStatus.SENDING) && friendShip.getUserSenderId() == userReceiverId) {
//                    friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_RECEIVED);
//                    return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
//                }
//            }
//            if (friendShip.getRelationShip().equals(RelationStatus.STRANGER)) {
//                friendShip.setRelationShip(RelationStatus.SENDING);
//                friendShipRepository.save(friendShip);
//                friendShipResponse.setMessage(CommonMessage.FRIEND_REQUEST_SUCCESS);
//                return new ResponseEntity<>(friendShipResponse, HttpStatus.OK);
//            }
//        }
//        friendShipResponse.setMessage(CommonMessage.SIGNIN_FIRST);
//        return new ResponseEntity<>(friendShipResponse, HttpStatus.UNAUTHORIZED);
//
//    }

    @Override
    public FriendShip checkFriendShip(int userSenderId, int userReceiverId) {
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).isPresent()) {
            return friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).get();
        }
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).isPresent()) {
            return friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).get();
        } else {
            FriendShip newFriendShip = new FriendShip();
            newFriendShip.setRelationShip(RelationStatus.STRANGER);
            newFriendShip.setUserSenderId(userSenderId);
            newFriendShip.setUserReceiverId(userReceiverId);
            friendShipRepository.save(newFriendShip);
            return newFriendShip;
        }
    }

    @Override
    public FriendShip checkFriendShipPresent(int userSenderId, int userReceiverId) {
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).isPresent()) {
            return friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).get();
        }
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).isPresent()) {
            return friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).get();
        } else throw new NoSuchElementException(CommonMessage.FRIENDSHIP_NOT_FOUND);
    }

    @Override
    public Boolean checkIsFriend(int userSenderId, int userReceiverId) {
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).isPresent()
                && friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId).get().getRelationShip().equals(RelationStatus.FRIEND)) {
            return true;
        }
        if (friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).isPresent()
                && friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId).get().getRelationShip().equals(RelationStatus.FRIEND)) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseEntity<?> acceptFriendRequest(int userSenderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FriendShipResponse friendShipResponse = new FriendShipResponse();

        //FriendShipRequest friendShipRequest = new FriendShipRequest();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            User userReceiver = userRepository.findByUserId(userSenderId).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            if (currentUser.getUserId() == userSenderId) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
            FriendShip friendShip = checkFriendShipPresent(currentUser.getUserId(), userSenderId);
            if (friendShip == null) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_NOT_FOUND);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
            if (friendShip.getRelationShip().equals(RelationStatus.SENDING) && friendShip.getUserReceiverId() == currentUser.getUserId()) {
                friendShip.setRelationShip(RelationStatus.FRIEND);
                friendShip.setRelationCreateTime(LocalDateTime.now());
                friendShipRepository.save(friendShip);
                friendShipResponse.setMessage(CommonMessage.FRIEND_ADD_SUCCESS);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.OK);
            }
            if (friendShip.getRelationShip().equals(RelationStatus.FRIEND)) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
            if (friendShip.getRelationShip().equals(RelationStatus.STRANGER)) {
                friendShipResponse.setMessage(CommonMessage.FRIEND_REQUEST_NOT_EXIST);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
        }
        friendShipResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(friendShipResponse, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> declineFriendRequest(int userSenderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FriendShipRequest friendShipRequest = new FriendShipRequest();
        FriendShipResponse friendShipResponse = new FriendShipResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            User userReceiver = userRepository.findByUserId(userSenderId).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            if (currentUser.getUserId() == userSenderId) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
            FriendShip friendShip = checkFriendShipPresent(currentUser.getUserId(), userSenderId);
            if (friendShip == null) {
                friendShipResponse.setMessage(CommonMessage.FRIENDSHIP_NOT_FOUND);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
            if (friendShip.getRelationShip().equals(RelationStatus.SENDING) || friendShip.getRelationShip().equals(RelationStatus.FRIEND)) {
                friendShip.setRelationShip(RelationStatus.STRANGER);
                friendShip.setRelationCreateTime(null);
                friendShipRepository.save(friendShip);
                friendShipResponse.setMessage(CommonMessage.FRIEND_DECLINE_SUCCESS);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.OK);
            }
            if (friendShip.getRelationShip().equals(RelationStatus.STRANGER)) {
                friendShipResponse.setMessage(CommonMessage.FRIEND_REQUEST_NOT_EXIST);
                return new ResponseEntity<>(friendShipResponse, HttpStatus.BAD_REQUEST);
            }
        }
        friendShipResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(friendShipResponse, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public List<Integer> getListFriendId(int userId) {

        Set<Integer> listFriendSId = new HashSet<>();
        List<FriendShip> friendShipList = friendShipRepository.findByUserSenderIdOrUserReceiverIdAndRelationShip(userId, userId, RelationStatus.FRIEND);
        if (!friendShipList.isEmpty()) {
            for (FriendShip friendShip : friendShipList) {
                if (friendShip.getRelationShip().equals(RelationStatus.FRIEND)) {
                    listFriendSId.add(friendShip.getUserReceiverId());
                    listFriendSId.add(friendShip.getUserSenderId());
                }
            }
            listFriendSId.remove(userId);
        }
        return listFriendSId.stream().toList();
    }

    @Override
    public ResponseEntity<?> getAllFriendList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FriendShipRequest friendShipRequest = new FriendShipRequest();
        FriendShipResponse friendShipResponse = new FriendShipResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            List<FriendShip> friendShipList = friendShipRepository.getAllFriendListByFriendShipStatus(currentUser.getUserId(),RelationStatus.FRIEND);
            friendShipList.addAll(friendShipRepository.getAllFriendListByFriendShipStatus(currentUser.getUserId(),RelationStatus.SENDING));
            return new ResponseEntity<>(friendShipList, HttpStatus.OK);
        }
        friendShipResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
        return new ResponseEntity<>(friendShipResponse, HttpStatus.UNAUTHORIZED);
    }
}