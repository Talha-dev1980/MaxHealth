package com.medical.dialysiscenter.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class ParentTechHolder extends GroupViewHolder {

    public TextView tvTechName, tvPateintCount;
    String parentName = null;

    public ParentTechHolder(View itemView) {
        super( itemView );
        tvTechName = (TextView) itemView.findViewById( R.id.tvTechName );
        tvPateintCount = (TextView) itemView.findViewById( R.id.tvPatientCountPL );
    }

    public void setParentTitle(ExpandableGroup group) {

    }


    public void setParent(String title, String count) {

        FirebaseDatabase.getInstance().getReference().child( "Techs" ).child( title ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( "TechName" )) {
                    parentName = snapshot.child( "TechName" ).getValue().toString().trim();
                    tvTechName.setText( parentName );
                    tvPateintCount.setText( count );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );


    }
}
