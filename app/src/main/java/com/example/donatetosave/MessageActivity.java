package com.example.donatetosave;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.donatetosave.Adapter.MessageAdapter;
import com.example.donatetosave.Class.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private EditText Message;
    private ImageButton Send;
    private FirebaseFirestore db;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    List<Chat> mchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.mess_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        Send = findViewById(R.id.btn_send);
        Message = findViewById(R.id.message);
        db = FirebaseFirestore.getInstance();


        Bundle args=getIntent().getExtras();
        final String userid = args.getString("uid");
        final String image_url = args.getString("image");
        final String name =args.getString("name");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        username.setText(name);
        Glide.with(getApplicationContext()).load(image_url).into(profile_image);
        readMessage(firebaseUser.getUid(),userid,image_url);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = Message.getText().toString();
                if (!msg.equals(""))
                    SendMessage(firebaseUser.getUid(),userid,msg);
                else
                    Toast.makeText(getApplicationContext(),"There is nothing to send !",Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void SendMessage(String sender, String receiver,String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> data = new HashMap<>();
        data.put("sender",sender);
        data.put("receiver",receiver);
        data.put("message",message);
        reference.child("Chats").push().setValue(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Something wring happen :"+e,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void readMessage(final String myid, final String userid, final String imageurl){

        mchat = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(userid)&& chat.getSender().equals(myid))
                        mchat.add(chat);
                    messageAdapter= new MessageAdapter(MessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
