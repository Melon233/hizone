package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hizone.dao.interaction.Interaction;
import com.example.hizone.front.interaction.CollectPost;
import com.example.hizone.front.interaction.ForwardPost;
import com.example.hizone.front.interaction.LikePost;
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
    }

    @Override
    public void forwardPost(ForwardPost forwardPost) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forwardPost'");
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
}
