package com.example.social.servicesImpl;

import com.example.social.dto.response.Response;
import com.example.social.fileProcess.FilesStorageService;
import com.example.social.common.CommonMessage;
import com.example.social.common.ConstResouce;
import com.example.social.common.RelationStatus;
import com.example.social.dto.request.authRequest.SigninRequest;
import com.example.social.dto.request.authRequest.SigupRequest;
import com.example.social.dto.request.userRequest.UserUpdateRequest;
import com.example.social.dto.response.userResponse.*;
import com.example.social.entity.User;
import com.example.social.repository.*;
import com.example.social.services.OtpServices;
import com.example.social.services.PostService;
import com.example.social.services.UserService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    OtpServices otpServices;

    @Autowired
    FilesStorageService filesStorageService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    FriendShipRepository friendShipRepository;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    PostService postService;


    @Override
    public ResponseEntity<?> signUp(SigupRequest sigupRequest) {
        User newUser;
        Response userSignUpResponse = new Response();

        if (userRepository.existsUserByUserEmail(sigupRequest.getEmail())) {
            userSignUpResponse.setMessage(CommonMessage.EXITS_EMAIL_ERROR);
            return new ResponseEntity<>(userSignUpResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            newUser = signUpResponseToUser(sigupRequest);
            userRepository.save(newUser);
            userSignUpResponse.setMessage(CommonMessage.SUCCESS);
            return new ResponseEntity<>(userSignUpResponse, HttpStatus.CREATED);
        } catch (Exception ex) {
            userSignUpResponse.setMessage(CommonMessage.OCCURED_SERVER);
            return new ResponseEntity<>(userSignUpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<?> signIn(SigninRequest signinRequest) {
        String currentUserEmail = signinRequest.getEmail();
        String currentUserPassword = signinRequest.getPassword();
        Response response = new Response();
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(currentUserEmail, currentUserPassword)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String otp = otpServices.otpSender(currentUserEmail).getOtp();
        response.setMessage(CommonMessage.OTP_EXPIRE);
        userSigninResponse.setOtp(otp);
        response.setData(userSigninResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public User signUpResponseToUser(SigupRequest sigupRequest) {
        User user = new User();
        user.setUserEmail(sigupRequest.getEmail());
        user.setUserPassword(passwordEncoder.encode(sigupRequest.getPassword()));
        user.setUserFullName(sigupRequest.getFullName());
        user.setUserRoles("user");
        user.setUserAvatar(ConstResouce.DEFAULT_AVATAR);
        return user;
    }

    @Override
    public ResponseEntity<?> updateUser(UserUpdateRequest userUpdateRequest) {
        UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResponse userResponse = new UserResponse();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));

            if (userUpdateRequest.getFullName() != null && !userUpdateRequest.getFullName().isEmpty()) {
                currentUser.setUserFullName(userUpdateRequest.getFullName());
            }
            if (userUpdateRequest.getBirthday() != null && !userUpdateRequest.getBirthday().isEmpty()) {
                currentUser.setUserBirthday(userUpdateRequest.getBirthday());
            }
            if (userUpdateRequest.getPhone() != null && !userUpdateRequest.getPhone().isEmpty()) {
                currentUser.setUserPhone(userUpdateRequest.getPhone());
            }
            if (userUpdateRequest.getAddress() != null && !userUpdateRequest.getAddress().isEmpty()) {
                currentUser.setUserAddress(userUpdateRequest.getAddress());
            }
            userRepository.save(currentUser);
            userResponse.setMessage(CommonMessage.SUCCESS);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            userResponse.setMessage(CommonMessage.SIGNIN_FIRST);
            return new ResponseEntity<>(userResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> avatarUpdate(MultipartFile file) {
        if (filesStorageService.photoFormatCheck(file)) {
            return new ResponseEntity<>(CommonMessage.FILE_FORMAT_ERROR, HttpStatus.BAD_REQUEST);
        }
        if (!filesStorageService.isFileSizeValid(file, ConstResouce.AVATAR_FILE_SIZE)) {
            return new ResponseEntity<>(CommonMessage.FILE_SIZE_ERROR, HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            Resource resource;
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            try {
                String image = filesStorageService.save(file);
                currentUser.setUserAvatar(image);
                userRepository.save(currentUser);
                resource = filesStorageService.load(image);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
            } catch (Exception e) {
                String message = CommonMessage.FILE_UPLOAD_ERROR + e.getMessage();
                return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> forgotPassword(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
        UserSigninResponse userSigninResponse = otpServices.otpSender(userEmail);
        Response response = new Response();
        response.setMessage(CommonMessage.SUCCESS);
        response.setData(userSigninResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    private String getCurrentTimeName() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return currentDateTime.format(formatter);
    }
    @Override
    public ResponseEntity<?> exportReport() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        UserResponse userResponse = new UserResponse();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));

            String timestamp = String.valueOf(currentUser.getUserId() + "_" + getCurrentTimeName());
            String fileName = "Log_" + timestamp + ".xlsx";

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Log");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Số lượng bài viết trong tuần qua");
                headerRow.createCell(1).setCellValue("Số lượng bình luận trong tuần qua");
                headerRow.createCell(2).setCellValue("Số lượng lượt thích trong tuần qua");
                headerRow.createCell(3).setCellValue("Số lượng bạn bè trong tuần qua");

                int postCount = postRepository.countPostsInLastWeek(oneWeekAgo, currentUser.getUserId());
                int commentCount = commentRepository.countCommentsInLastWeek(oneWeekAgo, currentUser.getUserId());
                int likeCount = likeRepository.countLikesInLastWeek(oneWeekAgo, currentUser.getUserId());
                int friendCount = friendShipRepository.countFriendsInLastWeek(oneWeekAgo, currentUser.getUserId(), RelationStatus.FRIEND);

                Row dataRow = sheet.createRow(1);
                dataRow.createCell(0).setCellValue(postCount);
                dataRow.createCell(1).setCellValue(commentCount);
                dataRow.createCell(2).setCellValue(likeCount);
                dataRow.createCell(3).setCellValue(friendCount);

                String currentWorkingDir = System.getProperty("user.dir");
                String filePath = currentWorkingDir + "\\log\\" + fileName;
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                    HttpHeaders headers = new HttpHeaders();
                    Resource resource = new UrlResource("file:" + filePath);
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(resource.contentLength())
                            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                            .body(resource);
                }
            } catch (IOException e) {
                e.printStackTrace();
                userResponse.setMessage("File processing failed");
                return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(CommonMessage.SIGNIN_FIRST, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResponse userResponse = new UserResponse();
        Response response = new Response();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByUserEmail(username).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
            UserInfoResponse userInfoResponse = getUserInfoResponse(currentUser);
            response.setMessage(CommonMessage.SUCCESS);
            response.setData(userInfoResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        userResponse.setMessage(CommonMessage.SIGNIN_FIRST);
        return new ResponseEntity<>(userResponse, HttpStatus.UNAUTHORIZED);
    }

    private static UserInfoResponse getUserInfoResponse(User currentUser) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(currentUser.getUserId());
        userInfoResponse.setFullName(currentUser.getUserFullName());
        userInfoResponse.setBirthday(currentUser.getUserBirthday());
        userInfoResponse.setAddress(currentUser.getUserAddress());
        userInfoResponse.setAvatar(currentUser.getUserAvatar());
        userInfoResponse.setEmail(currentUser.getUserEmail());
        userInfoResponse.setPhone(currentUser.getUserPhone());
        return userInfoResponse;
    }
}
