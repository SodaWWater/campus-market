package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketMessage;
import com.liminghan.market.mapper.MarketMessageMapper;
import com.liminghan.market.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl extends ServiceImpl<MarketMessageMapper, MarketMessage> implements MessageService {

    @Override
    public MarketMessage sendMessage(Long senderId, Long receiverId, Long goodsId, Long orderId, String content) {
        MarketMessage message = new MarketMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setGoodsId(goodsId);
        message.setOrderId(orderId);
        message.setContent(content);
        message.setReadStatus("UNREAD");
        message.setCreatedAt(LocalDateTime.now());
        save(message);
        return message;
    }

    @Override
    public List<Map<String, Object>> listConversations() {
        Long userId = getCurrentUserId();
        List<MarketMessage> allMessages = lambdaQuery()
                .and(wrapper -> wrapper.eq(MarketMessage::getSenderId, userId)
                        .or().eq(MarketMessage::getReceiverId, userId))
                .orderByDesc(MarketMessage::getCreatedAt)
                .list();

        Map<Long, Map<String, Object>> conversationMap = new HashMap<>();
        for (MarketMessage msg : allMessages) {
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            if (!conversationMap.containsKey(otherUserId)) {
                Map<String, Object> conv = new HashMap<>();
                conv.put("otherUserId", otherUserId);
                conv.put("lastMessage", msg.getContent());
                conv.put("lastMessageTime", msg.getCreatedAt());
                conv.put("unreadCount", countUnreadFromUser(userId, otherUserId));
                conversationMap.put(otherUserId, conv);
            }
        }
        return new ArrayList<>(conversationMap.values());
    }

    @Override
    public List<MarketMessage> getConversation(Long otherUserId) {
        Long userId = getCurrentUserId();
        return lambdaQuery()
                .and(wrapper -> wrapper
                        .and(w -> w.eq(MarketMessage::getSenderId, userId)
                                .eq(MarketMessage::getReceiverId, otherUserId))
                        .or(w -> w.eq(MarketMessage::getSenderId, otherUserId)
                                .eq(MarketMessage::getReceiverId, userId)))
                .orderByAsc(MarketMessage::getCreatedAt)
                .list();
    }

    @Override
    public long getUnreadCount() {
        Long userId = getCurrentUserId();
        return lambdaQuery()
                .eq(MarketMessage::getReceiverId, userId)
                .eq(MarketMessage::getReadStatus, "UNREAD")
                .count();
    }

    @Override
    public void markAsRead(Long messageId) {
        MarketMessage message = getById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "message not found");
        }
        Long userId = getCurrentUserId();
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "cannot mark others message as read");
        }
        message.setReadStatus("READ");
        updateById(message);
    }

    private long countUnreadFromUser(Long currentUserId, Long otherUserId) {
        return lambdaQuery()
                .eq(MarketMessage::getSenderId, otherUserId)
                .eq(MarketMessage::getReceiverId, currentUserId)
                .eq(MarketMessage::getReadStatus, "UNREAD")
                .count();
    }

    private Long getCurrentUserId() {
        return com.liminghan.market.security.SecurityContextUtil.currentUser().getUserId();
    }
}
