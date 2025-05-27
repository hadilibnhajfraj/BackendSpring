package tn.esprit.spring.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos")
public class VideoStreamController {

    private final String VIDEO_PATH = "uploads/videos/sample.mp4";

    @GetMapping("/stream")
    public ResponseEntity<byte[]> streamVideo(@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        Path videoPath = Paths.get(VIDEO_PATH);
        byte[] videoBytes = Files.readAllBytes(videoPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentLength(videoBytes.length);

        return new ResponseEntity<>(videoBytes, headers, HttpStatus.OK);
    }
}
