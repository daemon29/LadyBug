package com.example.donatetosave.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donatetosave.Class.Achievement;
import com.example.donatetosave.Class.Notification;
import com.example.donatetosave.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationAdapter extends  FirestoreRecyclerAdapter<Notification,NotificationAdapter.NoteHolder> {
    private Context mContext;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull Notification model) {
        String key = model.getKey();
        FirebaseFirestore.getInstance().collection("Item").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 String name = documentSnapshot.get("uid").toString();
                 String tag = documentSnapshot.get("tag").toString();
                 String url = documentSnapshot.get("url").toString();
                 String content = documentSnapshot.get("description").toString();
                 holder.Content.setText(content);
                 holder.Name.setText("From :"+name);
                 holder.Tag.setText(tag);
                 Glide.with(mContext).load(url).into(holder.imageView);
            }
        });
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_note, viewGroup, false);
        this.mContext = viewGroup.getContext();

        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView Name, Tag, Content;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notification_image);
            Name = itemView.findViewById(R.id.notification_name);
            Tag = itemView.findViewById(R.id.notification_tag);
            Content = itemView.findViewById(R.id.notification_content);


        }
    }
}
