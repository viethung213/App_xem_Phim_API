package com.appxemphim.firebaseBackend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {
    String Movie_id;
    String Parent_comment_id;
    String Content;
}
