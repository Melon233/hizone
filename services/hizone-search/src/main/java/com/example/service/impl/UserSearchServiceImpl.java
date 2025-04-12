package com.example.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feign.UserFeignClient;
import com.example.hizone.table.post.Post;
import com.example.hizone.table.user.User;
import com.example.service.UserSearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class UserSearchServiceImpl implements UserSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public void syncUser() throws IOException {
        List<Post> posts = userFeignClient.getAllUser();
        for (Post post : posts) {
            elasticsearchClient.index(i -> i
                    .index("post")
                    .id(Long.toString(post.getPostId()))
                    .document(post));
        }
    }

    @Override
    public List<User> searchUser(String keyword) throws IOException {
        syncUser();
        SearchResponse<User> search = elasticsearchClient.search(s -> s
                .index("user")
                .query(q -> q
                        .match(t -> t
                                .field("nickname")
                                .query(keyword)))
                .from(0).size(3)
                .sort(f -> f.field(o -> o.field("registerTime").order(SortOrder.Desc)))
                .highlight(h -> h.fields("nickname", f -> f.preTags("<b>").postTags("</b>"))),
                User.class);
        return search.hits().hits().stream().map(hit -> hit.source()).toList();
    }
}
