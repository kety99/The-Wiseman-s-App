package com.example.thewisemansapp.model;

import androidx.annotation.NonNull;

public class Advice {

    private Integer id;

    private String advice;

    private boolean saved;

    public Advice() {
    }

    public Advice(String advice) {
        this.advice = advice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    @NonNull
    @Override
    public String toString() {
        return advice;
    }
}
