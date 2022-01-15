package com.medical.dialysiscenter.utils;

public class RequestModel {
    String email, name, request, time;

    public RequestModel() {
    }

    public RequestModel(String email, String name, String request, String time) {
        this.email = email;
        this.name = name;
        this.request = request;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
