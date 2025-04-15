package com.appxemphim.firebaseBackend.service;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOException;

import org.springframework.stereotype.Service;

import com.appxemphim.firebaseBackend.Utilities.GoogleUtilities;
import com.appxemphim.firebaseBackend.dto.request.VideoRequest;
import com.appxemphim.firebaseBackend.model.Movie;
import com.appxemphim.firebaseBackend.model.Video;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final GoogleUtilities googleUtilities;
    private final Firestore db = FirestoreClient.getFirestore();

    public String create(VideoRequest videoRequest){
        try {
            
            Video video = new Video();
            DocumentReference videoRef  = db.collection("Video").document();
            
            video.setVideo_id(videoRef .getId());
            video.setDuration(googleUtilities.getVideoDuration(videoRequest.getLink()));
            video.setVideo_url(videoRequest.getLink());
            ApiFuture<com.google.cloud.firestore.WriteResult> future = videoRef.set(video);
            future.get();

            DocumentReference movieRef  = db.collection("Movies").document(videoRequest.getMovie_id());
            ApiFuture<WriteResult> add = movieRef.update("videos", FieldValue.arrayUnion(video.getVideo_id()));
            add.get();
    
            return "Thêm video thành công!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Thêm video thất bại: " + e.getMessage();
        }
    }

    public List<Video> getAllForMideo(String Movie_id){
        List<Video> result = new ArrayList<>();
        try{
            DocumentReference movieRef = db.collection("Movies").document(Movie_id);
            ApiFuture<DocumentSnapshot> future = movieRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                List<String> videoIds = (List<String>) document.get("videos");
                for(String id: videoIds){
                    DocumentReference docRef = db.collection("Video").document(id.trim());
                    DocumentSnapshot snapshot = docRef.get().get();
                    result.add(snapshot.toObject(Video.class));
                }
                return result;
            }else{
                throw new RuntimeException("Không thể lấy danh sách video: ");
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
