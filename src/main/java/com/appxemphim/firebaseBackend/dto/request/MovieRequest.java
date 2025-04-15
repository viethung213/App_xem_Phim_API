package com.appxemphim.firebaseBackend.dto.request;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieRequest {
    private String title;           //tên của bộ phim
    private String description;     // mô tả của bộ phim
    private String poster_url;      // link ảnh poster
    private String trailer_url;     // link trailer của bộ phim
    private double rating;           // đánh giá chung của phim
    private String nation;          // bộ phim thuộc quốc gia nào
    private Timestamp  created_at;        // thời gian tạo phim
}
