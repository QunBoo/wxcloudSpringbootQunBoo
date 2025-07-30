package com.tencent.wxcloudrun.common;

import okhttp3.Call;

import java.io.IOException;

public interface Callback {
    void onData(String data);
    void onFailure(Call call, IOException e);
    void onComplete();
}
