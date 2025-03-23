package org.wora.we_work.services.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    String uploadFile(MultipartFile file) throws IOException;
    List<String> uploadMultipleFiles(List<MultipartFile> files) throws IOException;
    void deleteFile(String publicId)throws IOException;

}
