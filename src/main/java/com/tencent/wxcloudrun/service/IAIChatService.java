package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.AIChatMessage;


import java.io.IOException;

public interface IAIChatService {
    String Chat(AIChatMessage message) throws IOException;
    String TestChat(String message) throws IOException;
}
