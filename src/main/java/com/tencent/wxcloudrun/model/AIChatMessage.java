package com.tencent.wxcloudrun.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// AIChatMessage 类用于封装前端传来的聊天请求数据
public class AIChatMessage {
    // messages 字段的 setter 方法
    // 聊天消息列表
    @Setter
    @Getter
    private List<Message> messages;
    // model 字段的 setter 方法
    // model 字段的 getter 方法
    // 聊天使用的模型名称
    @Setter
    @Getter
    private String model;
    // chatType 字段的 setter 方法
    // chatType 字段的 getter 方法
    // 聊天类型
    @Setter
    @Getter
    private String chatType;

    // 内部类 Message 用于表示单条聊天消息
    @Data
    public static class Message {
        // role 字段的 setter 方法
        // role 字段的 getter 方法
        // 消息的角色，如 "user", "system" 等
        private String role;
        // content 字段的 setter 方法
        // content 字段的 getter 方法
        // 消息的内容
        private String content;

        public Message(String contentstr, String rolestr) {
            this.content = contentstr;
            this.role = rolestr;
        }
    }


}
