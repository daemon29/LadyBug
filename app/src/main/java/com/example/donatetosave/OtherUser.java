package com.example.donatetosave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OtherUser extends AppCompatActivity {
    private ImageView BackGround, Image;
    private TextView Name, Bio,Email,WorkAt;
    private DocumentReference docref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        String uid= bundle.getString("uid");
        Log.d("tag ",uid);
        BackGround=findViewById(R.id.other_background);
        Image=findViewById(R.id.other_image);
        Name=findViewById(R.id.other_name);
        Bio=findViewById(R.id.other_bio);
        Email=findViewById(R.id.other_email);
        WorkAt=findViewById(R.id.other_workat);
        docref= FirebaseFirestore.getInstance().collection("User").document(uid);
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(OtherUser.this).load(documentSnapshot.getString("image_url")).into(Image);
                Glide.with(OtherUser.this).load(documentSnapshot.getString("back_ground")).into(BackGround);
                Name.setText(documentSnapshot.getString("name"));
                Bio.setText(Html.fromHtml("Bio: "+"<b>"+documentSnapshot.getString("bio")+"</b>"));
                Email.setText(Html.fromHtml("Email: "+"<b>"+documentSnapshot.getString("email")+"</b>"));
                WorkAt.setText(Html.fromHtml("Work at: "+"<b>"+documentSnapshot.getString("work_at")+"</b>"));
            }
        });


    }
}
