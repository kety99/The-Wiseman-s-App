package com.example.thewisemansapp.util;


public interface HttpCallback<T> {

    void onResponse(T response);

    void onError(Exception e);

}
