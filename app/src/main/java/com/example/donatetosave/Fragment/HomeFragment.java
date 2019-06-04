package com.example.donatetosave.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donatetosave.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements HomeFragmentDialog.HomeFragmentDialogListener, View.OnClickListener {
    private ImageView BackGround, Image;
    private Button BtnBackground,BtnImage,BtnName,BtnBio,BtnWorkat;
    private TextView Name, Bio,Email,WorkAt;
    private DocumentReference docref;
    private FirebaseFunctions mFunction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Home");


        BtnBackground = fragment.findViewById(R.id.home_edit_background);
        BtnImage = fragment.findViewById(R.id.home_edit_image);
        BtnName = fragment.findViewById(R.id.home_edit_name);
        BtnBio = fragment.findViewById(R.id.home_edit_bio);
        BtnWorkat = fragment.findViewById(R.id.home_edit_workat);

        BtnBackground.setOnClickListener(this);
        BtnImage.setOnClickListener(this);
        BtnName.setOnClickListener(this);
        BtnBio.setOnClickListener(this);
        BtnWorkat.setOnClickListener(this);

        mFunction= FirebaseFunctions.getInstance("asia-northeast1");

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
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.home_edit_background:
                break;
            case R.id.home_edit_image:
                break;
            case R.id.home_edit_name:
                openDialog("Name");
                break;
            case R.id.home_edit_bio:
                openDialog("Bio");
                break;
            case R.id.home_edit_workat:
                openDialog("Work Place");
                break;
            default:
                break;
        }
    }
    public void openDialog(String a){
        HomeFragmentDialog dialog = new HomeFragmentDialog();
        dialog.setTargetFragment(HomeFragment.this,0);
        Bundle args = new Bundle();
        args.putString("type",a);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(),"HomeFragmentDialog");
    }

    @Override
    public void applyText(String result,String type) {
        Map<String,Object> data = new HashMap<>();

        if("Name".equals(type)){
            Name.setText(result);
            data.put("root","name");
            data.put("value",Name.getText().toString().trim());
            data.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            mFunction.getHttpsCallable("setProfileMono").call(data);
        }
        if("Bio".equals(type)){ Bio.setText(Html.fromHtml("Bio: "+"<b>"+result+"</b>"));
            data.put("root","bio");
            data.put("value",Bio.getText().toString().trim());
            data.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            mFunction.getHttpsCallable("setProfileMono").call(data);
        }
        if("Work place".equals(type)){ WorkAt.setText(Html.fromHtml("Work at: "+"<b>"+result+"</b>"));
            data.put("root","work_at");
            data.put("value",WorkAt.getText().toString().trim());
            data.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            mFunction.getHttpsCallable("setProfileMono").call(data);
        }

    }
}
