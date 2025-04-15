package com.appxemphim.firebaseBackend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.appxemphim.firebaseBackend.dto.request.ReviewRequest;
import com.appxemphim.firebaseBackend.model.Review;
import com.appxemphim.firebaseBackend.model.Video;
import com.google.api.core.ApiFuture;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ReviewService {
    private final Firestore db = FirestoreClient.getFirestore();


    public String create(ReviewRequest request){
         try{
            Review review = new Review();
            review.setUser_id(request.getUser_id());
            review.setRating(request.getRating());
            review.setDescription(request.getDescription());
            review.setCreated_at(Timestamp.now());
            
            DocumentReference movieRef  = db.collection("Movies").document(request.getMovie_id());
            ApiFuture<WriteResult> add = movieRef.update("reviews", FieldValue.arrayUnion(review));
            add.get();

            DocumentSnapshot snapshot = movieRef.get().get();
            List<Object> reviews = (List<Object>) snapshot.get("reviews");
            int quantity = (reviews != null) ? reviews.size() : 1;
            
            Double rating = snapshot.getDouble("rating");
            if (rating == null) rating = 0.0;
            rating = ((rating*(quantity-1))+request.getRating())/quantity;
            
            ApiFuture<WriteResult> future = movieRef.update("rating", rating);
            future.get();
            return "Thêm review thành công";
         }catch( Exception e){
            return "Thêm review thất bại: "+ e.getMessage();
         }
    }

    
}
