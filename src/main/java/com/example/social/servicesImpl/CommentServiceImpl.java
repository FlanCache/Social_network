package com.example.social.servicesImpl;

import com.example.social.common.CommonMessage;
import com.example.social.dto.request.comment.CommentRequest;
import com.example.social.dto.response.CommentResponse.CommentResponse;
import com.example.social.dto.response.CommentResponse.PostCommentResponse;
import com.example.social.entity.Comment;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.repository.*;
import com.example.social.services.CommentService;
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
import java.util.NoSuchElementException;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendShipService friendShipService;
    @Autowired
    FriendShipRepository friendShipRepository;

    @Override
    public ResponseEntity<?> postComment(CommentRequest commentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CommentResponse commentResponse = new CommentResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            Post post = postRepository.findPostsByPostIdAndPostDeleteFlag(commentRequest.getPostId(), 0).orElseThrow(() -> new NoSuchElementException(CommonMessage.POST_NOT_FOUND));
            if (post.getPostDeleteFlag() == 1) {
                commentResponse.setMessage(CommonMessage.POST_NOT_FOUND);
                return new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
            }
            if (!friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId()) && currentUser.getUserId() != post.getPostUserId()) {
                commentResponse.setMessage(CommonMessage.COMMENT_FRIEND_REQUEST);
                return new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
            }
            Comment comment = new Comment();
            comment.setCommentContent(commentRequest.getContent());
            comment.setCommentDeleteFlag(0);
            comment.setCommentPostId(post.getPostId());
            comment.setCommentUserId(currentUser.getUserId());
            comment.setCommentCreatedTime(LocalDateTime.now());
            PostCommentResponse postCommentResponse = new PostCommentResponse();
            postCommentResponse.setContent(commentRequest.getContent());
            postCommentResponse.setCommentDeleteFlag(0);
            postCommentResponse.setCommentCreatedTime(comment.getCommentCreatedTime());
            commentRepository.save(comment);
            return new ResponseEntity<>(postCommentResponse, HttpStatus.CREATED);
        }
        commentResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
        return new ResponseEntity<>(commentResponse, HttpStatus.UNAUTHORIZED);

    }


    @Override
    public ResponseEntity<?> deleteComment(int commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CommentResponse commentResponse = new CommentResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException(CommonMessage.COMMENT_NOT_FOUND));
            if (comment.getCommentUserId() != currentUser.getUserId()) {
                commentResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
                return new ResponseEntity<>(commentResponse, HttpStatus.UNAUTHORIZED);
            }
            if (comment.getCommentDeleteFlag() == 1) {
                commentResponse.setMessage(CommonMessage.COMMENT_NOT_FOUND);
                return new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
            }
            comment.setCommentDeleteFlag(1);
            commentRepository.save(comment);
            commentResponse.setMessage(CommonMessage.SUCCESS);
            return new ResponseEntity<>(commentResponse, HttpStatus.OK);
        }
        commentResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(commentResponse, HttpStatus.UNAUTHORIZED);
    }

}

