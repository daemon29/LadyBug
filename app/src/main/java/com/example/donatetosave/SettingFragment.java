package com.example.donatetosave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class SettingFragment extends Fragment {
    private CircleImageView imageView;
    private EditText fullName,bio,address,contact;
    private Button update;
    private ProgressBar progressBar;
    private StorageReference storageRef;
    private FirebaseStorage mStorage;
    private FirebaseFunctions mFunction;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_setting, container, false);
        imageView=fragment.findViewById(R.id.setting_image);
        fullName=fragment.findViewById(R.id.setting_name);
        bio=fragment.findViewById(R.id.setting_detail);
        address=fragment.findViewById(R.id.setting_address);
        contact=fragment.findViewById(R.id.setting_contact);
        progressBar=fragment.findViewById(R.id.import_progress);

        mFunction = FirebaseFunctions.getInstance("asia-northeast1");
        mStorage = FirebaseStorage.getInstance("gs://donatetosave-2fec5");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });
        return fragment;
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
            }
            catch (IOException e){
                //catch cho vui thoi đéo biết handle sao cả
            }
            imageView.setImageBitmap(imageBitmap);
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] file = baos.toByteArray();
            final String filename = System.currentTimeMillis()+"image.jpg";

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    storageRef= mStorage.getReference(filename);
                    UploadTask uploadTask = storageRef.putBytes(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(GONE);
                            Toast.makeText(getActivity(),"Fail upload file",Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String,Object> data = new HashMap<>();
                                    data.put("name",fullName.getText().toString());
                                    data.put("bio",bio.getText().toString());
                                    data.put("address",address.getText().toString());
                                    data.put("url",uri.toString());
                                    data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    mFunction.getHttpsCallable("submit").call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                            Toast.makeText(getActivity(),"Successfully upload your item",Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(GONE);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
    }
    }
}
