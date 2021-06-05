package com.example.altaz20;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerFragment extends Fragment {

    RecyclerView rcV;
    RcvAdapter rcVAdapter;
    RecyclerView.LayoutManager rcVLayoutMng;

    private ArrayList<String> allImagesPaths;
    private ArrayList<Bitmap> allImagesThumbnails;

    private ArrayList<String> imagesPath;
    private ArrayList<Bitmap> imagesThumbnails;



    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        EditText searchBox = view.findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        imagesPath = new ArrayList<>();
        imagesThumbnails = new ArrayList<>();
        allImagesPaths = new ArrayList<>();
        allImagesThumbnails = new ArrayList<>();

        rcV = view.findViewById(R.id.recycler_view);
        rcVAdapter = new RcvAdapter(getContext(), imagesPath, imagesThumbnails);

        rcVLayoutMng = new LinearLayoutManager(getContext());

        rcV.setHasFixedSize(true);

        rcV.setLayoutManager(rcVLayoutMng);
        rcV.setAdapter(rcVAdapter);

        getImages();
        allImagesPaths.addAll(imagesPath);
        allImagesThumbnails.addAll(imagesThumbnails);

        rcVAdapter.notifyDataSetChanged();

        return view;
    }

    private void search(String searchText){
        ArrayList<String> targetText = new ArrayList<>();
        ArrayList<Bitmap> targetBitmap = new ArrayList<>();

        int index = 0;

        for(String path : allImagesPaths){
            if(path.toLowerCase().contains(searchText.toLowerCase())){
                targetText.add(path);
                Toast.makeText(getContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
                targetBitmap.add(allImagesThumbnails.get(index));
            }
            index++;
        }

        imagesPath.clear();
        imagesThumbnails.clear();

        imagesPath.addAll(targetText);
        imagesThumbnails.addAll(targetBitmap);

        rcVAdapter.notifyDataSetChanged();
    }

    private void getImages(){
        boolean SDCard = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(SDCard){
            imagesPath.clear();
            imagesThumbnails.clear();

            final String[] imageData = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

            final String order = MediaStore.Images.Media._ID;
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageData, null, null, order);

            while(cursor.moveToNext()) {
                int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String absoluteImagePath = cursor.getString(dataIndex);
                imagesPath.add(absoluteImagePath);
                imagesThumbnails.add(reducedBitmap(absoluteImagePath, 100, 100));
            }
            cursor.close();
        }
    }

    public static int reduceSampleSize(BitmapFactory.Options options, int width, int height){
        final int h = options.outHeight;
        final int w = options.outWidth;
        int sampleSize = 1;

        if(h > height || w > width){
            final int hh = h / 2;
            final int hw = w / 2;

            while((hh / sampleSize) >= height && (hw / sampleSize) >= width){
                sampleSize *= 2;
            }
        }

        return  sampleSize;
    }

    public static Bitmap reducedBitmap(String absoluteImagePath, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absoluteImagePath, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = reduceSampleSize(options, width, height);
        Bitmap imageBitmap = BitmapFactory.decodeFile(absoluteImagePath, options);
        return imageBitmap;
    }
}
