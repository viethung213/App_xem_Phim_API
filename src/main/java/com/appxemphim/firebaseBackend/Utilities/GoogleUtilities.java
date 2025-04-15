package com.appxemphim.firebaseBackend.Utilities;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleUtilities {
    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    
    public GoogleUtilities(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public long getVideoDuration(String fileLink) {
        String fileId = extractFileId(fileLink);
        try {
            String apiUrl = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/drive/v3/files/" + fileId)
                    .queryParam("fields", "videoMediaMetadata(durationMillis)")
                    .queryParam("key", apiKey)
                    .toUriString();

            // Gọi API bằng RestTemplate
            String response = restTemplate.getForObject(apiUrl, String.class);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("videoMediaMetadata")) {
                long durationMillis = jsonObject.getJSONObject("videoMediaMetadata").getLong("durationMillis");
                return durationMillis;
            } else {
                throw new RuntimeException("lỗi lấy thời lượng video");
            }

        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private String extractFileId(String driveUrl) {
        String regex = ".*(?:id=|/d/)([a-zA-Z0-9_-]+).*";
        return driveUrl.matches(regex) ? driveUrl.replaceAll(regex, "$1") : null;
    }

    public String exportLink(String driveUrl) {
        String fileId = extractFileId(driveUrl);
        if(fileId == null){
            throw new RuntimeException("lỗi lấy đường dẫn video");
        }
        return  "https://drive.google.com/uc?export=download&id=" + fileId;
    }

    
}
