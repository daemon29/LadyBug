package com.example.donatetosave;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AchievementAdapter extends FirestoreRecyclerAdapter<Achievement,AchievementAdapter.NoteHolder> {
    public AchievementAdapter(@NonNull FirestoreRecyclerOptions<Achievement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Achievement model) {
        holder.AchievementName.setText(model.getName());
        holder.Progress.setText(model.getCount().toString()+" / "+model.getMax().toString());
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.achievement_note,viewGroup,false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView AchievementName, Progress;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            AchievementName=itemView.findViewById(R.id.achievement_name);
            Progress=itemView.findViewById(R.id.achievement_progress);

        }
    }
}
