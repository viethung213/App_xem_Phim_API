package com.appxemphim.firebaseBackend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.appxemphim.firebaseBackend.model.Genres;
import com.appxemphim.firebaseBackend.model.Movie;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;


@Service
public class GenresService {

    private final Firestore db = FirestoreClient.getFirestore();
    
    public List<Genres> getAllForMovie(String moivie_id){
        List<Genres> result = new ArrayList<>();
        try{
            CollectionReference movieGenresRef = db.collection("Movie_Genres");
            Query query = movieGenresRef.whereEqualTo("movive_Id", moivie_id);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()){
                String genresId = document.getString("genres_id");
                DocumentReference genresRef = db.collection("Genres").document(genresId);
                ApiFuture<DocumentSnapshot> future = genresRef.get();
                DocumentSnapshot genresDoc = future.get();
                if(genresDoc.exists()){
                    Genres genres = genresDoc.toObject(Genres.class);
                    result.add(genres);
                }
            }
            return result;
        }catch ( Exception e){
            throw new RuntimeException("Lỗi khi lấy thể loại: "+ e.getMessage());
        }
    }

    public List<Genres> getAllFGenres(){
        try{
            ApiFuture<QuerySnapshot> future = db.collection("Genres").get();      
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Genres> genresList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : documents) {
                Genres genres = doc.toObject(Genres.class);
                genresList.add(genres);
            }
            return genresList;
        }catch( Exception e){
            throw new RuntimeException("Lỗi khi lấy danh sách thể loại: "+ e.getMessage());
        }
       
    }
    

}
