package com.example.donatetosave;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseFunctions mFunction;
    FirebaseStorage mStorage;
    StorageReference storageRef;

    private TextView name;
    private TextView detail;
    private ImageView profile_pic;
    private FirebaseUser currentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFunction=FirebaseFunctions.getInstance("asia-northeast1");
        mStorage=FirebaseStorage.getInstance("gs://donatetosave-2fec5");
        currentuser=FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        final String userid = currentuser.getUid();
        Toast.makeText(this, userid, Toast.LENGTH_SHORT).show();
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup,null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable= true;
                final PopupWindow popupWindow= new PopupWindow(popupView,width,height,focusable);
                popupWindow.showAtLocation(findViewById(R.id.drawer_layout), Gravity.CENTER,0,0);

                ImageButton take_photo= popupView.findViewById(R.id.take_photo_btn);
                Button upload_photo= popupView.findViewById(R.id.upload_btn);
                take_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        popupWindow.dismiss();
                        startActivityForResult(intent,0);
                    }
                });
                upload_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        popupWindow.dismiss();
                        startActivityForResult(photoPickerIntent,100);
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

        });



        name = navigationView.getHeaderView(0).findViewById(R.id.name);
        detail = navigationView.getHeaderView(0).findViewById(R.id.detail);
        profile_pic = navigationView.getHeaderView(0).findViewById(R.id.profile_pic);
        SetProfile(userid);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap=null;
        if(requestCode==0){
            imageBitmap=(Bitmap)data.getExtras().get("data");}
        else
        if(requestCode==100){
            Uri selectedImage = data.getData();
            try{imageBitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);}
            catch (IOException e){
                //catch cho vui thoi đéo biết handle sao cả
            }
        }

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup,null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable= true;
        final PopupWindow popupWindow= new PopupWindow(popupView,width,height,focusable);
        popupWindow.showAtLocation(findViewById(R.id.drawer_layout), Gravity.CENTER,0,0);

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

        Button button=popupView.findViewById(R.id.submit);
        final EditText name=popupView.findViewById(R.id.title);
        final Spinner spinner = popupView.findViewById(R.id.spinner_extend);
        Integer[] items = new Integer[]{1,2,3,4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ImageButton take_photo= popupView.findViewById(R.id.take_photo_btn);
        Button upload_photo= popupView.findViewById(R.id.upload_btn);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                popupWindow.dismiss();
                startActivityForResult(intent,0);
            }
        });

        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                popupWindow.dismiss();
                startActivityForResult(photoPickerIntent,100);
            }
        });

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
                        Toast.makeText(MainActivity.this,"Fail upload file",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this,"Success upload file",Toast.LENGTH_LONG).show();
                    }
                });
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        data.put("url",uri.toString());
                        mFunction.getHttpsCallable("submit").call(data);
                    }
                });
                popupWindow.dismiss();
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_organization) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment()).commit();
        } else if (id == R.id.nav_map) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment()).commit();
        } else if (id == R.id.nav_import) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void SetProfile(String userid){
        DocumentReference noteRef = FirebaseFirestore.getInstance().collection("User").document(userid);
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("name"));
                detail.setText(documentSnapshot.getString("organization")+"-"+documentSnapshot.getString("email"));
                Glide.with(getApplicationContext()).load(documentSnapshot.getString("image_url")).into(profile_pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

