package com.example.hizone.dto;

import lombok.Data;

@Data
public class UpdateUserMetadata {

    private Long userId;

    private String type;

    private Boolean isIncrement;
}
