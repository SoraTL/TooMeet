package com.toomet.chat.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageUploader imageUploader;
    private final ImageRepository imageRepository;


    public ImageUploaderResponse upsertImage(String cloudPath, MultipartFile image) {
        return imageUploader.uploadImage(cloudPath, image);
    }


}
