package com.appxemphim.firebaseBackend.model;

import com.google.cloud.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Movie {
    private String movie_Id;
    private String title;           //tên của bộ phim
    private String description;     // mô tả của bộ phim
    private String poster_url;      // link ảnh poster
    private String trailer_url;     // link trailer của bộ phim
    private double rating;           // đánh giá chung của phim
    private String nation;          // bộ phim thuộc quốc gia nào
    private Timestamp  created_at;        // thời gian tạo phim
}
