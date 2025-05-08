package com.example.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feign.PostFeignClient;
import com.example.hizone.response.PostDetail;
import com.example.service.PostSearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;

@Service
public class PostSearchServiceImpl implements PostSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private PostFeignClient postFeignClient;

    @Override
    public void syncPost() throws IOException {
        elasticsearchClient.indices().delete(d -> d.index("post"));
        CreateIndexResponse createResponse = elasticsearchClient.indices().create(c -> c
                .index("post")
                .mappings(m -> m
                        .properties("postId", p -> p.long_(i -> i)) // int -> integer
                        .properties("authorId", p -> p.long_(i -> i)) // int -> integer
                        .properties("postTitle", p -> p.text(t -> t)) // String -> text
                        .properties("postContent", p -> p.text(t -> t)) // String -> text
                ));
        System.out.println("Index 'post' created: " + createResponse.acknowledged());
        List<PostDetail> posts = postFeignClient.getPush(null);
        for (PostDetail post : posts) {
            elasticsearchClient.index(i -> i
                    .index("post")
                    .id(Long.toString(post.getPostId()))
                    .document(post));
        }
    }

    @Override
    public List<PostDetail> searchPost(String keyword) throws IOException {
        // syncPost();
        GetMappingResponse mapping = elasticsearchClient.indices().getMapping(g -> g.index("post"));
        System.out.println(mapping.result().get("post").mappings().toString());
        SearchResponse<PostDetail> search = elasticsearchClient.search(s -> s
                .index("post")
                .query(q -> q
                        .multiMatch(m -> m
                                .fields("postTitle", "postContent")
                                .query(keyword))),
                PostDetail.class);
        return search.hits().hits().stream().map(hit -> hit.source()).toList();
    }
}
// .from(0).size(3)
// .sort(so -> so.field(f -> f.field("postTime").order(SortOrder.Desc)))
// .highlight(h -> h
//         .fields("postTitle", f -> f.preTags("<b>").postTags("</b>"))
//         .fields("postContent", f -> f.preTags("<b>").postTags("</b>")))
