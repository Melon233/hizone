package com.example.service;

import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.front.interaction.CollectPost;
import com.example.fenta.front.interaction.ForwardPost;
import com.example.fenta.front.interaction.LikePost;
import com.example.fenta.inter.UserInteraction;
import com.example.fenta.inter.UserPost;
import com.example.fenta.outer.InteractionDetail;

import co.elastic.clients.elasticsearch.security.User;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void forwardPost(ForwardPost forwardPost);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(int postId);

    void initInteraction(int postId);

    UserInteraction getUserInteraction(UserPost userPost);
}
