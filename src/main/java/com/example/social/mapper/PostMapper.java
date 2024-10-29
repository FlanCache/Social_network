package com.example.social.mapper;

import com.example.social.dto.PostDto;
import com.example.social.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "postDto.postImages", ignore = true)
    @Mapping(target = "like", ignore = true)
    PostDto postToDto(Post post);

    Post dtoToPost(PostDto postDto);
}
