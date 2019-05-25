package com.example.donatetosave;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {
    private ImageView BackGround, Image;
    private TextView Name, Bio,Email,WorkAt;
    private DocumentReference docref;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        BackGround=fragment.findViewById(R.id.home_background);
        Image=fragment.findViewById(R.id.home_image);
        Name=fragment.findViewById(R.id.home_name);
        Bio=fragment.findViewById(R.id.home_bio);
        Email=fragment.findViewById(R.id.home_email);
        WorkAt=fragment.findViewById(R.id.home_workat);
        docref= FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(fragment.getContext()).load(documentSnapshot.getString("image_url")).into(Image);
                Glide.with(fragment.getContext()).load(documentSnapshot.getString("back_ground")).into(BackGround);
                Name.setText(documentSnapshot.getString("name"));
                Bio.setText(Html.fromHtml("Bio: "+"<b>"+documentSnapshot.getString("bio")+"</b>"));
                Email.setText(Html.fromHtml("Email: "+"<b>"+documentSnapshot.getString("email")+"</b>"));
                WorkAt.setText(Html.fromHtml("Work at: "+"<b>"+documentSnapshot.getString("work_at")+"</b>"));
            }
        });

        return fragment;

    }
}
