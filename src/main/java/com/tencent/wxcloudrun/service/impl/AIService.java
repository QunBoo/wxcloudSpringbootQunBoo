package com.tencent.wxcloudrun.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.tencent.wxcloudrun.common.Callback;
import com.tencent.wxcloudrun.model.AIChatMessage;
import com.tencent.wxcloudrun.model.DeepSeekConfig;
import com.tencent.wxcloudrun.service.IAIService;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AIService implements IAIService {

    @Autowired
    private DeepSeekConfig deepSeekConfig;


    public AIService() {
        // 根据实际AI API调整baseUrl
    }

    /**
     * 手动处理postman测试发送的message字符串
     * @param messageStr：postman测试发送的message字符串
     * @param callback：handler传入的回调函数
     * @return
     */
    public String streamAIResponse(String messageStr, Callback callback) {
        // 这里调用deepSeekApi，并返回结果
        // 1. 组装AIChatMessage Chatmessage
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        AIChatMessage.Message userMessage = new AIChatMessage.Message(messageStr, "user");
        List<AIChatMessage.Message> msgList = new ArrayList<>();
        msgList.add(userMessage);
        // 构建请求体，包含与 AI 交互所需的参数
        RequestBody body = buildRequestBody(mediaType, msgList, "You are a helpful assistant",true);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getDeepSeekKey())
                .build();

        //2. 调用接口，得到AI返回的结果并返回
        client.newCall(request).enqueue(new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                callback.onComplete();
                                break;
                            }
                            callback.onData(data);
                        }
                    }
                } catch (IOException e) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }
        });

        return messageStr; // 此处返回值可根据业务需求调整（如返回处理状态）
    }



    /**
     * 处理前端组装完json的AI调用函数
     * @param jsonStr：前端组装好的json字符串
     * @param callback：handler传入的回调函数
     * @return
     */
    public String streamAIResponseJsonStr(String jsonStr, Callback callback) {
        // 1. 组装AIChatMessage Chatmessage
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        AIChatMessage.Message userMessage = new AIChatMessage.Message(jsonStr, "user");
        // 构建请求体，包含与 AI 交互所需的参数
//        RequestBody body = buildRequestBody(mediaType, msgList, "You are a helpful assistant",true);
        RequestBody body = RequestBody.create(jsonStr, mediaType);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getDeepSeekKey())
                .build();
        //2. 调用接口，得到AI返回的结果并返回
        client.newCall(request).enqueue(new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                callback.onComplete();
                                break;
                            }
                            callback.onData(data);
                        }
                    }
                } catch (IOException e) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }
        });
        return jsonStr;
    }

    /**
     * 根据消息列表、系统内容和流标志组装请求体
     * @param mediaType 请求体的媒体类型
     * @param msgList 消息列表
     * @param isStream 是否为流式请求
     * @param sysContent 系统消息内容
     * @return 组装好的请求体
     */
    private RequestBody buildRequestBody(MediaType mediaType, List<AIChatMessage.Message> msgList, String sysContent, boolean isStream) {
        StringBuilder messageJson = new StringBuilder();
        messageJson.append("{\n  \"messages\": [\n    {\n      \"content\": \"")
                .append(sysContent.replace("\"", "\\\""))
                .append("\",\n      \"role\": \"system\"\n    }");
        for (AIChatMessage.Message message : msgList) {
            messageJson.append(",\n    {\n      \"content\": \"")
                    .append(message.getContent().replace("\"", "\\\""))
                    .append("\",\n      \"role\": \"")
                    .append(message.getRole())
                    .append("\"\n    }");
        }
        messageJson.append("\n  ],\n  \"model\": \"deepseek-chat\",\n  \"frequency_penalty\": 0,\n  \"max_tokens\": 2048,\n  \"presence_penalty\": 0,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"stop\": null,\n  \"stream\": ")
                .append(isStream)
                .append(",\n  \"stream_options\": null,\n  \"temperature\": 1,\n  \"top_p\": 1,\n  \"tools\": null,\n  \"tool_choice\": \"none\",\n  \"logprobs\": false,\n  \"top_logprobs\": null\n}");
        return RequestBody.create(messageJson.toString(), mediaType);
    }

}