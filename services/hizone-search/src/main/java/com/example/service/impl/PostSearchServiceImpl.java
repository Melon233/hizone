package com.example.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.feign.PostFeignClient;
import com.example.fenta.dao.post.Post;
import com.example.service.PostSearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;

public class PostSearchServiceImpl implements PostSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private PostFeignClient postFeignClient;

    @Override
    public void syncPost() throws IOException {
        List<Post> posts = postFeignClient.getPush();
        for (Post post : posts) {
            elasticsearchClient.index(i -> i
                    .index("post")
                    .id(Integer.toString(post.getPostId()))
                    .document(post));
        }
    }

    @Override
    public List<Post> searchPost(String keyword) throws IOException {
        SearchResponse<Post> search = elasticsearchClient.search(s -> s
                .index("post")
                .query(q -> q
                        .multiMatch(m -> m
                                .fields("postTitle", "postContent")
                                .query(keyword)))
                .from(0).size(3)
                .sort(so -> so.field(f -> f.field("postTime").order(SortOrder.Desc)))
                .highlight(h -> h
                        .fields("postTitle", f -> f.preTags("<b>").postTags("</b>"))
                        .fields("postContent", f -> f.preTags("<b>").postTags("</b>"))),
                Post.class);
        return search.hits().hits().stream().map(hit -> hit.source()).toList();
    }
}
