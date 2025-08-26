package com.fundicion.lara.service;

import com.fundicion.lara.dto.response.ImgBBResponse;
import com.fundicion.lara.exception.BadRequestException;
import com.fundicion.lara.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.fundicion.lara.commons.constants.Constants.IMGBB_API_URL;

@Slf4j
@Service()
@RequiredArgsConstructor
public class FileService {
    private final ProductService productService;
    private final RestTemplate restTemplate;

    @Value("${image.api-key}")
    private String apiKey;


    public String uploadImageById(MultipartFile file, Integer productId) throws IOException {
        validateFile(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("image", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<ImgBBResponse> response = restTemplate.exchange(IMGBB_API_URL, HttpMethod.POST, requestEntity, ImgBBResponse.class);


        if (response.getBody() != null && response.getBody().isSuccess()) {
            var avatar = response.getBody().getData().getUrl();
            var product = this.productService.findProductById(productId);
            product.setAvatar(avatar);
            this.productService.updateProduct(product, productId);
            return avatar;
        } else {
            throw new RuntimeException("Hubo un problema al subir la imagen. Asegúrate de que el archivo sea válido y prueba nuevamente.");
        }
    }

    private void validateFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Por favor selecciona un archivo");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("Solo se permiten archivos de imagen");
            }
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw new InternalException(e.getMessage());
            }
            throw new InternalException("Error al subir a Imgur: " + e.getMessage());
        }
    }
}
