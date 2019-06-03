package com.example.donatetosave.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donatetosave.Class.Chat;
import com.example.donatetosave.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.NoteHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    public MessageAdapter(Context mContext,List<Chat> mChat,String imageurl){
        this.mChat=mChat;
        this.mContext=mContext;
        this.imageurl=imageurl;
    }



    @NonNull
    @Override
    public MessageAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.NoteHolder(v);
        }
        else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.NoteHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.NoteHolder noteHolder, int i) {
        Chat chat=mChat.get(i);

        noteHolder.show_message.setText(chat.getMessage());

        Glide.with(mContext).load(imageurl).into(noteHolder.Image);
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView show_message;
        private ImageView Image;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            Image=itemView.findViewById(R.id.profile_image);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else return MSG_TYPE_LEFT;
    }
}
