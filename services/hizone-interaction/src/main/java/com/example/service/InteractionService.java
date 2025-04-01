package com.example.service;

import java.util.List;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.ForwardPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void forwardPost(ForwardPost forwardPost);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(int postId);

    void initInteraction(int postId);

    UserInteraction getUserInteraction(UserPost userPost);

    List<LikePost> getLikePostList(int postId);

    List<CollectPost> getCollectPostList(int postId);
}
