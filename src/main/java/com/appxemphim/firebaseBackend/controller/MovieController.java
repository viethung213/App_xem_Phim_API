package com.appxemphim.firebaseBackend.controller;

import com.appxemphim.firebaseBackend.dto.request.MovieRequest;
import com.appxemphim.firebaseBackend.dto.response.MovieDTO;
import com.appxemphim.firebaseBackend.exception.ResourceNotFoundException;
import com.appxemphim.firebaseBackend.model.Movie;
import com.appxemphim.firebaseBackend.service.MovieService;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final Client meiliClient;
    private final MovieService movieService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<Page<Movie>> getMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<Integer> years,
            @RequestParam(required = false) List<String> nations,
            @RequestParam(defaultValue = "0.0") @Min(0) Double minRating,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        logger.info("Fetching movies with filters: title={}, genres={}, years={}, nations={}, minRating={}",
                title, genres, years, nations, minRating);
        Pageable pageable = PageRequest.of(page, size);

        // If title provided, search via MeiliSearch
        if (StringUtils.hasText(title)) {

            Page<Movie> pageResult = movieService.searchWithMeili(title, pageable);
            return ResponseEntity.ok(pageResult);
        }

        // Otherwise fallback to Firestore logic
        try {
            Page<Movie> movies = movieService.searchMovies(title, genres, years, nations, minRating, pageable);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            logger.error("Error searching movies with filters: title={}, genres={}, years={}, nations={}, minRating={}",
                    title, genres, years, nations, minRating, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty(pageable));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable String id) {
        logger.info("Fetching movie with ID: {}", id);
        try {
            MovieDTO movieDTO = movieService.getMovieDTOById(id);
            return ResponseEntity.ok(movieDTO);
        } catch (ResourceNotFoundException e) {
            logger.warn("Movie not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error fetching movie with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> createMovie(@Valid @RequestBody MovieRequest movieRequest) {
        logger.info("Creating new movie with title: {}", movieRequest.getTitle());
        String response = movieService.create(movieRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/{detailType}")
    public ResponseEntity<?> getMovieDetails(@PathVariable String id, @PathVariable String detailType) {
        logger.info("Fetching {} for movie ID: {}", detailType, id);
        try {
            Object details = movieService.getMovieDetails(id, detailType);
            return ResponseEntity.ok(details);
        } catch (ResourceNotFoundException e) {
            logger.warn("Movie not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error fetching details for movie ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/by-video/{videoId}")
    public ResponseEntity<Movie> getMovieByVideoId(@PathVariable String videoId) {
        logger.info("Fetching movie by videoId: {}", videoId);
        try {
            Movie movie = movieService.getMovieByVideoId(videoId);
            return ResponseEntity.ok(movie);
        } catch (ResourceNotFoundException e) {
            logger.warn("Movie not found with videoId: {}", videoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error fetching movie by videoId: {}", videoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
