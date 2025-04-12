package com.example.hizone.dto;

import lombok.Data;

@Data
public class CheckCode {

    private String email;

    private Integer checkCode;

    private Long expireTime;
}
