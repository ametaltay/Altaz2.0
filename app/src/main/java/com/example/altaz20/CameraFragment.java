package com.example.altaz20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 1005;

    Button captureButton;
    ImageView captureView;
    Uri image;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_activity, container, false);
        captureView =  view.findViewById(R.id.camera_imageView);
        captureButton = view.findViewById(R.id.capture_button);

        captureButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(perm, 1000);
            }else{
                openCamera();
            }
        });

        return view;
    }

    private void openCamera(){
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(new Date());
        String imageName = time + "_JPG";

        try{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, imageName);
            image = getView().getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, image);
            startActivityForResult(camIntent, CAMERA_REQUEST_CODE);
        }catch(Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Permisiune refuzata", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            captureView.setImageURI(image);
        }else{
            File f = new File(image.getPath());
            f.delete();
        }
    }
}
