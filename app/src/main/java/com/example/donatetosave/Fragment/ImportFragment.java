package com.example.donatetosave.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.donatetosave.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class ImportFragment extends Fragment {
    private ImageButton Upload,Photo;
    private Button Submit;
    private ImageView Image;
    private EditText Description,Address,Contact,Title;
    private Spinner Expire,Tag;
    private StorageReference storageRef;
    private FirebaseStorage mStorage;
    private ProgressBar progressBar;
    GeoPoint location;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_import, container, false);
        getActivity().setTitle("Import");

        location = new GeoPoint(10.7629183,106.679983);
        Title=fragment.findViewById(R.id.import_title);
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
                else{
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
                        Log.d("Here","upload fail");
                        Toast.makeText(getContext(),"Fail upload file",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("Here","upload oke");
                                submit(Address.getText().toString(),Contact.getText().toString(),Description.getText().toString(),Expire.getSelectedItem().toString(),Tag.getSelectedItem().toString(),uri.toString(),location,Title.getText().toString());
                            }
                        });
                    }
                });}
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
    private void submit(String address, String contact, String description, String expire,final String tag, String url, final GeoPoint location, String title){
        Map<String,Object> data = new HashMap<>();
        data.put("location",location);
        data.put("title",title);
        data.put("address",address);
        data.put("contact",contact);
        data.put("description",description);
        data.put("expire",Integer.parseInt(expire));
        data.put("tag",tag);
        data.put("url",url);
        data.put("time", Calendar.getInstance().getTime());
        FirebaseFirestore.getInstance().collection("Item").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Submit item successfully", Toast.LENGTH_SHORT).show();
                Map<String,Object> data_item = new HashMap<>();
                data_item.put("geo",location);
                data_item.put("key",documentReference.getId());
                data_item.put("tag",tag);
                FirebaseDatabase.getInstance().getReference().child("Item").setValue(data_item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(GONE);
                        Toast.makeText(getContext(), "Submit item successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(GONE);
                Toast.makeText(getContext(), "Something wrong happen, and it's not our fault :/ "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
