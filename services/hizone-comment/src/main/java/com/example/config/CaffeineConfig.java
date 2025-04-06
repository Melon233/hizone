package com.example.config;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hizone.dao.comment.Comment;
import com.example.hizone.dao.comment.Reply;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CaffeineConfig {

    @Bean
    Cache<String, Object> caffeinCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    System.out.println("key: " + key + " value: " + value + " cause: " + cause);
                })
                .build();
    }

    @Bean
    Cache<Integer, TreeSet<Comment>> caffeineCommentSet() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    System.out.println("key: " + key + " value: " + value + " cause: " + cause);
                })
                .build();
    }

    @Bean
    Cache<Integer, TreeSet<Reply>> caffeineReplySet() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    System.out.println("key: " + key + " value: " + value + " cause: " + cause);
                })
                .build();
    }
}
