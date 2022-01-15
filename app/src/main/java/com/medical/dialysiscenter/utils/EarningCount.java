package com.medical.dialysiscenter.utils;

public class EarningCount {
    Long day;
    int patientCount, earning;


    public EarningCount() {
    }

    public EarningCount(Long day, int patientCount, int earning) {
        this.day = day;
        this.patientCount = patientCount;
        this.earning = earning;
    }

    public void addStats(int patientCount, int earning) {
        this.patientCount = this.patientCount+patientCount;
        this.earning =this.earning+ earning;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public void addPatient(int patientCount) {
        this.patientCount = this.patientCount + patientCount;
    }

    public int getEarning() {
        return earning;
    }

    public void addEarning(int earning) {
        this.earning = this.earning + earning;
    }
}
