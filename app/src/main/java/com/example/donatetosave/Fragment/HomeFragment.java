package com.example.donatetosave.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.donatetosave.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class HomeFragment extends Fragment implements HomeFragmentDialog.HomeFragmentDialogListener, View.OnClickListener {
    private ImageView BackGround, Image;
    private Button BtnBackground,BtnImage,BtnName,BtnBio,BtnWorkat;
    private TextView Name, Bio,Email,WorkAt;
    private DocumentReference docref;
    private FirebaseStorage mStorage;
    private StorageReference storageRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Home");

        mStorage=FirebaseStorage.getInstance("gs://donatetosave-2fec5");
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
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),3);
                break;
            case R.id.home_edit_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),4);
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
            FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                    "name", result
            );
        }
        if("Bio".equals(type)){ Bio.setText(Html.fromHtml("Bio: "+"<b>"+result+"</b>"));
            FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                    "bio", result
            );
        }
        if("Work place".equals(type)){ WorkAt.setText(Html.fromHtml("Work at: "+"<b>"+result+"</b>"));
            FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                    "work_place", result
            );
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap=null;
        if (data==null) return;
        if(requestCode==3){
            try{
                Uri selectedImage = data.getData();
                imageBitmap=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                BackGround.setImageBitmap(imageBitmap);
                BackGround.setDrawingCacheEnabled(true);
                BackGround.buildDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] file = baos.toByteArray();
                storageRef = mStorage.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"_background.jpg");
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UploadTask uploadTask = storageRef.putBytes(file);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                                                "back_ground", uri.toString()
                                        );
                                        Log.d("Here","back_ground");
                                    }
                                });
                            }
                        });
                    }
                });

            }
            catch (IOException e){
                //catch cho vui thoi đéo biết handle sao cả
            }
        }
        else
        if(requestCode==4){
            try{
                Uri selectedImage = data.getData();
                imageBitmap=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                Image.setImageBitmap(imageBitmap);
                Image.setDrawingCacheEnabled(true);
                Image.buildDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] file = baos.toByteArray();
                storageRef = mStorage.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UploadTask uploadTask = storageRef.putBytes(file);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                                                "image_url", uri.toString()
                                        );
                                        Log.d("Here",uri.toString());

                                    }
                                });
                            }
                        });
                    }
                });
            }
            catch (IOException e){
                //catch cho vui thoi đéo biết handle sao cả
            }
        }

    }
}
