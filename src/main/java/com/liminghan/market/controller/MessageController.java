package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.SendMessageRequest;
import com.liminghan.market.entity.MarketMessage;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.MessageService;
import com.liminghan.market.vo.UnreadCountVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Send message")
    @PostMapping
    public Result<MarketMessage> send(@Valid @RequestBody SendMessageRequest request) {
        Long senderId = SecurityContextUtil.currentUser().getUserId();
        return Result.success(messageService.sendMessage(
                senderId, request.getReceiverId(),
                request.getGoodsId(), request.getOrderId(),
                request.getContent()));
    }

    @Operation(summary = "List conversations")
    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> conversations() {
        return Result.success(messageService.listConversations());
    }

    @Operation(summary = "Get conversation with user")
    @GetMapping("/conversations/{userId}")
    public Result<List<MarketMessage>> conversation(@PathVariable Long userId) {
        return Result.success(messageService.getConversation(userId));
    }

    @Operation(summary = "Unread count")
    @GetMapping("/unread-count")
    public Result<UnreadCountVO> unreadCount() {
        return Result.success(new UnreadCountVO(messageService.getUnreadCount()));
    }

    @Operation(summary = "Mark message as read")
    @PutMapping("/{id}/read")
    public Result<String> read(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success("ok");
    }
}
