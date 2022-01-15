package com.medical.dialysiscenter.utils;

public class PatientModel {
    String name, amount, id, time, tech;

    public PatientModel() {
    }


    public PatientModel(String name, String amount, String id, String time, String tech) {
        this.name = name;
        this.amount = amount;
        this.id = id;
        this.time = time;
        this.tech = tech;
    }


    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

