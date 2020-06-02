package com.leonyr.smartipaddemo.crop;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.databinding.FmMediaBinding;
import com.leonyr.smartipaddemo.movie.AudioFragment;
import com.leonyr.smartipaddemo.movie.MovieFragment;

import org.parceler.Parcel;

public class MediaFragment extends AbBindFragment<MediaViewModel, FmMediaBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_media;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        MediaViewModel.picPath.setValue("");
        MediaViewModel.moviePath.setValue("");
        MediaViewModel.audioPath.setValue("");

        MediaViewModel.picPath.observe(this, pathObserver);
        MediaViewModel.moviePath.observe(this, pathObserver);
        MediaViewModel.audioPath.observe(this, pathObserver);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image:
                openFragment(new CropHomeFragment.CropHomeType());
                break;
            case R.id.btn_video:
                openFragment(new MovieFragment.MovieType());
                break;
            case R.id.btn_auto:
                openFragment(new AudioFragment.AudioType());
                break;
        }
    }


    private Observer<String> pathObserver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            Binding().filePath.setText(getString(R.string.file_path, MediaViewModel.picPath.getValue(), MediaViewModel.moviePath.getValue(), MediaViewModel.audioPath.getValue()));
        }
    };


    @Parcel
    public static class MediaType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new MediaFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return MediaFragment.class.getSimpleName();
        }
    }
}
