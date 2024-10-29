package com.example.social.servicesImpl;

import com.example.social.common.CommonMessage;
import com.example.social.common.LikeStatus;
import com.example.social.dto.response.likeResponse.LikeResponse;
import com.example.social.entity.Like;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.LikeRepository;
import com.example.social.repository.PostRepository;
import com.example.social.repository.UserRepository;
import com.example.social.services.FriendShipService;
import com.example.social.services.LikeService;
import com.example.social.services.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    FriendShipService friendShipService;

    @Override
    public ResponseEntity<?> likePost(int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LikeResponse likeResponse = new LikeResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            Post post = postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0).orElseThrow(() -> new NoSuchElementException(CommonMessage.POST_NOT_FOUND));
            if (!friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId()) && currentUser.getUserId() != post.getPostUserId()) {
                likeResponse.setMessage(CommonMessage.LIKE_FRIEND_REQUEST);
                return new ResponseEntity<>(likeResponse, HttpStatus.BAD_REQUEST);
            }
            Like like = likeCheck(postId,currentUser.getUserId());
            if (like.getLikeStatus().equals(LikeStatus.LIKE)) {
                like.setLikeStatus(LikeStatus.UNLIKE);
                like.setLikeCreatedTime(null);
                likeRepository.save(like);
                likeResponse.setMessage(CommonMessage.UNLIKE);
                return new ResponseEntity<>(likeResponse, HttpStatus.OK);
            } else {
                like.setLikeCreatedTime(LocalDateTime.now());
                like.setLikePostId(postId);
                like.setLikeStatus(LikeStatus.LIKE);
                likeRepository.save(like);
                likeResponse.setMessage(CommonMessage.LIKE);
                return new ResponseEntity<>(likeResponse, HttpStatus.OK);
            }


        }
        likeResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
        return new ResponseEntity<>(likeResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    public Like likeCheck(int postId,int userId){
        Like like = new Like();

        if (!likeRepository.existsLikeByLikePostIdAndLikeUserId(postId,userId)) {
            like.setLikePostId(postId);
            like.setLikeUserId(userId);
            like.setLikeStatus(LikeStatus.UNLIKE);
        }else {
            like = likeRepository.findByLikePostIdAndLikeUserId(postId,userId );
        }
        return like;
    }
}
