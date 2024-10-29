package com.example.test.social.service;

import com.example.social.common.CommonMessage;
import com.example.social.common.RelationStatus;
import com.example.social.dto.response.FriendShipResponse.FriendShipResponse;
import com.example.social.entity.FriendShip;
import com.example.social.entity.User;
import com.example.social.repository.FriendShipRepository;
import com.example.social.repository.UserRepository;
import com.example.social.services.FriendShipService;
import com.example.social.servicesImpl.FriendShipServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


@MockitoSettings(strictness = Strictness.LENIENT)
public class TestFriendService {
    @InjectMocks
    FriendShipServiceImpl friendShipService;
    @Mock
    FriendShipRepository friendShipRepository;
    @Mock
    UserRepository userRepository;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAddFriendSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int userReceiverId = 2;
        String username = "user";

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userReceiverId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.STRANGER);
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));

        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.existsFriendShipByUserSenderId(currentUser.getUserId())).thenReturn(false);
        when(friendShipRepository.existsFriendShipByUserReceiverId(userReceiverId)).thenReturn(false);
        when(friendShipService.checkFriendShip(currentUser.getUserId(), userReceiverId)).thenReturn(friendShip);
        ResponseEntity<?> responseEntity = friendShipService.addFriend(userReceiverId);

        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIEND_REQUEST_SUCCESS);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        Mockito.verify(friendShipRepository, times(2)).save(Mockito.any(FriendShip.class));
    }

    @Test
    public void testAddFriendConflictYourself() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int userReceiverId = 1;
        String username = "user";

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        User userReceiver = new User();
        userReceiver.setUserId(userReceiverId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");


        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));

        ResponseEntity<?> responseEntity = friendShipService.addFriend(userReceiverId);

        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    public void testAddFriendConflictFriendAlready() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int userReceiverId = 2;
        String username = "user";

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userReceiverId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.FRIEND);

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.existsFriendShipByUserSenderId(Mockito.anyInt())).thenReturn(true);
        when(friendShipRepository.existsFriendShipByUserReceiverId(Mockito.anyInt())).thenReturn(true);
        when(friendShipService.checkFriendShip(Mockito.anyInt(), Mockito.anyInt())).thenReturn(friendShip);
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(friendShip));
        ResponseEntity<?> responseEntity = friendShipService.addFriend(userReceiverId);

        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    public void testAddFriendConflictFriendAlreadySend() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        int userReceiverId = 2;
        String username = "user";

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userReceiverId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.SENDING);
        friendShip.setUserSenderId(currentUser.getUserId());

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.existsFriendShipByUserSenderId(currentUser.getUserId())).thenReturn(true);
        when(friendShipRepository.existsFriendShipByUserReceiverId(userReceiverId)).thenReturn(true);
        when(friendShipService.checkFriendShip(currentUser.getUserId(), userReceiverId)).thenReturn(friendShip);
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(friendShip));

        ResponseEntity<?> responseEntity = friendShipService.addFriend(userReceiverId);

        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_SEND);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    public void testAddFriendConflictFriendAlreadyReceived() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        int userReceiverId = 2;
        String username = "user";

        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userReceiverId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.SENDING);
        friendShip.setUserSenderId(userReceiverId);


        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.existsFriendShipByUserSenderId(currentUser.getUserId())).thenReturn(true);
        when(friendShipRepository.existsFriendShipByUserReceiverId(userReceiverId)).thenReturn(true);
        when(friendShipService.checkFriendShip(currentUser.getUserId(), userReceiverId)).thenReturn(friendShip);
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(friendShip));

        ResponseEntity<?> responseEntity = friendShipService.addFriend(userReceiverId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY_RECEIVED);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCheckFriendShipExisting() {
        int userSenderId = 1;
        int userReceiverId = 2;

        FriendShip existingFriendShip = new FriendShip();
        existingFriendShip.setRelationShip(RelationStatus.FRIEND);
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(existingFriendShip));
        FriendShip result = friendShipService.checkFriendShip(userSenderId, userReceiverId);
        assertEquals(RelationStatus.FRIEND, result.getRelationShip());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCheckFriendShipNonExisting() {
        int userSenderId = 1;
        int userReceiverId = 2;

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.empty());

        FriendShip result = friendShipService.checkFriendShip(userSenderId, userReceiverId);

        assertEquals(RelationStatus.STRANGER, result.getRelationShip());
        assertEquals(userSenderId, result.getUserSenderId());
        assertEquals(userReceiverId, result.getUserReceiverId());
        verify(friendShipRepository, times(1)).save(any(FriendShip.class));
    }

    @Test
    public void testCheckFriendShipPresentExisting() {
        int userSenderId = 1;
        int userReceiverId = 2;

        FriendShip existingFriendShip = new FriendShip();
        existingFriendShip.setRelationShip(RelationStatus.FRIEND);

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId))
                .thenReturn(Optional.of(existingFriendShip));

        FriendShip result = friendShipService.checkFriendShipPresent(userSenderId, userReceiverId);

        assertEquals(RelationStatus.FRIEND, result.getRelationShip());
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    public void testCheckFriendShipPresentNonExisting() {
        int userSenderId = 1;
        int userReceiverId = 2;

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId))
                .thenReturn(Optional.empty());

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userReceiverId, userSenderId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> friendShipService.checkFriendShipPresent(userSenderId, userReceiverId));
        verify(friendShipRepository, never()).save(any(FriendShip.class));
    }

    @Test
    public void testCheckIsFriendWhenFriends() {
        int userSenderId = 1;
        int userReceiverId = 2;

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.FRIEND);

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId))
                .thenReturn(Optional.of(friendShip));


        boolean result = friendShipService.checkIsFriend(userSenderId, userReceiverId);

        assertTrue(result);
    }

    @Test
    public void testCheckIsFriendWhenNoFriendShip() {
        int userSenderId = 1;
        int userReceiverId = 2;

        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(userSenderId, userReceiverId))
                .thenReturn(Optional.empty());

        boolean result = friendShipService.checkIsFriend(userSenderId, userReceiverId);

        assertFalse(result);
    }

    @Test
    public void testAcceptFriendRequestSuccess() {
        int userSenderId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(2);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.SENDING);
        friendShip.setUserReceiverId(currentUser.getUserId());

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(currentUser.getUserId(), userSenderId))
                .thenReturn(Optional.of(friendShip));

        ResponseEntity<?> result = friendShipService.acceptFriendRequest(userSenderId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIEND_ADD_SUCCESS);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testAcceptFriendRequestConflictYourself() {
        int userSenderId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(userSenderId);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));

        ResponseEntity<?> result = friendShipService.acceptFriendRequest(userSenderId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testAcceptFriendRequestFriendshipNotFound() {
        int userSenderId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(userSenderId);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(2);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.STRANGER);
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));

        assertThrows(NoSuchElementException.class, () -> friendShipService.checkFriendShipPresent(userSenderId, userReceiver.getUserId()));
    }

    @Test
    public void testAcceptFriendRequestAlreadyFriend() {
        int userSenderId = 1;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(2);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.FRIEND);
        friendShip.setUserReceiverId(currentUser.getUserId());

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(currentUser.getUserId(), userSenderId))
                .thenReturn(Optional.of(friendShip));

        ResponseEntity<?> result = friendShipService.acceptFriendRequest(userSenderId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_FRIEND_ALREADY);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testDeclineFriendRequestSuccess() {
        int userSenderId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(2);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");

        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        FriendShip friendShip = new FriendShip();
        friendShip.setRelationShip(RelationStatus.SENDING);
        friendShip.setUserReceiverId(currentUser.getUserId());

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(friendShipRepository.findFriendShipByUserSenderIdAndAndUserReceiverId(currentUser.getUserId(), userSenderId))
                .thenReturn(Optional.of(friendShip));

        ResponseEntity<?> result = friendShipService.declineFriendRequest(userSenderId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIEND_DECLINE_SUCCESS);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testDeclineFriendRequestConflictYourself() {
        int userSenderId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);


        User currentUser = new User();
        currentUser.setUserId(userSenderId);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");

        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));
        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));

        ResponseEntity<?> result = friendShipService.declineFriendRequest(userSenderId);
        FriendShipResponse response = new FriendShipResponse();
        response.setMessage(CommonMessage.FRIENDSHIP_CONFLICT_YOURSELF);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testDeclineFriendRequestFriendshipNotFound() {
        int userSenderId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(2);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));


        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> friendShipService.declineFriendRequest(userSenderId));
        assertEquals(CommonMessage.FRIENDSHIP_NOT_FOUND, exception.getMessage());

    }

    @Test
    void testGetListFriendId() {
        int userId = 1;

        List<FriendShip> friendShipList = new ArrayList<>();
        FriendShip friendShip1 = new FriendShip();
        friendShip1.setUserSenderId(userId);
        friendShip1.setUserReceiverId(2);
        friendShip1.setRelationShip(RelationStatus.FRIEND);
        friendShipList.add(friendShip1);

        FriendShip friendShip2 = new FriendShip();
        friendShip2.setUserSenderId(3);
        friendShip2.setUserReceiverId(userId);
        friendShip2.setRelationShip(RelationStatus.FRIEND);
        friendShipList.add(friendShip2);

        when(friendShipRepository.findByUserSenderIdOrUserReceiverIdAndRelationShip(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(friendShipList);

        User user2 = new User();
        user2.setUserId(2);

        User user3 = new User();
        user3.setUserId(3);

        when(userRepository.findByUserId(2)).thenReturn(Optional.of(user2));
        when(userRepository.findByUserId(3)).thenReturn(Optional.of(user3));

        List<Integer> result = friendShipService.getListFriendId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(2));
    }
    @Test
    public void testGetAllFriendList_LoggedInUserWithFriends_ReturnsFriendList() {
        int userSenderId = 1;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = new User();
        currentUser.setUserId(2);
        currentUser.setUserEmail("user@example.com");
        currentUser.setUserPassword("12345");
        currentUser.setUserFullName("userTest");
        currentUser.setUserRoles("user");
        currentUser.setUserAvatar("avatar.png");
        currentUser.setUserBirthday("01/01/2000");
        currentUser.setUserAddress("ha noi");
        currentUser.setUserPhone("01112233");
        User userReceiver = new User();
        userReceiver.setUserId(userSenderId);
        userReceiver.setUserEmail("user@example.com");
        userReceiver.setUserPassword("12345");
        userReceiver.setUserFullName("userTest2");
        userReceiver.setUserRoles("user");
        userReceiver.setUserAvatar("avatar.png");
        userReceiver.setUserBirthday("01/01/2000");
        userReceiver.setUserAddress("ha noi");
        userReceiver.setUserPhone("01112233");
        Mockito.when(((UserDetails) authentication.getPrincipal()).getUsername()).thenReturn("userTest");

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(userReceiver));

        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(currentUser));

        List<FriendShip> friendShipList = new ArrayList<>();
        when(friendShipRepository.getAllFriendListByFriendShipStatus(anyInt(), any())).thenReturn(friendShipList);

        ResponseEntity<?> responseEntity = friendShipService.getAllFriendList();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        assertEquals(friendShipList, responseEntity.getBody());
    }

}
