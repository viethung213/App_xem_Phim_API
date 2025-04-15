package com.appxemphim.firebaseBackend.model;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Favourite {
    String movie_id;
    Timestamp time_add;
}
