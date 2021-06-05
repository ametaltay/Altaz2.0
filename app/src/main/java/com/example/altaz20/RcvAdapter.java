package com.example.altaz20;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RcvAdapter extends RecyclerView.Adapter<RcvAdapter.RcvViewHolder>{
    private final Context ct;
    private final ArrayList<String> imagesPath;
    private final ArrayList<Bitmap> imagesThumbnails;

    public RcvAdapter(Context ct, ArrayList<String> imagesPath, ArrayList<Bitmap> imagesThumbnails){
        this.ct = ct;
        this.imagesPath = imagesPath;
        this.imagesThumbnails = imagesThumbnails;
    }

    @Override
    public RcvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_single_item,parent,false);
        RcvViewHolder rcvViewHolder = new RcvViewHolder(view);
        return rcvViewHolder;
    }

    @Override
    public void onBindViewHolder(RcvViewHolder holder, int position) {
        File imgPTH = new File(imagesPath.get(position));
        Bitmap imgBTMP = imagesThumbnails.get(position);

        if(imgPTH.exists()){
            holder.imageSprite.setImageBitmap(imgBTMP);
            holder.imageTitle.setText(imgPTH.getName());
            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   shareImage(v, imgPTH);
                }
            });
        }
    }

    public void shareImage(View v, File imageFile){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri shareUri = FileProvider.getUriForFile(v.getContext(), v.getContext().getApplicationContext().getPackageName() + ".provider",
                imageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
        shareIntent.setType("image/*");

        Intent chooser = Intent.createChooser(shareIntent,"ShareImage");
        List<ResolveInfo> resolveInfoList = v.getContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo resolveInfo : resolveInfoList){
            String packageName = resolveInfo.activityInfo.packageName;
            v.getContext().grantUriPermission(packageName, shareUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        v.getContext().startActivity(chooser);
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    public static class RcvViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageSprite;
        private final TextView imageTitle;
        private final ImageButton shareButton;

        public RcvViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSprite = itemView.findViewById(R.id.image_sprite);
            imageTitle = itemView.findViewById(R.id.image_title);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);
        }
    }
}
