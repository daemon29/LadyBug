package com.example.donatetosave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.donatetosave.Adapter.FriendAdapter;
import com.example.donatetosave.Adapter.MemberAdapter;
import com.example.donatetosave.Class.Chat;
import com.example.donatetosave.Class.Friends;
import com.example.donatetosave.Class.Member;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FriendActivity extends AppCompatActivity {
    private CollectionReference friendref;
    private FriendAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your List Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friendref=FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Friend");
        Query query =friendref.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Friends> options= new FirestoreRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        adapter = new FriendAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.friend_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Friends friends = documentSnapshot.toObject(Friends.class);
                Intent i = new Intent(FriendActivity.this, MessageActivity.class);
                Bundle bundle= new Bundle();
                bundle.putString("name",friends.getName());
                bundle.putString("image",friends.getImage_url());
                bundle.putString("uid",friends.getUid());
                i.putExtras(bundle);
                startActivity(i);
            }
        });
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
