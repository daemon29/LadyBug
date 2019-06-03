package com.example.donatetosave.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donatetosave.Class.Member;
import com.example.donatetosave.Adapter.MemberAdapter;
import com.example.donatetosave.OtherUser;
import com.example.donatetosave.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OrganizationFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference memref;
    private MemberAdapter adapter;
    private String work_id;
    private ImageView Image;
    private TextView Name,Bio,Place;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_organization, container, false);
        Image=fragment.findViewById(R.id.organization_image);
        Name = fragment.findViewById(R.id.organization_name);
        Bio = fragment.findViewById(R.id.organization_bio);
        Place = fragment.findViewById(R.id.organization_place);

        work_id=getArguments().getString("work_id");
        db=FirebaseFirestore.getInstance();
        memref=db.collection("Organization").document(work_id).collection("Member");
        Query query =memref.orderBy("role", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Member> options= new FirestoreRecyclerOptions.Builder<Member>().setQuery(query,Member.class).build();
        adapter = new MemberAdapter(options);

        RecyclerView recyclerView = fragment.findViewById(R.id.member_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        DocumentReference noteRef = FirebaseFirestore.getInstance().collection("Organization").document(work_id);
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(fragment.getContext()).load(documentSnapshot.getString("image_url")).into(Image);
                Name.setText(documentSnapshot.getString("name"));
                Bio.setText(Html.fromHtml("Bio: "+"<b>"+documentSnapshot.getString("summary")+"</b>"));
                Place.setText(Html.fromHtml("Work at: "+"<b>"+documentSnapshot.getString("place")+"</b>"));
            }
        });

        adapter.setItemClickListener(new MemberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Member member = documentSnapshot.toObject(Member.class);
                Intent i = new Intent(getContext(), OtherUser.class);
                String uid = member.getUid();
                Bundle bundle= new Bundle();
                bundle.putString("uid",uid);
                i.putExtras(bundle);
                startActivity(i);
                }
        });
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
