package com.example.test.social.service;

import com.example.social.config.UserDetailsImpl;
import com.example.social.entity.User;
import com.example.social.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserDetailsService {
    @InjectMocks
    UserDetailsImpl userDetailsService;
    @Mock
    UserRepository userRepository;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testLoadUserByUsernameFoundUser() {
        String userEmail = "test@example.com";
        User user = new User();
        user.setUserEmail(userEmail);
        user.setUserId(1);
        user.setUserRoles("user");

        when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(userEmail));

        verify(userRepository, times(1)).findByUserEmail(userEmail);
        assertNotNull(userDetails);
        assertEquals(userEmail, userDetails.getUsername());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testLoadUserByUsernameUserNotFound() {
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByUserEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userDetailsService.loadUserByUsername(userEmail));

        verify(userRepository, times(1)).findByUserEmail(userEmail);
    }
}
