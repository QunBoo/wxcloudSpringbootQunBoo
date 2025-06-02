package com.tencent.wxcloudrun.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DeepSeekConfig {
    @Value("${deepseek.key}")
    private String deepSeekKey;

    public String getDeepSeekKey() {
        return deepSeekKey;
    }
}
