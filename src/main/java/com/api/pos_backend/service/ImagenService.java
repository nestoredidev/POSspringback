package com.api.pos_backend.service;

import com.api.pos_backend.entity.Imagen;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImagenService {

    Imagen uploadImage(MultipartFile file) throws Exception;

    void deleteImage(Imagen imagen) throws Exception;
}
