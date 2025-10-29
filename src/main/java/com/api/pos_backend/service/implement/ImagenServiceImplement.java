package com.api.pos_backend.service.implement;

import com.api.pos_backend.entity.Imagen;
import com.api.pos_backend.repository.ImagenRepository;
import com.api.pos_backend.service.ClaudinaryService;
import com.api.pos_backend.service.ImagenService;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImagenServiceImplement implements ImagenService {
    private final ImagenRepository imagenRepository;
    private final ClaudinaryService cloudinaryService;

    @Override
    public Imagen uploadImage(MultipartFile file) throws Exception {
        Map<?, ?> uploadResult = cloudinaryService.uplolad(file);
        String url = (String) uploadResult.get("url");
        String publicId = (String) uploadResult.get("public_id");
        Imagen imagen = new Imagen();
        imagen.setName(file.getOriginalFilename());
        imagen.setImagenUrl(url);
        imagen.setImagenId(publicId);
        return imagenRepository.save(imagen);



    }

    @Override
    public void deleteImage(Imagen imagen) throws Exception {
        cloudinaryService.delete(imagen.getImagenId());
        imagenRepository.deleteById(imagen.getId());
    }
}
