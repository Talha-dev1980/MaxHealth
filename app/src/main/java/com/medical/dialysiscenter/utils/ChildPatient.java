package com.medical.dialysiscenter.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

public class ChildPatient implements Parcelable {

    private String childPatientKey, name, id, bill, time;


    public ChildPatient(DataSnapshot snapshot) {
        this.id = snapshot.child( "id" ).getValue().toString();
        this.name = snapshot.child( "name" ).getValue().toString();
        this.bill = snapshot.child( "amount" ).getValue().toString();
        this.time = snapshot.child( "time" ).getValue().toString();

    }

    protected ChildPatient(Parcel in) {
        id = in.readString();
        name = in.readString();
        bill = in.readString();
        time = in.readString();
    }

    public static final Creator<ChildPatient> CREATOR = new Creator<ChildPatient>() {
        @Override
        public ChildPatient createFromParcel(Parcel in) {
            return new ChildPatient( in );
        }

        @Override
        public ChildPatient[] newArray(int size) {
            return new ChildPatient[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( id );
    }
}
