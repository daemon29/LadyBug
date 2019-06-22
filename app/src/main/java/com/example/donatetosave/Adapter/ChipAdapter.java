package com.example.donatetosave.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donatetosave.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.Holder> {
    Context context;
    ArrayList<String> label;
    LayoutInflater layoutInflater;
    public ChipAdapter(Context context, ArrayList<String> label){
        this.context = context;
        this.label = label;
        layoutInflater = LayoutInflater.from(context);
    }
    ArrayList<String> getLabel(){
        return  label;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.chip_view,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final Holder temp = holder;
        holder.label.setText(label.get(i));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(holder.getAdapterPosition());
            }
        });
    }
    public void delete(int position) { //removes the row
        getLabel().remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public int getItemCount() {
        return label.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView remove;
        public Holder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.chip_content);
            remove = itemView.findViewById(R.id.removeBTN);
        }
    }
}
