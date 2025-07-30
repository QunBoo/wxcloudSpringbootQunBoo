package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.common.Callback;
import org.springframework.web.socket.WebSocketSession;

public interface IAIService {
    String streamAIResponse(String userMessage, Callback callback);
    String streamAIResponseJsonStr(String jsonStr, Callback callback);
}
