package com.qa.fgj.baymin.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.presenter.PersonalInfoPresenter;
import com.qa.fgj.baymin.ui.view.IPersonalInfoView;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.RoundImageView;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoActivity extends BaseActivity<PersonalInfoPresenter> implements IPersonalInfoView,
        View.OnClickListener{

    public static final int REQUEST_CODE = 0xaa12;

    Toolbar toolbar;
    RelativeLayout avatarLayout;
    RoundImageView avatarView;
    LinearLayout nicknameLayout;
    TextView nickname;
    LinearLayout emailLayout;
    TextView email;
    TextView growth;
    RadioGroup sexGroup;
    RadioButton sexBoy;
    RadioButton sexGirl;
    RelativeLayout modifyPassword;
    TextView logout;

    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    /** 设置头像标记：从相册中选择、拍照、裁剪 **/
    public static final int FROM_ALBUM = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PICTURE = 2;
    private boolean isAllowOpenAlbum = true;
    private boolean isAllowOpenCamera = true;
    private String avatarName;
    private Uri imgUri;
    private String imgPath;

    private PersonalInfoPresenter presenter;
    private UserBean mUser = new UserBean();

    public static void start(final Context context){
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(final Activity activity){
        Intent intent = new Intent(activity, PersonalInfoActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_personal_info);
        initView();
        setEvenListener();
        presenter = new PersonalInfoPresenter();
        presenter.attachView(this);
        presenter.onCreate();
        fetchData();
    }

    private void initView(){
        initToolbar();
        avatarLayout = (RelativeLayout) findViewById(R.id.avatar_layout);
        avatarView = (RoundImageView) findViewById(R.id.avatar);
        nicknameLayout = (LinearLayout) findViewById(R.id.nickname_layout);
        nickname = (TextView) findViewById(R.id.nickname);
        emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        email = (TextView) findViewById(R.id.email);
        growth = (TextView) findViewById(R.id.growth);
        sexGroup = (RadioGroup) findViewById(R.id.sex_group);
        sexBoy = (RadioButton) findViewById(R.id.select_boy);
        sexGirl = (RadioButton) findViewById(R.id.select_girl);
        modifyPassword = (RelativeLayout) findViewById(R.id.modify_password_layout);
        logout = (TextView) findViewById(R.id.logout);
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_information));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("user", mUser);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void setEvenListener() {
        avatarLayout.setOnClickListener(this);
        nicknameLayout.setOnClickListener(this);
        emailLayout.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
        logout.setOnClickListener(this);
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.select_girl){

                }

            }
        });
    }

    private void fetchData(){
        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        avatarName = account + ".jpg";
        presenter.fetchData(account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avatar_layout:
                showChoosePictureDialog();
                break;
            case R.id.nickname_layout:

                break;
            case R.id.email_layout:

                break;
            case R.id.modify_password_layout:

                break;
            case R.id.logout:

                break;
        }
    }

    private void showChoosePictureDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_user_image);
        String fromAlbum = getString(R.string.selectFromAlbum);
        String takePhoto = getString(R.string.takePhoto);
        final String[] selectItems = {fromAlbum, takePhoto};
        builder.setItems(selectItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case FROM_ALBUM:
                        if (isAllowOpenAlbum){
                            openAlbum();
                        } else {
                            ToastUtil.show(getString(R.string.request_storage_tip));
                        }
                        break;
                    case TAKE_PHOTO:
                        if (isAllowOpenCamera){
                            openCamera();
                        } else {
                            ToastUtil.show(getString(R.string.request_camera_tip));
                        }
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void openAlbum(){
        //4.4及以上版本，TODO 4.4以下待测试
        Intent openAlbum = new Intent(Intent.ACTION_PICK);
        //Intent openAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //Intent openAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbum.setType("image/*");
        if (openAlbum.resolveActivity(getPackageManager()) != null){
            startActivityForResult(openAlbum, FROM_ALBUM);
        } else {
            ToastUtil.show(getString(R.string.no_album));
        }
    }

    private void openCamera(){
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCamera.resolveActivity(getPackageManager()) != null){
            File outputImg = new File(Environment.getExternalStorageDirectory(), avatarName);
            imgUri = Uri.fromFile(outputImg);
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            startActivityForResult(openCamera, TAKE_PHOTO);
        }else {
            ToastUtil.show(getString(R.string.no_camera));
        }
    }

    @Override
    public void showData(UserBean userBean) {
        if (userBean != null){
//            avatarView.setImageResource();
            nickname.setText(userBean.getUsername());
            email.setText(userBean.getEmail());
            growth.setText(userBean.getGrowthValue());
            if ("男".equals(userBean.getSex())){
                sexBoy.setChecked(true);
            } else {
                sexGirl.setChecked(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    cropPicture(imgUri);
                    break;
                case FROM_ALBUM:
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT>=19) {
                        //4.4及以上
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                    break;
                case CROP_PICTURE:
                    if (data != null){
                        handlePicture(data);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 裁剪图片
     */
    private void cropPicture(Uri uri){
        imgUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //设置裁剪比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CROP_PICTURE);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if ("com.android.providers.downloads.documents".equals(uri
                    .getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://" +
                        "downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            // 如果是file类型, 则直接获取
            imagePath = uri.getPath();
        } else {
            imagePath = getImagePath(uri,null);
        }
        displayImage(imagePath);
    }

    /**
     * API19以下的手机处理方法
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    /**
     * 裁剪完图片后进行处理，如显示到屏幕、保存到本地、上传到服务器
     */
    private void handlePicture(Intent data){
//        Bitmap photo = null;
//        try {
//            photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        PhotoUtils photoUtils = new PhotoUtils(this);
//        if (photo != null) {
//            imgPath = photoUtils.savePhoto(photo, Environment.getExternalStorageDirectory()
//                    .getAbsolutePath()+ "/" + getString(R.string.app_name) + "/image", FACE_IMG_NAME);
//            displayImage(imgPath);
//            tempImagePath = imgPath;
//        }
    }

    private String getImagePath(Uri uri, String selection) {
        if (uri == null) {
            return null;
        }
        String path = null;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, filePathColumn, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            imgPath = imagePath;
//            Bitmap bitmap = PhotoUtils.getSpecifiedBitmap(imagePath, imageWidth, imageHeight);
//            avatarView.setImageBitmap(bitmap);
//            tempImagePath = imagePath;
//            updateUserInfo();
        } else {
            ToastUtil.shortShow(getString(R.string.no_camera));
        }
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
