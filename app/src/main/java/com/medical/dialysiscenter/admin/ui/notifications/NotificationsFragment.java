package com.medical.dialysiscenter.admin.ui.notifications;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.NotificationManager;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private ProgressDialog dialog;
    private RecyclerView notifList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


            notificationsViewModel =
                    new ViewModelProvider( this ).get( NotificationsViewModel.class );
            View root = inflater.inflate( R.layout.fragment_notifications, container, false );
        try {
            notifList = (RecyclerView) root.findViewById( R.id.listnotifications );
            LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
            layoutManager.setReverseLayout( true );
            layoutManager.setStackFromEnd( true );
            notifList.setLayoutManager( layoutManager );
            dialog = new ProgressDialog( getActivity() );
            dialog.setTitle( "loading" );
            dialog.show();
        } catch (Exception e) {
            Log.e( "notifFrag", e.getMessage() );
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child( "Notifications" ).child( "Admin" ).orderByKey().limitToLast( 20 );
        FirebaseRecyclerAdapter<NotificationManager, UsersViewHolder> adapter
                = new FirebaseRecyclerAdapter<NotificationManager, UsersViewHolder>(

                NotificationManager.class, R.layout.item_notification, UsersViewHolder.class, firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, NotificationManager notif, int i) {
                MeasureToday measureToday = new MeasureToday();
                holder.tvMessage.setText( notif.getMessage() + "" );
                holder.tvDate.setText( measureToday.getTimeFromMillis( Long.parseLong( notif.getTime() ) ) );
            }
        };
        notifList.setAdapter( adapter );
        dialog.dismiss();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvMessage, tvDate;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvMessage = (TextView) mView.findViewById( R.id.notifMessage );
            tvDate = (TextView) mView.findViewById( R.id.notifDate );
        }
    }
}