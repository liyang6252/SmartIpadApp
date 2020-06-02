package com.leonyr.smartipaddemo.crop;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.mvvm.vh.AbViewBinder;
import com.leonyr.mvvm.vh.BindViewHolder;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.databinding.ItemCropRlBinding;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/1
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class CropFileAdapter extends AbViewBinder<String, ItemCropRlBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.item_crop_rl;
    }

    @Override
    protected void onBindViewHolder(@NonNull BindViewHolder<ItemCropRlBinding> holder, @NonNull String item) {
        if (item.endsWith(".mp4")) {
            holder.Binding().setShowPlay(true);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(item);
            Bitmap bitmap = media.getFrameAtTime();
            holder.Binding().itemName.setImageBitmap(bitmap);
            holder.Binding().itemPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.playMp4(holder.itemView.getContext(), item);
                }
            });
            holder.Binding().itemName.setOnClickListener(null);
        } else {
            Glide.with(holder.itemView.getContext()).load("file://" + item).into(holder.Binding().itemName);
            holder.Binding().setShowPlay(false);
            holder.Binding().itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.playImage(holder.itemView.getContext(), item);
                }
            });
        }
    }


}
