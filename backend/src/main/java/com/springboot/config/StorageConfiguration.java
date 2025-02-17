package com.springboot.config;

import com.springboot.question.service.FileSystemStorageService;
import com.springboot.question.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {
    @Bean
    public StorageService fileSystemStorageService(@Value("${file.upload-dir}") String uploadDir){
        return new FileSystemStorageService(uploadDir);
    }
}