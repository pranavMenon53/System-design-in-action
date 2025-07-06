
package com.example.upload;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*")
@RestController
public class UploadController {

    private static final String UPLOAD_DIR = "uploaded_chunks";

    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                      @RequestParam("chunkIndex") int chunkIndex,
                                      @RequestParam("totalChunks") int totalChunks,
                                      @RequestParam("fileName") String fileName) throws IOException {

        String dirName = StringUtils.substringBeforeLast(fileName,".");

        Path dirPath = Paths.get(UPLOAD_DIR, dirName);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path chunkPath = dirPath.resolve("chunk_" + chunkIndex);
        Files.write(chunkPath, chunk.getBytes());

        // Once we have received all the chunks, combine them to create a video file
        // TODO: This video file can then be shared with a splitter service which will
        // split the video into 5 second clips.
        if (Files.list(dirPath).count() == totalChunks) {
            Path finalFile = Paths.get(UPLOAD_DIR, fileName);

            try (OutputStream os = Files.newOutputStream(finalFile)) {
                for (int i = 0; i < totalChunks; i++) {
                    Path part = dirPath.resolve("chunk_" + i);
                    Files.copy(part, os);
                }
            }

            // Save video metadata to the DB
            Video video = new Video();
            video.setFileName(fileName);
            video.setPath(finalFile.toString());
            videoRepository.save(video);
        }

        UploadResponse response = new UploadResponse("Chunk received");
        ResponseEntity<UploadResponse> objectResponseEntity =
                new ResponseEntity<>(response, HttpStatusCode.valueOf(200));

        return objectResponseEntity;
    }
}
