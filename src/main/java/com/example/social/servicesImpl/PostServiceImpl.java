package com.example.social.servicesImpl;

import com.example.social.dto.response.Response;
import com.example.social.dto.response.postResponse.TimelineResponse;
import com.example.social.fileProcess.FilesStorageService;
import com.example.social.common.CommonMessage;
import com.example.social.common.ConstResouce;
import com.example.social.common.LikeStatus;
import com.example.social.dto.PostDto;
import com.example.social.dto.request.postRequest.EditPostRequest;
import com.example.social.dto.request.postRequest.UpPostRequest;
import com.example.social.dto.response.postResponse.ExceptionResponse;
import com.example.social.dto.response.postResponse.PostResponse;
import com.example.social.dto.response.postResponse.UpPostResponse;
import com.example.social.entity.Image;
import com.example.social.entity.Post;
import com.example.social.entity.User;
import com.example.social.mapper.PostMapper;
import com.example.social.repository.*;
import com.example.social.services.FriendShipService;
import com.example.social.services.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepository postRepository;
    @Autowired
    FilesStorageService filesStorageService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    FriendShipService friendShipService;


    @Override
    public ResponseEntity<?> upPost(UpPostRequest upPostRequest, MultipartFile[] multipartFiles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UpPostResponse upPostResponse = new UpPostResponse();
        Response response = new Response();

        String currentUserEmail;
        List<Image> imageList = new ArrayList<>();


        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserEmail = authentication.getName();
            upPostRequest.setPostIdUser(userRepository.findByUserEmail(currentUserEmail)
                    .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND)).getUserId()
            );
        } else {
            response.setMessage(CommonMessage.SIGNIN_FIRST);
            return new ResponseEntity<>(upPostResponse, HttpStatus.UNAUTHORIZED);
        }

        int currentPostId = postSave(upPostRequest).getPostId();
        upPostRequest.setPostId(currentPostId);
        if (multipartFiles != null) {
            for (MultipartFile image : multipartFiles) {
                if (filesStorageService.photoFormatCheck(image)) {
                    return new ResponseEntity<>(CommonMessage.FILE_FORMAT_ERROR, HttpStatus.BAD_REQUEST);
                }
                if (!filesStorageService.isFileSizeValid(image, ConstResouce.POST_PHOTO_SIZE)) {
                    return new ResponseEntity<>(CommonMessage.FILE_SIZE_ERROR, HttpStatus.BAD_REQUEST);
                }
                String filePath = filesStorageService.save(image);
                Image newImage = new Image();
                newImage.setImageUrl(filePath);
                newImage.setImageDeleteFlag(0);
                newImage.setPostId(currentPostId);
                imageRepository.save(newImage);
                imageList.add(newImage);
            }
        }
        try {
            if (imageList.isEmpty() && upPostRequest.getPostContent().isEmpty()) {
                ExceptionResponse exceptionResponse = new ExceptionResponse();
                exceptionResponse.setMessage(CommonMessage.POST_VALID);
                return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (NullPointerException ex) {
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setMessage(CommonMessage.POST_VALID);
            return new ResponseEntity<>(CommonMessage.POST_VALID, HttpStatus.BAD_REQUEST);
        }
        response.setMessage(CommonMessage.SUCCESS);
        response.setData(postRequestToResponse(upPostRequest, imageList, 0));
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @Override
    public UpPostResponse postRequestToResponse(UpPostRequest upPostRequest, List<Image> imageList, int updateFlag) {
        UpPostResponse upPostResponse = new UpPostResponse();
        upPostResponse.setId(upPostRequest.getPostId());
        upPostResponse.setUserId(upPostRequest.getPostIdUser());
        upPostResponse.setContent(upPostRequest.getPostContent());
        upPostResponse.setImages(imageList);
        upPostResponse.setCreateTime(LocalDateTime.now());
        return upPostResponse;
    }

    public Post postSave(UpPostRequest upPostRequest) {
        Post post = new Post();
        post.setPostUserId(upPostRequest.getPostIdUser());
        post.setPostCreateTime(LocalDateTime.now());
        post.setContent(upPostRequest.getPostContent());
        post.setPostDeleteFlag(0);
        postRepository.save(post);
        return post;
    }

    @Override
    public ResponseEntity<?> editPost(EditPostRequest editPostRequest, MultipartFile[] multipartFiles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response response = new Response();
        Post post = postRepository.findPostsByPostIdAndPostDeleteFlag(editPostRequest.getPostId(), 0)
                .orElseThrow(() -> new NoSuchElementException(CommonMessage.POST_NOT_FOUND));
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            if (currentUser.getUserId() == post.getPostUserId()) {
                List<Image> imagesFormRequest = new ArrayList<>();
                List<Image> imagesFormPost = imageRepository.findByPostIdAndImageDeleteFlag(editPostRequest.getPostId(), 0);
                List<Image> imageListAfterEdit = new ArrayList<>();
                if (multipartFiles != null) {
                    for (MultipartFile image : multipartFiles) {
                        if (filesStorageService.photoFormatCheck(image)) {
                            return new ResponseEntity<>(CommonMessage.FILE_FORMAT_ERROR, HttpStatus.BAD_REQUEST);
                        }
                        if (!filesStorageService.isFileSizeValid(image, ConstResouce.POST_PHOTO_SIZE)) {
                            return new ResponseEntity<>(CommonMessage.FILE_SIZE_ERROR, HttpStatus.BAD_REQUEST);
                        }
                        String filePath = filesStorageService.save(image);
                        Image newImage = new Image();
                        newImage.setImageUrl(filePath);
                        newImage.setImageDeleteFlag(0);
                        imagesFormRequest.add(newImage);
                    }
                }
                if (!imagesFormRequest.isEmpty()) {
                    if (!imagesFormPost.isEmpty()) {
                        for (Image image : imagesFormPost) {
                            image.setImageDeleteFlag(1);
                            imageRepository.save(image);
                        }
                    }
                    for (Image image : imagesFormRequest) {
                        Image newImage = new Image();
                        newImage.setImageUrl(image.getImageUrl());
                        newImage.setImageDeleteFlag(0);
                        newImage.setPostId(post.getPostId());
                        imageRepository.save(newImage);
                        imageListAfterEdit.add(newImage);
                    }
                } else {
                    if (!imagesFormPost.isEmpty()) {
                        for (Image image : imagesFormPost) {
                            image.setImageDeleteFlag(1);
                            imageRepository.save(image);
                        }
                    }
                }
                post.setContent(editPostRequest.getPostContent());
                post.setPostDeleteFlag(editPostRequest.getPostDeleteFlag());
                try {
                    if (imagesFormRequest.isEmpty() && editPostRequest.getPostContent().isEmpty()) {
                        response.setMessage(CommonMessage.POST_VALID);
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                } catch (NullPointerException ex) {
                    response.setMessage(CommonMessage.POST_VALID);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                postRepository.save(post);
                UpPostResponse upPostResponse = new UpPostResponse();
                upPostResponse.setContent(editPostRequest.getPostContent());
                upPostResponse.setDeleteFlag(editPostRequest.getPostDeleteFlag());
                upPostResponse.setImages(imagesFormRequest);
                upPostResponse.setComments(commentRepository.findCommentsByCommentPostIdAndCommentDeleteFlag(editPostRequest.getPostId(), 0));
                upPostResponse.setLike(likeRepository.countLikeByLikePostIdAndLikeStatus(editPostRequest.getPostId(), LikeStatus.LIKE));
                upPostResponse.setCreateTime(post.getPostCreateTime());

                response.setData(upPostResponse);
                response.setMessage(CommonMessage.SUCCESS);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }
        response.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deletePost(int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PostResponse postResponse = new PostResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            Post post = postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0).orElseThrow(() -> new NoSuchElementException(CommonMessage.POST_NOT_FOUND));
            if (currentUser.getUserId() == post.getPostUserId()) {
                post.setPostDeleteFlag(1);

                postRepository.save(post);
                postResponse.setMessage(CommonMessage.SUCCESS);
                return new ResponseEntity<>(postResponse, HttpStatus.OK);
            } else {
                postResponse.setMessage(CommonMessage.NOT_ENOUGHT_PERMISSION);
                return new ResponseEntity<>(postResponse, HttpStatus.FORBIDDEN);
            }

        }
        postResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(postResponse, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> showPostById(int postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response response = new Response();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            Post post = postRepository.findPostsByPostIdAndPostDeleteFlag(postId, 0).orElseThrow(() -> new NoSuchElementException(CommonMessage.POST_NOT_FOUND));
            if (!friendShipService.checkIsFriend(currentUser.getUserId(), post.getPostUserId()) && currentUser.getUserId() != post.getPostUserId()) {
                return new ResponseEntity<>(CommonMessage.COMMENT_FRIEND_REQUEST, HttpStatus.BAD_REQUEST);
            }
            PostDto postDto = PostMapper.INSTANCE.postToDto(post);
            postDto.setPostImages(imageRepository.findByPostIdAndImageDeleteFlag(postDto.getPostId(), 0));
            postDto.setPostComments(commentRepository.findCommentsByCommentPostIdAndCommentDeleteFlag(postDto.getPostId(), 0));
            postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeStatus(postDto.getPostId(), LikeStatus.LIKE));
            response.setMessage(CommonMessage.SUCCESS);
            response.setData(postDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(CommonMessage.NOT_ENOUGHT_PERMISSION, HttpStatus.UNAUTHORIZED);
    }


    @Override
    public ResponseEntity<?> timeLineByPage(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime timeCanSeePost = LocalDateTime.now().minusHours(5);
        Response response = new Response();
        TimelineResponse timelineResponse = new TimelineResponse();
        Page<Post> postsList;
        Page<PostDto> postDtoList;

        Pageable pageable = PageRequest.of(page, size);

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            List<Integer> listFriendIds = new ArrayList<>(friendShipService.getListFriendId(currentUser.getUserId()));
            listFriendIds.add(currentUser.getUserId());
            List<PostDto> listPostDto = new ArrayList<>();
            List<Post> listPost = postRepository.findByPostUserIdInAndPostDeleteFlagOrderByPostCreateTime(listFriendIds, 0, pageable);
            timelineResponse.setTotalRecords(postRepository.countPostsByPostUserIdAndPostDeleteFlag(currentUser.getUserId(), 0));
            for (Post post : listPost) {
                PostDto postDto = (PostMapper.INSTANCE.postToDto(post));
                postDto.setPostImages(imageRepository.findByPostIdAndImageDeleteFlag(post.getPostId(), 0));
                postDto.setPostComments(commentRepository.findCommentsByCommentPostIdAndCommentDeleteFlag(post.getPostId(), 0));
                postDto.setLike(likeRepository.countLikeByLikePostIdAndLikeStatus(post.getPostId(), LikeStatus.LIKE));
                listPostDto.add(postDto);
            }
            timelineResponse.setListPosts(listPostDto);
            response.setData(timelineResponse);
            response.setMessage(CommonMessage.SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(CommonMessage.SIGNIN_FIRST, HttpStatus.UNAUTHORIZED);
    }
}
