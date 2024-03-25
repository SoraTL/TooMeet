package com.toomet.chat.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.toomet.chat.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploader {
    private final Cloudinary cloudinary;

    public ImageUploaderResponse uploadImage(String cloudPath, MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("Không tìm thấy file");

        try {
            Map upload = cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("public_id", "toomeet/chats/" + cloudPath));
            return getImageUploaderResponse(upload);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Tải ảnh lên thất bại");
        }

    }

    public ImageUploaderResponse uploadImage(String cloudPath, byte[] file) {
        try {
            Map upload = cloudinary.uploader()
                    .upload(file,
                            Map.of("public_id", "toomeet/chats/" + cloudPath));
            return getImageUploaderResponse(upload);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Tải ảnh lên thất bại");
        }
    }

    private ImageUploaderResponse getImageUploaderResponse(Map upload) {
        ImageUploaderResponse response = new ImageUploaderResponse();
        response.setUrl(upload.get("url").toString());
        response.setFormat(Image.Format.valueOf(upload.get("format").toString().toUpperCase(Locale.ROOT)));
        response.setPublicId(upload.get("public_id").toString());
        return response;
    }


    public List<ImageUploaderResponse> uploadMultiImage(String cloudPath, List<MultipartFile> images) {
        List<ImageUploaderResponse> imageUploaderResponses = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            ImageUploaderResponse imageUploaderResponse = uploadImage(cloudPath, image);
            imageUploaderResponses.add(imageUploaderResponse);
        }
        return imageUploaderResponses;
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Cập nhật ảnh thất bại");
        }
    }


}
