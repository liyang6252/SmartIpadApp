package com.leonyr.smartipaddemo.crop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.leonyr.lib.utils.BitmapUtil;
import com.leonyr.lib.utils.FileUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.util.RxSchedulers;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmCropImageBinding;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class CropImageFragment extends AbBindFragment<LViewModel, FmCropImageBinding> {

    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
    public static final String EXTRA_CROPPED_FILE = "extra_cropped_file";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_ALBUM = 200;

    public static String cropFile;

    private static final String EXTRA_CROP_FILE_PATH = "extra_crop_file_path";
    Disposable tempDisposable;
    ProgressDialog mProgressDialog;

    public static CropImageFragment newInstance(String cropFilePath) {

        Bundle args = new Bundle();
//        args.putString(EXTRA_CROP_FILE_PATH, cropFilePath);

        CropImageFragment fragment = new CropImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_crop_image;
    }

    /*public static Intent getJumpIntent(Context context, boolean fromAlbum, File croppedFile) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_FROM_ALBUM, fromAlbum);
        intent.putExtra(EXTRA_CROPPED_FILE, croppedFile);
        return intent;
    }*/

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        /*Bundle extras = getArguments();
        if (null != extras) {
            cropFile = extras.getString(EXTRA_CROP_FILE_PATH);
        }*/
        if (null == mProgressDialog){
            mProgressDialog = new ProgressDialog(mCtx);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(cropFile)) {
            getFragmentManager().popBackStack();
        }
        Binding().setCropImageFragment(this);

        mProgressDialog.show();
        Observable
                .fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(cropFile, options);
                        options.inJustDecodeBounds = false;

//            options.inSampleSize = calculateSampleSize(options);
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(cropFile, options);
                        FileUtil.deleteFile(cropFile);
                        return selectedBitmap;
                    }
                })
//                .map(new Function<Bitmap, Bitmap>() {
//                    @Override
//                    public Bitmap apply(Bitmap bitmap) throws Exception {
//                        return BitmapUtil.rotateBitmap(bitmap, 180);
//                    }
//                })
                .compose(RxSchedulers.IOMain())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        tempDisposable = d;
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Binding().ivCrop.setImageToCrop(bitmap);
                        Binding().btnOk.setEnabled(true);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getActivity().onBackPressed();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_ok:
                File mCroppedFile = new File(getContext().getExternalFilesDir("cache"), FileName.getPhotoJpgName());
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CROPPED_FILE, mCroppedFile.getPath());
                if (Binding().ivCrop.canRightCrop()) {
                    Bitmap crop = Binding().ivCrop.crop();
                    if (crop != null) {
                        saveImage(crop, mCroppedFile);
//                        FileUtil.saveImage(crop, mCroppedFile);
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        setResult(Activity.RESULT_CANCELED, intent);
                    }
//                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "cannot crop correctly", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void saveImage(Bitmap bitmap, File saveFile) {
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    @Override
    public void onDestroy() {
        if (null != tempDisposable && !tempDisposable.isDisposed()){
            tempDisposable.dispose();
        }
        super.onDestroy();
    }

    @Parcel
    public static class CropImageType implements Common.Type {

        String savePath;

        @ParcelConstructor
        public CropImageType(String savePath) {
            this.savePath = savePath;
        }

        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return newInstance(savePath);
        }

        @NonNull
        @Override
        public String getTag() {
            return CropImageFragment.class.getSimpleName();
        }
    }

}
