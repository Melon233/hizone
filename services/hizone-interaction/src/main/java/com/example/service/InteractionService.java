package com.example.service;

import java.util.List;

import com.example.hizone.dto.UpdateCommentCount;
import com.example.hizone.dto.UserInteraction;
import com.example.hizone.dto.UserPost;
import com.example.hizone.request.interaction.CancelCollectPost;
import com.example.hizone.request.interaction.CancelLikePost;
import com.example.hizone.request.interaction.CollectPost;
import com.example.hizone.request.interaction.LikePost;
import com.example.hizone.table.interaction.Interaction;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(Long postId);

    void initInteraction(Long postId);

    UserInteraction getUserInteraction(UserPost userPost);

    List<LikePost> getLikePostList(Long postId);

    List<CollectPost> getCollectPostList(Long postId);

    void cancelLikePost(CancelLikePost cancelLikePost);

    void cancelCollectPost(CancelCollectPost cancelCollectPost);

    void updatePostLikeCount(Long postId, Long increment);

    void updatePostCollectCount(Long postId, Long increment);

    void updateCommentCount(UpdateCommentCount updateCommentCount);
}
