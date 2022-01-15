package com.medical.dialysiscenter.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.RequestModel;

import java.util.HashMap;
import java.util.Map;

public class AddTech extends AppCompatActivity {

    private RecyclerView listTechRequests;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_tech );
        listTechRequests = (RecyclerView) findViewById( R.id.techRequests );
        listTechRequests.setLayoutManager( new LinearLayoutManager( this ) );
        mRef = FirebaseDatabase.getInstance().getReference().child( "TechRequests" );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<RequestModel, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<RequestModel, UsersViewHolder>(
                RequestModel.class, R.layout.item_requests, UsersViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child( "TechRequests" )
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, RequestModel model, int i) {
                if (model.getRequest().equals( "recieved" )) {
                    holder.tvName.setText( model.getName() + "" );
                    holder.btnApprove.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Map map = new HashMap();
                            map.put( "request", "approved" );
                            mRef.child( getRef( i ).getKey().toString() ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText( AddTech.this, "Technician added", Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            } );
                        }
                    } );
                } else {
                    holder.mView.setVisibility( View.GONE );
                }
            }
        };
        listTechRequests.setAdapter( adapter );
        if (adapter.getItemCount() >= 1) {
            findViewById( R.id.tvNoRequest ).setVisibility( View.GONE );
        }
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvName;
        Button btnApprove;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvName = (TextView) mView.findViewById( R.id.tvApplicantName );
            btnApprove = (Button) mView.findViewById( R.id.btnApprove );

            // userImage=(CircleImageView) mView.findViewById(R.id.single_userImage_users);


        }


    }
}