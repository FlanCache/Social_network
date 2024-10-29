package com.example.test.social.service;

import com.example.social.fileProcess.FilesStorageServiceImpl;
import com.example.social.common.ConstResouce;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TestFileStorageService {
    @InjectMocks
    FilesStorageServiceImpl filesStorageService;

    @Mock
    Files filesMock;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void initCreatesDirectoriesSuccessfully() throws IOException {
        Path root = Paths.get("uploads");

        Mockito.mockStatic(Files.class);
        Mockito.when(Files.createDirectories(Mockito.any())).thenReturn(root);

        assertDoesNotThrow(() -> filesStorageService.init());

        Mockito.verify(Files.class, times(1));
        Files.createDirectories(any(Path.class));
    }

    @Test
    void testPhotoFormatCheck() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.png", "beautifullImg", "Lulibina!".getBytes());
        boolean result = filesStorageService.photoFormatCheck(mockFile);
        assertTrue(result);
    }
    @Test
    void testIsFileSizeValid() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.png", "beautifullImg", new byte[ConstResouce.AVATAR_FILE_SIZE + 1]);
        boolean result = filesStorageService.isFileSizeValid(mockFile, ConstResouce.AVATAR_FILE_SIZE);
        assertFalse(result);
    }

}
