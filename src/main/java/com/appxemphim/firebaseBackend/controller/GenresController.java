package com.appxemphim.firebaseBackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.appxemphim.firebaseBackend.model.Genres;
import com.appxemphim.firebaseBackend.service.GenresService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final GenresService genresService;

    @GetMapping("/{movie_id}")
    public List<Genres> findAllForMovie(@PathVariable String movie_id) {
        return genresService.getAllForMovie(movie_id);
    }
    
}
