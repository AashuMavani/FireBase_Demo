package com.example.firebase_demo.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase_demo.Product_Data;
import com.example.firebase_demo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class Add_Product_Fragment extends Fragment {

    AppCompatEditText pname,pprice,pdes;
    ImageView pimg;
    Button Addbutton,Updatebutton;
    Uri resultUri;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    String id,name,price,des,imageName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add__product_, container, false);

        pname=view.findViewById(R.id.pname);
        pprice=view.findViewById(R.id.pprice);
        pdes=view.findViewById(R.id.pdes);
        pimg=view.findViewById(R.id.pimg);
        Addbutton=view.findViewById(R.id.Addbutton);
        Updatebutton=view.findViewById(R.id.Updatebutton);
        if (getArguments()==null)
        {
            Addbutton.setVisibility(View.VISIBLE);
        }
        if (getArguments()!=null) {
            Updatebutton.setVisibility(View.VISIBLE);
            //id= preferences.getString("userid",null);
            id = getArguments().getString("id");
            name = getArguments().getString("name");
            price = getArguments().getString("price");
            des = getArguments().getString("des");
            imageName = getArguments().getString("img");


            Log.d("ggg", "onCreateView: id=" + id);
            Log.d("ggg", "onCreateView: name=" + name);
            Log.d("ggg", "onCreateView: price=" + price);
            Log.d("ggg", "onCreateView: des=" + des);
            Log.d("ggg", "onCreateView: image=" + imageName);

            pname.setText("" + name);
            pprice.setText("" + price);
            pdes.setText("" + des);
        }


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Product").push();
        String idd = myRef.getKey();


        Log.d("YYY", "onCreateView: Key=" + id);
        storage = FirebaseStorage.getInstance();
        String imgName = "Img_" + new Random().nextInt(10000) + ".jpg";
        StorageReference mainBucket = storage.getReference().child("ProductImages/" + imgName);

     pimg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             CropImage.activity()
                     .start(getContext(),Add_Product_Fragment.this);
         }
     });
     Updatebutton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
             Query applesQuery = ref.child("Product").orderByChild("pId").equalTo(id);
             applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                         //appleSnapshot.getRef().removeValue();
                        // String id=ref.getKey();

                        Product_Data model=new Product_Data(id,pname.getText().toString(),pdes.getText().toString(),pprice.getText().toString(),"url");

                         appleSnapshot.getRef().setValue(model);
                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                     Log.e("YYY", "onCancelled", databaseError.toException());

                 }
             });
         }
     });
     Addbutton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pimg.setDrawingCacheEnabled(true);
             pimg.buildDrawingCache();
             Bitmap bitmap = ((BitmapDrawable) pimg.getDrawable()).getBitmap();
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

                                             Product_Data product_data = new Product_Data(id, pname.getText().toString(), pdes.getText().toString(), pprice.getText().toString(),fileLink);
                                             myRef.setValue(product_data);
                                             Toast.makeText(Add_Product_Fragment.this.getContext(), "Add Product", Toast.LENGTH_SHORT).show();
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


         }
     });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                pimg.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}