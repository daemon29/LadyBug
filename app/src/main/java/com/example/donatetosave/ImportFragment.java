package com.example.donatetosave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class ImportFragment extends Fragment {
    private Button Upload,Photo,Submit;
    private ImageView Image;
    private EditText Description,Address,Contact;
    private Spinner Expire,Tag;
    private StorageReference storageRef;
    private FirebaseStorage mStorage;
    private FirebaseFunctions mFunction;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_import, container, false);
        Upload=fragment.findViewById(R.id.import_upload);
        Photo=fragment.findViewById(R.id.import_photo);
        Submit=fragment.findViewById(R.id.import_submit);
        Image=fragment.findViewById(R.id.import_image);
        Description=fragment.findViewById(R.id.import_detail);
        Address=fragment.findViewById(R.id.import_address);
        Contact=fragment.findViewById(R.id.import_contact);
        Expire=fragment.findViewById(R.id.import_expire);
        Tag=fragment.findViewById(R.id.import_tag);
        progressBar=fragment.findViewById(R.id.import_progress);

        mFunction= FirebaseFunctions.getInstance("asia-northeast1");
        mStorage=FirebaseStorage.getInstance("gs://donatetosave-2fec5");

        ArrayAdapter<CharSequence> adapter_expire = ArrayAdapter.createFromResource(this.getActivity(),R.array.expire, android.R.layout.simple_spinner_item);
        adapter_expire.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Expire.setAdapter(adapter_expire);

        ArrayAdapter<CharSequence> adapter_tag = ArrayAdapter.createFromResource(this.getActivity(),R.array.tag, android.R.layout.simple_spinner_item);
        adapter_tag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Tag.setAdapter(adapter_tag);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
            }
        });
        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((BitmapDrawable)Image.getDrawable()).getBitmap()==null){
                    Toast.makeText(getActivity(), "File must contain image", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap imageBitmap=((BitmapDrawable)Image.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] file = baos.toByteArray();
                final String filename = System.currentTimeMillis()+"image.jpg";

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
                                submit(Address.getText().toString(),Contact.getText().toString(),Description.getText().toString(),Expire.getSelectedItem().toString(),Tag.getSelectedItem().toString(),uri.toString(),FirebaseAuth.getInstance().getCurrentUser().getUid())
                                         .addOnCompleteListener(new OnCompleteListener<String>() {
                                     @Override
                                     public void onComplete(@NonNull Task<String> task) {
                                         if(!task.isSuccessful()){
                                             Exception e = task.getException();
                                             if(e instanceof FirebaseException){
                                                 FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                 FirebaseFunctionsException.Code code = ffe.getCode();
                                                 Object details = ffe.getDetails();
                                             }
                                         }
                                     }
                                 });
                            }
                        });
                    }
                });
            }
        });
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap=null;
        if (data==null) return;
        if(requestCode==1){
            imageBitmap=(Bitmap)data.getExtras().get("data");
        }
        else
        if(requestCode==0){
            try{
                Uri selectedImage = data.getData();
                imageBitmap=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            }
            catch (IOException e){
                //catch cho vui thoi đéo biết handle sao cả
            }
        }
        Image.setImageBitmap(imageBitmap);
        Image.setDrawingCacheEnabled(true);
        Image.buildDrawingCache();
    }
    private Task<String> submit(String address,String contact,String description,String expire,String tag,String url,String uid){
        Map<String,Object> data = new HashMap<>();
        data.put("address",address);
        data.put("contact",contact);
        data.put("description",description);
        data.put("expire",expire);
        data.put("tag",tag);
        data.put("url",url);
        data.put("uid",uid);
        return mFunction.getHttpsCallable("submit").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                Log.d("TAG",result);
                return result;
            }
        });
    }
}
