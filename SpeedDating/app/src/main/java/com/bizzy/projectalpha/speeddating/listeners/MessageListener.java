package com.bizzy.projectalpha.speeddating.listeners;

import com.bizzy.projectalpha.speeddating.models.Message;
import com.parse.ParseUser;


/**
 * Created by Wayne on 3/27/15.
 */
public interface MessageListener {
    void onMessageReceived(ParseUser sender, Message message);
}
