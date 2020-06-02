package com.leonyr.smartipaddemo.crop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.StatusBarUtil;
import com.leonyr.lib.utils.StringUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.lib.utils.UriUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.NewCommon;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmCropHomeBinding;
import com.leonyr.smartipaddemo.set.PDFUtils;
import com.leonyr.smartipaddemo.set.SettingFragment;
import com.leonyr.smartipaddemo.sign.SignHomeFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.parceler.Parcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.leonyr.smartipaddemo.crop.CropImageFragment.EXTRA_CROPPED_FILE;

public class CropHomeFragment extends AbBindFragment<LViewModel, FmCropHomeBinding> implements NewCommon.OnBackPressedListener {

    private final int REQUEST_CODE_TAKE_PHOTO = 0x1200; //请求拍照
    private final int REQUEST_CODE_TAKE_PHOTO_CROP = 0x1201;  //请求裁剪
    private final int REQUEST_CODE_TAKE_SIGN = 0x1202; //请求签名
    CropImageFragment cropFragment;

    File photoFile;
    private Disposable disposable;

    List<String> fileList = new ArrayList<>();
    private MultiTypeAdapter typeAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_crop_home;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        StatusBarUtil.hackStatusBarImage(Binding().getRoot());
        Binding().setFm(this);

        Binding().mediaRl.setLayoutManager(new GridLayoutManager(mCtx, 3));
        typeAdapter = new MultiTypeAdapter();
        typeAdapter.register(String.class, new CropFileAdapter());

