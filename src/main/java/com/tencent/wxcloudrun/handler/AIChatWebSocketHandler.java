package com.tencent.wxcloudrun.handler;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.common.Callback;
import com.tencent.wxcloudrun.service.IAIService;
import com.tencent.wxcloudrun.service.impl.AIChatService;
import okhttp3.Call;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AIChatWebSocketHandler extends TextWebSocketHandler {

    private final IAIService aiService;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public AIChatWebSocketHandler(IAIService aiService) {
        this.aiService = aiService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        session.sendMessage(new TextMessage("Connected to AI chat service. Send your message."));
    }

    /**
     * 处理文本消息
     *
     * @param session  WebSocket会话
     * @param message  文本消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SseEmitter emitter = new SseEmitter();
        ObjectMapper objectMapper = new ObjectMapper();
        String messageStr = message.getPayload();

        // 发送确认消息
//        session.sendMessage(new TextMessage("Processing your message: " + messageStr));
        // 调用AI服务并流式返回结果：JsonStr用于前后端联调，aiService.streamAIResponse函数用于postman测试
        String aiResponse = aiService.streamAIResponseJsonStr(messageStr, new Callback() {
//        String aiResponse = aiService.streamAIResponse(messageStr, new Callback() {
            @Override
            public void onData(String data) {
                try {
//                    System.out.println(data);
                    JsonNode rootNode = objectMapper.readTree(data);
                    JsonNode choicesNode = rootNode.path("choices");
                    if (!choicesNode.isMissingNode() && choicesNode.isArray()) {
                        for (JsonNode choice : choicesNode) {
                            JsonNode deltaNode = choice.path("delta");
                            if (!deltaNode.isMissingNode()) {
                                JsonNode contentNode = deltaNode.path("content");
                                if (!contentNode.isMissingNode()) {
                                    String content = contentNode.asText();
                                    // 在终端打印 delta.content 部分
                                    System.out.println(content);
                                    session.sendMessage(new TextMessage(content));
                                }
                            }
                            JsonNode finishReasonNode = choice.path("finish_reason");
                            if (!finishReasonNode.isMissingNode() && finishReasonNode.asText().equals("stop")) {
                                String finishReason = finishReasonNode.asText();
                                String overtextMessage = "[OVER]";
//                                System.out.println("[OVER]" + finishReason);
                                session.sendMessage(new TextMessage(overtextMessage));
                            }
                        }
                    }
                    emitter.send(SseEmitter.event().data(data));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                emitter.completeWithError(e);
            }

            @Override
            public void onComplete() {
                emitter.complete();
            }
        });
//        session.sendMessage(new TextMessage(aiResponse));
    }

    /**
     * 处理连接关闭
     *
     * @param session  WebSocket会话
     * @param status   关闭状态
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    /**
     * 处理传输错误
     *
     * @param session   WebSocket会话
     * @param exception 异常
     * @throws Exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session.getId());
        session.close(CloseStatus.SERVER_ERROR);
    }
}