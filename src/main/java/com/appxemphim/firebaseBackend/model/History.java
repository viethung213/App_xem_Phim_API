package com.appxemphim.firebaseBackend.model;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class History {
    private String Video_id;
    private double Person_view;
    private Timestamp Updated_at;
}