        Binding().mediaRl.setAdapter(typeAdapter);
        typeAdapter.setItems(fileList);
    }

    /**
     * 点击拍照按钮
     *
     * @param view
     */
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.action_photo:
            case R.id.action_again:
                startTakePhoto();
                break;
            case R.id.action_pdf:
                SignHomeFragment fragment = SignHomeFragment.newInstance();
                fragment.setTargetFragment(this, REQUEST_CODE_TAKE_SIGN);
                fragment.show(getFragmentManager(), SignHomeFragment.class.getSimpleName());

                break;

        }
    }

    /**
     * 调用摄像头并返回存储路径
     */
    private void startTakePhoto() {
        dispose();
        disposable = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
//                            SettingFragment.BrightControl(true);
                            photoFile = new File(getContext().getExternalFilesDir("cache"), FileName.getPhotoJpgName());
                            /*Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri uri = UriUtil.getUri(mCtx, photoFile);
                            startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startCameraIntent.putExtra("autofocus", true); // 自动对焦
                            startCameraIntent.putExtra("showActionIcons", false);
                            if (startCameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                            }*/

                            Intent startCameraIntent = new Intent(mCtx, CameraActivity.class);
                            startCameraIntent.putExtra(CameraActivity.OUTPUT, photoFile.getPath());
                            startCameraIntent.putExtra(CameraActivity.CAMERA_TYPE, CameraActivity.TYPE_PHOTO);
                            if (startCameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                            }
                        } else {
                            ToastUtil.showToast(getContext(), "权限不足");
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
//            SettingFragment.BrightControl(false);
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                String path = data.getStringExtra(CameraActivity.OUTPUT);
                LogUtil.e(path);
                if (cropFragment == null) {
                    cropFragment = CropImageFragment.newInstance(path);
                    cropFragment.setTargetFragment(this, REQUEST_CODE_TAKE_PHOTO_CROP);
                }
                CropImageFragment.cropFile = path;
                openFragment(cropFragment, CropImageFragment.class.getSimpleName());
                break;
            case REQUEST_CODE_TAKE_PHOTO_CROP:
                String cropPath = data.getStringExtra(EXTRA_CROPPED_FILE);
                fileList.add(cropPath);
                typeAdapter.notifyDataSetChanged();

                Binding().actionPdf.setEnabled(fileList.size() > 0);

                /*photoFile = new File(cropPath);
                Glide.with(mCtx).load(photoFile).into(Binding().actionPhotoThumb);
                refreshScanFile(mCtx, cropPath);*/
                break;
            case REQUEST_CODE_TAKE_SIGN:
                ToastUtil.showToast(mCtx, "签名成功，开始合成");
                try {
                    String signPath = data.getStringExtra(SignHomeFragment.RESULT_PATH_SIGN);
                    if (StringUtil.isNotBlank(signPath)) {

                        if (fileList.size() <= 0) {
                            return;
                        }
                        String lastPicPath = fileList.get(fileList.size() - 1);
                        Bitmap src = BitmapFactory.decodeStream(new FileInputStream(lastPicPath));
                        Bitmap mark = BitmapFactory.decodeStream(new FileInputStream(signPath));
                        Bitmap result = PDFUtils.mergeBitmap(src, mark);
                        File rf = new File(mCtx.getExternalFilesDir("cache"), FileName.getPhotoJpgName());
                        if (rf.exists()) {
                            rf.delete();
                        }
                        FileOutputStream outputStream = new FileOutputStream(rf);
                        result.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        fileList.remove(fileList.size() - 1);
                        fileList.add(rf.getPath());
                        src.recycle();
                        mark.recycle();
                        src = null;
                        mark = null;
                        result.recycle();
                        result = null;
                        ToastUtil.showToast(mCtx, "合成完，开始存储");

                        File temp = new File(lastPicPath);
                        if (temp.exists()) {
                            temp.delete();
                        }
                        temp = new File(signPath);
                        if (temp.exists()) {
                            temp.delete();
                        }

                        File pdfFile = new File(getContext().getExternalCacheDir(), FileName.getPdfName());
                        if (fileList.size() <= 0) {
                            ToastUtil.showToast(mCtx, "暂无图片");
                            return;
                        }
                        PDFUtils.createPdf(pdfFile.getPath(), fileList);
                        MediaViewModel.picPath.setValue(pdfFile.getPath());

                        ToastUtil.showToast(mCtx, "合成Success");
//                        ToastUtil.showToast(mCtx, "合成Success\n" + pdfFile.getPath());
                        FileName.createOverFile(mCtx);
                        fileList.clear();
                        typeAdapter.notifyDataSetChanged();
                        Binding().actionPhoto.setText("拍照");
                        Binding().actionPdf.setEnabled(false);
                        getActivity().finish();


                    /*try {
                        char[] PASSWORD = "123456".toCharArray();
                        //将证书文件放入指定路径，并读取keystore ，获得私钥和证书链
                        String pkPath = mCtx.getExternalFilesDir("file") + File.separator + "pdf.p12";
                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        ks.load(new FileInputStream(pkPath), PASSWORD);
                        String alias = ks.aliases().nextElement();
                        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
                        Certificate[] chain = ks.getCertificateChain(alias);

                        //封装签章信息
                        SignatureInfo info = new SignatureInfo();
                        info.setReason("理由");
                        info.setLocation("位置");
                        info.setPk(pk);
                        info.setChain(chain);
                        info.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
                        info.setDigestAlgorithm(DigestAlgorithms.SHA1);
                        info.setFieldName("client1");
                        info.setImagePath(signPath);
                        info.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

                        PDFUtils.sign(pdfFile.getPath(), FileName.getPdfName(), info);
                    }catch (Exception e){
                        e.printStackTrace();
                    }*/
                    }
                } catch (Exception e) {
                    ToastUtil.showToast(mCtx, e.getMessage());
                }
                break;
        }

        Binding().actionPhoto.setText(fileList.size() > 0 ? "下一张" : "拍照");
    }

    private void refreshScanFile(Context c, String mPhotoFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse(mPhotoFile); //out is your output file
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.e("==============");
        if (null != fileList && fileList.size() > 0) {
            for (String f : fileList) {
                File file = new File(f);
                FileUtil.copyFile(f, new File(mCtx.getExternalCacheDir(), file.getName()).getPath());
                file.delete();
            }
            FileName.createOverFile(mCtx);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        dispose();
        super.onDestroy();
    }

    @Parcel
    public static class CropHomeType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new CropHomeFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return CropHomeFragment.class.getSimpleName();
        }
    }
}
