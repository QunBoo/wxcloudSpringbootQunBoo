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
        //    TODO:记得删除API Key再提交代码
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

        // 构建请求体，包含与 AI 交互所需的参数
        RequestBody body = RequestBody.create(mediaType, "{" +
                "\n  \"messages\": [" +
                "\n    {" +
                "\n      \"content\": \"You are a helpful assistant\"," +
                "\n      \"role\": \"system\"" +
                "\n    }," +
                "\n    {" +
                "\n      \"content\": \"" + messageStr + "\"," +
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

    public void StreamChat(String message, Callback callback) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder().build();
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
                "\n  \"stream\": true," +
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
    public interface Callback {
        void onData(String data);
        void onFailure(Call call, IOException e);
        void onComplete();
    }
}
