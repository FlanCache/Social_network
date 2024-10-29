package com.example.social.servicesImpl;

import com.example.social.common.CommonMessage;
import com.example.social.dto.request.authRequest.OtpLoginRequest;
import com.example.social.dto.request.userRequest.ChangePasswordRequest;
import com.example.social.dto.response.Response;
import com.example.social.dto.response.userResponse.UserSigninResponse;
import com.example.social.entity.User;
import com.example.social.jwt.JwtUltils;
import com.example.social.repository.UserRepository;
import com.example.social.services.OtpServices;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
@Transactional
public class OtpServicesImpl implements OtpServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUltils jwtUltils;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public String otpGenerate() {
        int otpLength = 5;
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < otpLength; i++) {
            int digit = random.nextInt(10);
            otp.append(digit);
        }
        return otp.toString();
    }

    @Override
    public UserSigninResponse otpSender(String userEmail) {
        User currentUser = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
        String otp = otpGenerate();
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        currentUser.setUserOtp(otp);
        currentUser.setUserOtpCreatedTime(LocalDateTime.now());
        userSigninResponse.setOtp(otp);
        userRepository.save(currentUser);
        return userSigninResponse;
    }

    @Override
    public ResponseEntity<?> otpLogin(OtpLoginRequest otpLoginRequest) {
        User currentUser = userRepository.findByUserEmail(otpLoginRequest.getEmail())
                .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
        String currentOtp = currentUser.getUserOtp();
        UserSigninResponse userSigninResponse = new UserSigninResponse();
        Response response = new Response();
        if (currentOtp == null) {
            response.setMessage(CommonMessage.OTP_NOT_EXIST);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (currentUser.getUserOtpCreatedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            response.setMessage(CommonMessage.OTP_EXPIRED);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!currentOtp.equals(otpLoginRequest.getOtp())) {
            response.setMessage(CommonMessage.OTP_NOT_MATCH);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            String userToken = jwtUltils.generateToken(currentUser.getUserEmail());
            userSigninResponse.setToken(userToken);
            currentUser.setUserOtp(null);
            response.setMessage(CommonMessage.SUCCESS);
            response.setData(userSigninResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> otpChangePasswordCheck(ChangePasswordRequest changePasswordRequest) {
        User currentUser = userRepository.findByUserEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
        String currentOtp = currentUser.getUserOtp();
        UserSigninResponse userSigninResponse = new UserSigninResponse();

        if (currentOtp == null) {
            userSigninResponse.setMessage(CommonMessage.OTP_NOT_EXIST);
            return new ResponseEntity<>(userSigninResponse, HttpStatus.UNAUTHORIZED);
        } else if (!currentOtp.equals(changePasswordRequest.getOtp())) {
            userSigninResponse.setMessage(CommonMessage.OTP_NOT_MATCH);

            return new ResponseEntity<>(userSigninResponse, HttpStatus.UNAUTHORIZED);
        }
        if (currentOtp.equals(currentUser.getUserOtp())) {
            if (currentUser.getUserOtpCreatedTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
                userSigninResponse.setMessage(CommonMessage.OTP_EXPIRED);
                return new ResponseEntity<>(userSigninResponse, HttpStatus.BAD_REQUEST);
            }
            currentUser.setUserPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            currentUser.setUserOtp(null);
            userRepository.save(currentUser);
            userSigninResponse.setMessage(CommonMessage.SUCCESS);
            return new ResponseEntity<>(userSigninResponse, HttpStatus.OK);
        } else {
            userSigninResponse.setMessage(CommonMessage.SIGNIN_FIRST);
            return new ResponseEntity<>(userSigninResponse, HttpStatus.UNAUTHORIZED);
        }

    }
}
