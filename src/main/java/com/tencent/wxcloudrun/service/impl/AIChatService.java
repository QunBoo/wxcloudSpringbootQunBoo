package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.model.AIChatMessage;
import com.tencent.wxcloudrun.model.DeepSeekConfig;
import com.tencent.wxcloudrun.service.IAIChatService;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatService implements IAIChatService {
    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Override
    public String Chat(AIChatMessage message) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        // 构建请求体，包含与 AI 交互所需的参数
        RequestBody body = RequestBody.create(mediaType, "{" +
                "\n  \"messages\": [" +
                "\n    {" +
                "\n      \"content\": \"You are a helpful assistant\"," +
                "\n      \"role\": \"system\"" +
                "\n    }," +
                "\n    {" +
                "\n      \"content\": \"" + message + "\"," +
                "\n      \"role\": \"user\"" +
                "\n    }" +
                "\n  ]," +
                "\n  \"model\": \"deepseek-chat\"," +
                "\n  \"frequency_penalty\": 0," +
                "\n  \"max_tokens\": 2048," +
                "\n  \"presence_penalty\": 0," +
                "\n  \"response_format\": {" +
                "\n    \"type\": \"text\"" +
                "\n  }," +
                "\n  \"stop\": null," +
                "\n  \"stream\": false," +
                "\n  \"stream_options\": null," +
                "\n  \"temperature\": 1," +
                "\n  \"top_p\": 1," +
                "\n  \"tools\": null," +
                "\n  \"tool_choice\": \"none\"," +
                "\n  \"logprobs\": false," +
                "\n  \"top_logprobs\": null" +
                "\n}");
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getDeepSeekKey())
                .build();
        Response response = client.newCall(request).execute();
        String responseData = "";
        if (response.body() != null) {
            responseData = response.body().string();
        }
        return responseData;
    }

    @Override
    public String TestChat(String messageStr) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        AIChatMessage.Message userMessage = new AIChatMessage.Message(messageStr, "user");
        List<AIChatMessage.Message> msgList = new ArrayList<>();
        msgList.add(userMessage);
        // 构建请求体，包含与 AI 交互所需的参数
        RequestBody body = buildRequestBody(mediaType, msgList, "You are a helpful assistant",false);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getDeepSeekKey())
                .build();
        Response response = client.newCall(request).execute();
        String responseData = "";
        if (response.body() != null) {
            responseData = response.body().string();
        }
        return responseData;
    }

    public void StreamChat(String message, Callback callback) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        AIChatMessage.Message userMessage = new AIChatMessage.Message(message, "user");
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
    }

    public void StreamAIMessageChat(AIChatMessage Chatmessage, Callback callback) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        List<AIChatMessage.Message>  msgList = Chatmessage.getMessages();
        // 构建请求体，包含与 AI 交互所需的参数
        RequestBody body = buildRequestBody(mediaType, msgList, "You are a helpful assistant",true);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getDeepSeekKey())
                .build();
        // 发送请求并处理响应
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


    public interface Callback {
        void onData(String data);
        void onFailure(Call call, IOException e);
        void onComplete();
    }
}
