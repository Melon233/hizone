package com.example.hizone.request.interaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectPost {

    private Long postId;

    private Long senderId;
}
