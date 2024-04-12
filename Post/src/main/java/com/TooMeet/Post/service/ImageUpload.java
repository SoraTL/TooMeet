package com.TooMeet.Post.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUpload {
    String uploadImage(MultipartFile multipartFile) throws IOException;
    public void deleteImageById(String imageId);
    public String extractPublicId(String cloudinaryUrl);
}
