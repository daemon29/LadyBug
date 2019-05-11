package com.example.donatetosave;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFunctions mFunction;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    ImageButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFunction=FirebaseFunctions.getInstance();
        mStorage=FirebaseStorage.getInstance();
        button=findViewById(R.id.btn_camera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Bitmap imageBitmap=(Bitmap)data.getExtras().get("data");
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup,null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable= true;
        final PopupWindow popupWindow= new PopupWindow(popupView,width,height,focusable);
        popupWindow.showAtLocation(findViewById(R.id.linearlayout0), Gravity.CENTER,0,0);
        ImageView popupImage = popupView.findViewById(R.id.image_id);

        popupImage.setImageBitmap(imageBitmap);
        popupImage.setDrawingCacheEnabled(true);
        popupImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) popupImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] file = baos.toByteArray();
        final String filename = System.currentTimeMillis()+"image.jpg";
        storageRef= mStorage.getReference(filename);

        Button button= popupView.findViewById(R.id.submit);
        final EditText name=popupView.findViewById(R.id.title);
        final Spinner spinner = popupView.findViewById(R.id.spinner_extend);
        Integer[] items = new Integer[]{1,2,3,4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=name.getText().toString();
                Integer date_expire= Integer.parseInt(spinner.getSelectedItem().toString());
                final Map<String,Object> data = new HashMap<>();
                data.put("title",content);
                data.put("expire",date_expire);
                final UploadTask uploadTask = storageRef.putBytes(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Fail upload file",Toast.LENGTH_LONG);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this,"Success upload file",Toast.LENGTH_LONG);
                    }
                });
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        data.put("url",uri.toString());
                        mFunction.getHttpsCallable("submit").call(data);
                    }
                });
                //Toast.makeText(MainActivity.this, content+ "+"+date_expire, Toast.LENGTH_SHORT).show();
            }
        });
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
    }
    private Task<String> Submit(Map data) {
        return mFunction
                .getHttpsCallable("submit")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}

