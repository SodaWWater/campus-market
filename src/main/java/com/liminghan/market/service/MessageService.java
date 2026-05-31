package com.liminghan.market.service;

import com.liminghan.market.entity.MarketMessage;

import java.util.List;
import java.util.Map;

public interface MessageService {

    MarketMessage sendMessage(Long senderId, Long receiverId, Long goodsId, Long orderId, String content);

    List<Map<String, Object>> listConversations();

    List<MarketMessage> getConversation(Long otherUserId);

    long getUnreadCount();

    void markAsRead(Long messageId);
}
