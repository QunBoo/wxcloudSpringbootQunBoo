package com.tencent.wxcloudrun.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.model.AIChatMessage;
import com.tencent.wxcloudrun.model.AIChatTestMessage;
import com.tencent.wxcloudrun.service.impl.AIChatService;
import okhttp3.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/chat")
public class AIChatController {
    @Autowired
    private AIChatService chatService;

    @PostMapping
    public String chat(@RequestBody AIChatMessage message) throws IOException {
//        String message1 = "";
        return chatService.Chat(message);
    }

    @PostMapping("/teststr")
    public String strchat(@RequestBody AIChatTestMessage message) throws IOException {
        String msgStr = message.getMessage();
        System.out.println("<UNK>" + msgStr);
        String result = chatService.TestChat(msgStr);
        System.out.println("<OK>" + result);
        return result;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody AIChatTestMessage message){
        String msgStr = message.getMessage();
        System.out.println("StreamChat: " + msgStr);
        // 设置超时时间为 3 分钟（180000 毫秒），可根据实际需求调整
        SseEmitter emitter = new SseEmitter(180000L);

        // 设置超时回调
        emitter.onTimeout(() -> {
            System.out.println("Stream request timed out");
            emitter.complete();
        });

        // 设置完成回调
        emitter.onCompletion(() -> System.out.println("Stream request completed"));

        try {
            chatService.StreamChat(msgStr, new AIChatService.Callback() {
                @Override
                public void onData(String data) {
                    try {
//                        System.out.println(data);
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
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @PostMapping(value = "/AIStreamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter AIStreamChat(@RequestBody AIChatMessage ChatMessage) throws IOException {
        SseEmitter emitter = new SseEmitter();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            chatService.StreamAIMessageChat(ChatMessage, new AIChatService.Callback() {
                @Override
                public void onData(String data) {
                    try {
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
                                    }
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
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }


}
