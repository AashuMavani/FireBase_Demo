package com.example.firebase_demo.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase_demo.Product_Data;
import com.example.firebase_demo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class Add_Product_Fragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference imgBucket;

    @SuppressLint("WrongThread")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add__product_, container, false);
        imageView = view.findViewById(R.id.imgView);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Product").push();
        String id = myRef.getKey();
        Log.d("YYY", "onCreateView: Key=" + id);
        storage = FirebaseStorage.getInstance();
        String imgName = "Img_" + new Random().nextInt(10000) + ".jpg";
        StorageReference mainBucket = storage.getReference().child("ProductImages/" + imgName);

        //StorageReference imgBucket = mainBucket.child("ProductImages/" + imgName);


        // While the file names are the same, the references point to different files
        //mainBucket.getName().equals(imgBucket.getName());    // true
        // mainBucket.getPath().equals(imgBucket.getPath());    // false

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mainBucket.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("YYY", "onSuccess: Image Uploaded");

                UploadTask uploadTask = mainBucket.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String fileLink = task.getResult().toString();
                                        Log.d("YYY", "onComplete: url="+fileLink);
                                        //next work with URL

                                        Product_Data product_data = new Product_Data(id, "Mouse", "Pointing Device", "750",fileLink);
                                        myRef.setValue(product_data);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
            }

        });
        return view;
    }
}