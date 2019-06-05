package com.example.donatetosave.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.donatetosave.Class.Achievement;
import com.example.donatetosave.Adapter.AchievementAdapter;
import com.example.donatetosave.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AchievementFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference achievementRef;
    private AchievementAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_achievement, container, false);

        getActivity().setTitle("Your Achievement");

        db=FirebaseFirestore.getInstance();
        achievementRef=db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Achievement");
        Query query = achievementRef.orderBy("count",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Achievement> options = new FirestoreRecyclerOptions.Builder<Achievement>().setQuery(query,Achievement.class).build();
        adapter = new AchievementAdapter(options);

        RecyclerView recyclerView = fragment.findViewById(R.id.achievement_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
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
