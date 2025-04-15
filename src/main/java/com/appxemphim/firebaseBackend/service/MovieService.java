package com.appxemphim.firebaseBackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.appxemphim.firebaseBackend.Utilities.GoogleUtilities;
import com.appxemphim.firebaseBackend.dto.request.MovieRequest;
import com.appxemphim.firebaseBackend.dto.response.MovieDTO;
import com.appxemphim.firebaseBackend.model.Movie;
import com.appxemphim.firebaseBackend.model.Review;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.time.ZoneId;
import java.util.Date;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final GoogleUtilities googleUtilities;
    private final VideoService videoService;
    private final PersonService personService;
    private final GenresService genresService;
    private final Firestore db = FirestoreClient.getFirestore();

    public MovieDTO getMovieById(String id) throws Exception {
        try{
            DocumentReference docRef = db.collection("Movies").document(id.trim());
            DocumentSnapshot snapshot = docRef.get().get();

        if (!snapshot.exists()) {
            throw new RuntimeException("Movie not found");
        }

        Movie movie = snapshot.toObject(Movie.class);
        MovieDTO movieDTO = new MovieDTO();
        BeanUtils.copyProperties(movie, movieDTO); // ánh xạ toàn bộ field có cùng tên
        movieDTO.setActors(personService.findALLActorForMovie(id, "Movie_Actor"));
        movieDTO.setDirectors(personService.findALLActorForMovie(id, "Movie_Director"));
        movieDTO.setReviews((List<Review>) snapshot.get("reviews"));
        movieDTO.setVideos(videoService.getAllForMideo(id));
        movieDTO.setGenres(genresService.getAllForMovie(id));
        return movieDTO;
        }catch( Exception e){
            throw new RuntimeException("Lỗi khi thêm movie: "+ e.getMessage());
        }
        
    }
    
    
    public List<Movie> findAll() throws Exception {
        ApiFuture<QuerySnapshot> future = db.collection("Movies").get();      
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Movie> movieList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Movie movie = doc.toObject(Movie.class);
            movie.setMovie_Id(doc.getId());
            movieList.add(movie);
        }
        return movieList;
    }

    public String create(MovieRequest movieRequet){
        try {
            Movie movie = new Movie();
            DocumentReference docRef = db.collection("Movies").document();
    
            movie.setMovie_Id(docRef.getId());
            movie.setTitle(movieRequet.getTitle());
            movie.setDescription(movieRequet.getDescription());
            movie.setPoster_url(googleUtilities.exportLink(movieRequet.getPoster_url()));
            movie.setTrailer_url(googleUtilities.exportLink(movieRequet.getTrailer_url()));
            movie.setRating(movieRequet.getRating());
            movie.setNation(movieRequet.getNation());
            movie.setCreated_at(movieRequet.getCreated_at());
    
            ApiFuture<com.google.cloud.firestore.WriteResult> future = docRef.set(movie);
            future.get();
    
            return "Thêm phim thành công!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Thêm phim thất bại: " + e.getMessage();
        }
    }

    public List<Movie> getMovieByGenres(String genres_id){
        List<Movie> result = new ArrayList<>();
        try{
            CollectionReference movieGenresRef = db.collection("Movie_Genres");
            Query query = movieGenresRef.whereEqualTo("genres_id", genres_id);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()){
                String movie_id = document.getString("movive_Id");
                DocumentReference genresRef = db.collection("Movies").document(movie_id);
                ApiFuture<DocumentSnapshot> future = genresRef.get();
                DocumentSnapshot genresDoc = future.get();
                if(genresDoc.exists()){
                    Movie movie = genresDoc.toObject(Movie.class);
                    result.add(movie);
                }
            }
            return result;
        }catch( Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Movie> getMovieByYear(Integer year){
        List<Movie> result = new ArrayList<>();
        try{
            LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
            LocalDateTime startOfNextYear = LocalDateTime.of(year + 1, 1, 1, 0, 0);
            Timestamp startTimestamp = Timestamp.of(Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant()));
            Timestamp endTimestamp = Timestamp.of(Date.from(startOfNextYear.atZone(ZoneId.systemDefault()).toInstant()));
            CollectionReference movieRef = db.collection("Movies");
            Query query = movieRef.whereGreaterThanOrEqualTo("created_at", startTimestamp).whereLessThan("created_at", endTimestamp);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()){
                Movie movie = document.toObject(Movie.class);
                result.add(movie);
            }
            return result;
        }catch( Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
