package com.example.service;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.ForwardPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;
import com.example.hizone.outer.InteractionDetail;

import co.elastic.clients.elasticsearch.security.User;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void forwardPost(ForwardPost forwardPost);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(int postId);

    void initInteraction(int postId);

    UserInteraction getUserInteraction(UserPost userPost);
}
