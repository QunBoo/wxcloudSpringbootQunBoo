package com.tencent.wxcloudrun.controller;

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
        SseEmitter emitter = new SseEmitter();
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


}
