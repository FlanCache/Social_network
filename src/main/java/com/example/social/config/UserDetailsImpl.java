package com.example.social.config;

import com.example.social.common.CommonMessage;
import com.example.social.entity.User;
import com.example.social.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserDetailsImpl implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUserEmail(userEmail);
        return user
                .map(UserDetailsJwt::new)
                .orElseThrow(() -> new NoSuchElementException(CommonMessage.USER_NOT_FOUND));
    }
}
