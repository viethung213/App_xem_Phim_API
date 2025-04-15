package com.appxemphim.firebaseBackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Video {
    private String video_id;
    private String video_url;
    private long duration;
    private int view;
}
