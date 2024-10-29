package com.example.social.repository;

import com.example.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
 Optional<User> findByUserEmail(String email);
 Optional<User> findByUserId(int userId);

 boolean existsUserByUserEmail(String email);

 boolean existsUserByUserId(int userId);

}
