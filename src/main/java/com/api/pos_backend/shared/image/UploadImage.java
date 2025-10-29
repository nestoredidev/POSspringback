package com.api.pos_backend.shared.image;

import org.springframework.beans.factory.annotation.Value;

public  class UploadImage {

    @Value("${image.upload.url}")
    private String imageUrl;





}