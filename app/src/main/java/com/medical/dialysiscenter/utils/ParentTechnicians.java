package com.medical.dialysiscenter.utils;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ParentTechnicians extends ExpandableGroup<ChildPatient> {
    String title,count;
    public ParentTechnicians(String title,String count, List<ChildPatient> items) {
        super( title, items );
        this.title=title;
        this.count=count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}