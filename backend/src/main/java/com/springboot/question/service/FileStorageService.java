package com.springboot.question.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final String UPLOAD_DIR = "C:\\solo_project\\backend\\src\\main\\resources\\fileImage";
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif"};

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file); // 파일 검증 (확장자 확인)

        // 파일명을 UUID로 생성하여 중복 방지
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        // 파일 저장 디렉토리 확인 & 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Files.write(Paths.get(filePath), file.getBytes());

        return "/fileImage/" + fileName;
    }

    // 허용된 파일 형식인지 검증
    private void validateFile(MultipartFile file) {
        if (!isAllowedType(file.getContentType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (JPEG, PNG, GIF만 가능)");
        }
    }

    // 허용된 확장자 검사
    private boolean isAllowedType(String contentType) {
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
