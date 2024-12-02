package com.example.social.fileProcess;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
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
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot upload an empty file.");
            }

            // Kiểm tra tên file trùng lặp
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            Path filePath = root.resolve(originalFileName);

            // Nếu file đã tồn tại thì thêm hậu tố để tránh ghi đè
            String resolvedName = resolveFileNameConflict(filePath);
            filePath = root.resolve(resolvedName);

            // Lưu file vào thư mục uploads
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not upload the file. Error: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            if (filename.contains("..")) {
                throw new RuntimeException("Invalid path sequence: " + filename);
            }
            Path file = root.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + e.getMessage(), e);
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
            throw new RuntimeException("Could not load the files!", e);
        }
    }

    @Override
    public Boolean photoFormatCheck(MultipartFile file) {
        return SUPPORTED_IMAGE_TYPES.stream().noneMatch(supportedType ->
                Objects.equals(file.getContentType(), supportedType));
    }

    @Override
    public Boolean isPhotoFormatInvalid(MultipartFile file) {
        return SUPPORTED_IMAGE_TYPES.stream().noneMatch(supportedType ->
                Objects.equals(file.getContentType(), supportedType));
    }

    @Override
    public Boolean isFileSizeValid(MultipartFile file, int size) {
        return file.getSize() <= size;
    }

    //Hàm thêm số và kí tự khi trùng tên file
    private String resolveFileNameConflict(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
        int counter = 0;

        while (Files.exists(filePath)) {
            counter++;
            filePath = root.resolve(baseName + "_" + counter + extension);
        }
        return filePath.getFileName().toString();
    }
}
