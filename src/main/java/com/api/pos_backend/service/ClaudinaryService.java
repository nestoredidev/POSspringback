package com.api.pos_backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

public interface ClaudinaryService {
    Map<?, ?> uplolad(MultipartFile multipartFile) throws Exception;

    Map<?, ?> uplolad(File file) throws Exception;

    Map<?, ?> delete(String id) throws Exception;
}
