package com.example.service;

import com.example.fenta.dao.interaction.Interaction;
import com.example.fenta.front.interaction.CollectPost;
import com.example.fenta.front.interaction.ForwardPost;
import com.example.fenta.front.interaction.LikePost;

public interface InteractionService {

    void addLikePost(LikePost postLike);

    void forwardPost(ForwardPost forwardPost);

    void addCollectPost(CollectPost collectPost);

    Interaction getInteraction(int postId);
}
