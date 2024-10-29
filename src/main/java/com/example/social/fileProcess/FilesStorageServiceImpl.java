package com.example.social.fileProcess;
import com.example.social.common.AvatarOrPostImgFlag;
import com.example.social.common.ConstResouce;
import com.example.social.dto.response.Response;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service

public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path root = Paths.get("uploads");


    public static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            Path filePath = this.root.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        String path = ConstResouce.PATH_ROOT;
        try {
            Path file = root.resolve(path+filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public Boolean photoFormatCheck(MultipartFile file) {
        return SUPPORTED_IMAGE_TYPES.stream().noneMatch(supportedType ->
                Objects.equals(file.getContentType(), supportedType));
    }

    @Override
    public Boolean isFileSizeValid(MultipartFile file, int size) {
        return file.getSize() <= size;
    }

//public ResponseEntity<?> UploadImage(MultipartFile[] file, AvatarOrPostImgFlag flag){
//    Response response  = new Response();
//        if (flag.equals(AvatarOrPostImgFlag.AVATAR_UPDATE)){
//            if (file.length !=1 ){
//                return
//            }
//        }
//}

}