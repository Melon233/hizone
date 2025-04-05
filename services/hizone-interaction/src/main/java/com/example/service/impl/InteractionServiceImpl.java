package com.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CancelCollectPost;
import com.example.hizone.front.interaction.CancelLikePost;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.LikePost;
import com.example.hizone.inter.UpdateCommentCount;
import com.example.hizone.inter.UserInteraction;
import com.example.hizone.inter.UserPost;
import com.example.mapper.InteractionMapper;
import com.example.service.InteractionService;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private InteractionMapper interactionMapper;

    @Override
    public void addLikePost(LikePost postLike) {
        interactionMapper.insertPostLike(postLike);
        // interactionMapper.updateInteractionLikeCount(postLike.getPostId(), 1);
    }

    @Override
    public void addCollectPost(CollectPost collectPost) {
        interactionMapper.insertPostCollect(collectPost);
    }

    @Override
    public Interaction getInteraction(int postId) {
        return interactionMapper.selectInteractionById(postId);
    }

    @Override
    public void initInteraction(int postId) {
        interactionMapper.insertNewInteraction(postId);
    }

    @Override
    public UserInteraction getUserInteraction(UserPost userPost) {
        UserInteraction userInteraction = new UserInteraction();
        System.out.println("getUserInteraction" + userPost);
        if (interactionMapper.selectPostLike(userPost) != null) {
            userInteraction.setLiked(true);
        } else {
            userInteraction.setLiked(false);
        }
        if (interactionMapper.selectPostCollect(userPost) != null) {
            userInteraction.setCollected(true);
        } else {
            userInteraction.setCollected(false);
        }
        return userInteraction;
    }

    @Override
    public List<LikePost> getLikePostList(int postId) {
        return interactionMapper.selectPostLikeList(postId);
    }

    @Override
    public List<CollectPost> getCollectPostList(int postId) {
        return interactionMapper.selectPostCollectList(postId);
    }

    @Override
    public void cancelLikePost(CancelLikePost cancelLikePost) {
        interactionMapper.deletePostLike(cancelLikePost);
    }

    @Override
    public void cancelCollectPost(CancelCollectPost cancelCollectPost) {
        interactionMapper.deletePostCollect(cancelCollectPost);
    }

    @Override
    public void updatePostLikeCount(int postId, int increment) {
        interactionMapper.updateInteractionLikeCount(postId, increment);
    }

    @Override
    public void updatePostCollectCount(int postId, int increment) {
        interactionMapper.updateInteractionCollectCount(postId, increment);
    }

    @Override
    public void updateCommentCount(UpdateCommentCount updateCommentCount) {
        interactionMapper.updateCommentCount(updateCommentCount);
    }
}
