package com.example.service;

import java.util.List;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CancelCollectPost;
import com.example.hizone.front.interaction.CancelLikePost;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(int postId);

    void initInteraction(int postId);

    UserInteraction getUserInteraction(UserPost userPost);

    List<LikePost> getLikePostList(int postId);

    List<CollectPost> getCollectPostList(int postId);

    void cancelLikePost(CancelLikePost cancelLikePost);

    void cancelCollectPost(CancelCollectPost cancelCollectPost);

    void updatePostLikeCount(int postId, int increment);

    void updatePostCollectCount(int postId, int increment);

    void updateCommentCount(UpdateCommentCount updateCommentCount);
}
