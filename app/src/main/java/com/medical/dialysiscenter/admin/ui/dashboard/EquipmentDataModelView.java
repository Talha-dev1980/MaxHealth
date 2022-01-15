package com.medical.dialysiscenter.admin.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EquipmentDataModelView extends ViewModel {

    private MutableLiveData<String> mText;

    public EquipmentDataModelView() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is dashboard fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}