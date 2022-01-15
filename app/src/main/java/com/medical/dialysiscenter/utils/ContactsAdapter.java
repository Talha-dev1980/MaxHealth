package com.medical.dialysiscenter.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medical.dialysiscenter.R;

import java.util.List;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<EarningCount> mEarningList;

    // Pass in the contact array into the constructor
    public ContactsAdapter(List<EarningCount> list) {
        mEarningList = list;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from( context );

        View contactView = inflater.inflate( R.layout.item_visit, parent, false );

        ViewHolder viewHolder = new ViewHolder( contactView );
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        EarningCount earningCount = mEarningList.get( position );

        MeasureToday measureToday = new MeasureToday();
        // Set item views based on your views and data model
        holder.tvId.setText( "Earning" );
        holder.tvTech.setText( earningCount.getEarning() + "/-" );
        holder.tvdate.setText( measureToday.getMillisToDate( Long.parseLong( earningCount.getDay() + "" ) ) );
        holder.tvName.setText( "Patients " + earningCount.getPatientCount() + "" );

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mEarningList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView tvName, tvId, tvTech, tvdate;


        public ViewHolder(View itemView) {
            super( itemView );
            mView = itemView;
            tvName = (TextView) mView.findViewById( R.id.tvPatientNameVisit );
            tvId = (TextView) mView.findViewById( R.id.tvPidVisit );
            tvdate = (TextView) mView.findViewById( R.id.tvTimDateVisit );
            tvTech = (TextView) mView.findViewById( R.id.tvTechTreatedBy );
        }
    }
}
