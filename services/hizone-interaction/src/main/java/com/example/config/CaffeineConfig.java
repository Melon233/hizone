package com.example.config;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
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
    Cache<String, Set<LikePost>> caffeineCacheLikePostList() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    System.out.println("key: " + key + " value: " + value + " cause: " + cause);
                })
                .build();
    }

    @Bean
    Cache<String, Set<CollectPost>> caffeineCacheCollectPostList() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    System.out.println("key: " + key + " value: " + value + " cause: " + cause);
                })
                .build();
    }
}
