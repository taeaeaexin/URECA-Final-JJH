package com.ureca.ocean.jjh.gemini.service.impl;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.gemini.service.GoogleVisionOcrService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "vision.key.env", havingValue = "real")
public class GoogleVisionOcrServiceImpl implements GoogleVisionOcrService {

    @Value("${vision.key.path}")
    private String visionKeyPath;

    @PostConstruct
    public void setUpCredentials() {
        if (visionKeyPath == null || visionKeyPath.isBlank()) {
            throw new MapException(ErrorCode.OCR_PROCESSING_FAIL);
        }

        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", visionKeyPath);
    }

    // Ocr로 text 추출
    @Override
    public String getOcr(MultipartFile imageFile) {
        try {
            byte[] fileBytes = imageFile.getBytes();
            ByteString imgBytes = ByteString.copyFrom(fileBytes);

            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);

                if (!response.getResponsesList().isEmpty()) {
                    AnnotateImageResponse imageResponse = response.getResponsesList().get(0);

                    if (imageResponse.hasError()) {
                        // error handled below
                    } else {
                        // no debug prints here
                    }
                }

                AnnotateImageResponse res = response.getResponses(0);

                if (res.hasError()) {
                    throw new MapException(ErrorCode.OCR_PROCESSING_FAIL);
                }

                String result = res.getFullTextAnnotation().getText();

                if (result == null || result.trim().isEmpty()) {
                    throw new MapException(ErrorCode.OCR_NO_RESULT);
                }
                return result;
            }
        } catch (IOException e) {
            throw new MapException(ErrorCode.OCR_NO_RESULT);
        }
    }
}
