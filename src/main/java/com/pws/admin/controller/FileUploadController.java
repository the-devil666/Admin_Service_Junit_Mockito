package com.pws.admin.controller;

import com.pws.admin.entity.FileEntity;
import com.pws.admin.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
public class FileUploadController {

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity <String> uploadfile(@RequestParam ("file")MultipartFile file) throws IOException {
        if (file.isEmpty()){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request must contain file");
        }
        if (!file.getContentType().equals("image/jpeg")){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Only JPEG content was allowed");

        }else
        {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename();
            String fileType = file.getContentType();
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(filename);
            fileEntity.setFileType(fileType);
            fileEntity.setFileData(bytes);
            fileRepository.save(fileEntity);
        }
        return ResponseEntity.ok("Uploaded successfully") ;
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Optional<FileEntity> fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            FileEntity file = fileOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getFileType()));
            headers.setContentLength(file.getFileData().length);
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(file.getFileName()).build());
            return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
