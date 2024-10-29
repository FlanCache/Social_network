package com.example.social.repository;

import com.example.social.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {
    List<Image> findByPostIdAndImageDeleteFlag(int postId, int deleteFlag);
}
