package com.example.donatetosave;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OrganizationFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference memref;
    private MemberAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_organization, container, false);
        db=FirebaseFirestore.getInstance();
        memref=db.collection("Organization").document("NVrHnlDX2k0RAj83fPqe").collection("Member");
        Query query =memref.orderBy("role", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Member> options= new FirestoreRecyclerOptions.Builder<Member>().setQuery(query,Member.class).build();
        adapter = new MemberAdapter(options);
        RecyclerView recyclerView = fragment.findViewById(R.id.member_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setItemClickListener(new MemberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Member member = documentSnapshot.toObject(Member.class);
                String id = documentSnapshot.getId();
                String path =documentSnapshot.getReference().getPath();
                String uid = member.getUid();
                Toast.makeText(getActivity(), id+path+uid, Toast.LENGTH_SHORT).show();
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
