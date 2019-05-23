package com.example.donatetosave;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends FirestoreRecyclerAdapter<Member,MemberAdapter.NoteHolder> {

    public MemberAdapter(@NonNull FirestoreRecyclerOptions<Member> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull final Member model) {
        if(model.getRole()==0) {holder.role.setText("Admin");
        holder.layout.setBackgroundColor(Color.parseColor("#96FBFF12"));
        } else if(model.getRole()==1) {holder.role.setText("Member");
            holder.layout.setBackgroundColor(Color.parseColor("#969900FF"));
        } else if(model.getRole()==2) {holder.role.setText("Pending member");
            holder.layout.setBackgroundColor(Color.parseColor("#806666CC"));
        }
        holder.email.setText(model.getEmail());
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_note,viewGroup,false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView name,role,email;
        private RelativeLayout layout;
        private CircleImageView image;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.member_name);
            role = itemView.findViewById(R.id.member_role);
            email = itemView.findViewById(R.id.member_email);
            image=itemView.findViewById(R.id.member_image);
            layout=itemView.findViewById(R.id.member_layout);
        }
    }
}
