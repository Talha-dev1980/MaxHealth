package com.medical.dialysiscenter.admin.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.admin.SaleFixDuration;
import com.medical.dialysiscenter.admin.SaleOneDay;
import com.medical.dialysiscenter.utils.MeasureToday;

public class EarningsFrag extends Fragment {

    private HomeViewModel homeViewModel;
    private int earningToday = 0, patientsToday = 0, earningYester = 0, patientsYester = 0, earningMonth = 0, patientsMonth = 0;
    MeasureToday measureToday = new MeasureToday();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider( this ).get( HomeViewModel.class );
        View root = inflater.inflate( R.layout.fragment_earnings, container, false );
        try {
            root.findViewById( R.id.cardToday ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent();
                    MeasureToday dayMeasure = new MeasureToday();
                    int day = dayMeasure.getCurrentDay();
                    String todayMillis = dayMeasure.getTodayinMillisec( day );
                    it.putExtra( "day", todayMillis );
                    it.setClass( getActivity(), SaleOneDay.class );
                    getActivity().startActivity( it );
                }
            } );
            root.findViewById( R.id.cardYesterday ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent();
                    MeasureToday dayMeasure = new MeasureToday();
                    int day = dayMeasure.getCurrentDay();
                    String todayMillis = dayMeasure.getTodayinMillisec( day - 1 );
                    it.putExtra( "day", todayMillis );
                    it.setClass( getActivity(), SaleOneDay.class );
                    getActivity().startActivity( it );
                }
            } );
            root.findViewById( R.id.cardThisMonth ).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent();
                    it.setClass( getActivity(), SaleFixDuration.class );
                    getActivity().startActivity( it );
                }
            } );
        } catch (Exception e) {
            Log.e( "EarningFrag", e.getMessage() );
        }
        return root;
    }

}