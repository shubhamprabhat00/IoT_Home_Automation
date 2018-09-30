package com.example.shubhamprabhat.iothomeatomation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    public static final int chooseimage = 1;
    Button select, upload;
    ImageView imageView;
    Uri filepath;
    ProgressDialog progressDialog;
    private StorageReference SR;

    //public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressDialog = new ProgressDialog(UploadActivity.this);
        select = (Button) findViewById(R.id.button2);
        upload = (Button) findViewById(R.id.button3);
        imageView = (ImageView) findViewById(R.id.img2);
        SR = FirebaseStorage.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //pb=(ProgressBar)findViewById(R.id.progressBar);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == chooseimage && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadimage() {

        if (filepath != null) {
            //Toast.makeText(UploadActivity.this,"Image Uploading",Toast.LENGTH_SHORT).show();
            progressDialog.setMessage("Uploading");
            progressDialog.show();


            //Uri filepath = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            StorageReference riversRef = SR.child("images/" + filepath.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(filepath);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...

                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {              //////Progress Bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "% Uploaded");
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(UploadActivity.this, "No Image Upload", Toast.LENGTH_SHORT).show();

        }


    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), chooseimage);
    }
}
