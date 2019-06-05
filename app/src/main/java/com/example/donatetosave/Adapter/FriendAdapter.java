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
import com.example.donatetosave.Class.Friends;
import com.example.donatetosave.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;


public class FriendAdapter extends FirestoreRecyclerAdapter<Friends,FriendAdapter.NoteHolder> {
    private Context mContext;
    private OnItemClickListener listener;
    public FriendAdapter(@NonNull FirestoreRecyclerOptions<Friends> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendAdapter.NoteHolder holder, int position, @NonNull Friends model) {
        holder.Name.setText(model.getName());
        Glide.with(mContext).load(model.getImage_url()).into(holder.Image);
    }

    @NonNull
    @Override
    public FriendAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item,viewGroup,false);
        this.mContext = viewGroup.getContext();
        return new FriendAdapter.NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView Name;
        private ImageView Image;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            Name=itemView.findViewById(R.id.friend_name);
            Image=itemView.findViewById(R.id.friend_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= getAdapterPosition();
                    if (pos!= RecyclerView.NO_POSITION&& listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });

        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int pos);
    }
    public void setItemClickListener(FriendAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
}
