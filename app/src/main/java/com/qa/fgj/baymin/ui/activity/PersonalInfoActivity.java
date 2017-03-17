package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.qa.fgj.baymin.util.PhotoUtils;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.EditDialog;
import com.qa.fgj.baymin.widget.RoundImageView;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 个人信息页
 * Created by FangGengjia on 2017/2/19.
 */

public class PersonalInfoActivity extends BaseActivity implements IPersonalInfoView,
        View.OnClickListener{

    public static final int REQUEST_CODE = 0xaa12;

//    Toolbar toolbar;
    ImageView back;
    TextView saveChange;
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
    private PhotoUtils photoUtils;
    private boolean isAllowOpenAlbum = true;
    private boolean isAllowOpenCamera = true;
    private String avatarName;
    private static final int AVATAR_WITH = 100;
    private static final int AVATAR_HEIGHT = 100;
    //存储拍照后指定的图片路径
    private Uri imgUri;
    private String newAvatarPath;

    private Scheduler uiThread = AndroidSchedulers.mainThread();
    private Scheduler backgroundThread = Schedulers.io();
    private PersonalInfoPresenter presenter;
    private UserBean mUser = new UserBean();

    //根据处理结果返回的图片路径显示图片
    private Subscriber<String> showImageSubscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            ToastUtil.show("图片路径解析错误");
        }

        @Override
        public void onNext(String imagePath) {
            displayImage(imagePath);
        }
    };

    public static void start(final Context context){
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(final Activity activity, String account){
        Intent intent = new Intent(activity, PersonalInfoActivity.class);
        intent.putExtra("account", account);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initView();
        setEvenListener();
        photoUtils = new PhotoUtils(this);
        presenter = new PersonalInfoPresenter(this, uiThread, backgroundThread);
        presenter.onCreate();
        presenter.attachView(this);
        fetchData();
    }

    private void initView(){
//        initToolbar();
        back = (ImageView) findViewById(R.id.back);
        ((TextView)findViewById(R.id.title_name)).setText(R.string.my_information);
        saveChange = (TextView) findViewById(R.id.operation);
        saveChange.setText(R.string.save);
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

//    private void initToolbar(){
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        saveChange = (TextView) toolbar.findViewById(R.id.save);
//        toolbar.setTitle(getString(R.string.my_information));
//        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
//        toolbar.setNavigationIcon(R.drawable.back);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent data = new Intent();
//                data.putExtra("user", mUser);
//                setResult(RESULT_OK, data);
//                finish();
//            }
//        });
//    }

    private void setEvenListener() {
        back.setOnClickListener(this);
        saveChange.setOnClickListener(this);
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
    public void showData(UserBean userBean) {
        if (userBean != null){
//            displayImage(userBean.getImagePath());
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent data = new Intent();
                data.putExtra("user", mUser);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.operation:
                ToastUtil.show("save");
                break;
            case R.id.avatar_layout:
                showChoosePictureDialog();
                break;
            case R.id.nickname_layout:
                showModifyNicknameDialog();
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
                    case PhotoUtils.FROM_ALBUM:
                        if (isAllowOpenAlbum){
                            presenter.openAlbum();
                        } else {
                            ToastUtil.show(getString(R.string.request_storage_tip));
                        }
                        break;
                    case PhotoUtils.TAKE_PHOTO:
                        if (isAllowOpenCamera){
                            imgUri = presenter.openCamera(avatarName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.TAKE_PHOTO:
                    presenter.cropPicture(imgUri);
                    break;
                case PhotoUtils.FROM_ALBUM:
                    presenter.handleAlbumPicture(data.getData(), showImageSubscriber);
                    break;
                case PhotoUtils.CROP_PICTURE:
                    presenter.handleCropPicture(imgUri, avatarName, showImageSubscriber);
                    break;
                default:
                    break;
            }
        }
    }

    public void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = photoUtils.getSpecifiedBitmap(imagePath, AVATAR_WITH, AVATAR_HEIGHT);
            avatarView.setImageBitmap(bitmap);
            newAvatarPath = imagePath;
            updateUserInfo();
        } else {
            avatarView.setImageResource(R.drawable.default_user_image);
            ToastUtil.shortShow("找不到头像");
        }
    }

    private void showModifyNicknameDialog(){
//        final String[] newNickname = new String[1];
//        LayoutInflater layoutInflater = PersonalInfoActivity.this.getLayoutInflater();
//        final View editNickname = layoutInflater.inflate(R.layout.modify_nickname, null);
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.change_nickname))
//                    .setView(editNickname)
//                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            EditText editNewName = (EditText) editNickname.findViewById(R.id.new_nickname);
//                            newNickname[1] = editNewName.getText().toString().trim();
//                            if (newNickname[1].length() < 3){
//                                editNewName.setError(getString(R.string.username_wrong_length));
//                            }
//                        }
//                    })
//                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).create();
//            dialog.show();
        final EditDialog dialog = new EditDialog(this);
                dialog.setTitleText(R.string.change_nickname)
                .setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.cancel_button){
                            ToastUtil.show("click cancel");
                        } else {
                            ToastUtil.show("click yes");
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 更新本地头像路径以及上传头像至服务器
     */
    protected void updateUserInfo(){
//        if (newAvatarPath != null && !newAvatarPath.equals(user.getImagePath())){
//            user.setImagePath(newAvatarPath);
//            isChange = true;
//            UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(this);
//            uploadAsyncTask.execute(tempImagePath, user.getEmail());
//            uploadAsyncTask.setAsyncResponse(new AsyncResponse() {
//                @Override
//                public void onDataReceivedSuccess(List<String> list) {
//                    Toast.makeText(PersonalInfoActivity.this, list.get(0),Toast.LENGTH_LONG ).show();
//                }
//
//                public void onDataReceivedFailed() {
//                    Toast.makeText(PersonalInfoActivity.this, getString(R.string.upload_failed),Toast.LENGTH_LONG ).show();
//                }
//            });
//        }
//        if (isChange){
//            Global.userInfoDB.update(user);
//            isChange = false;
//        }
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
        presenter.detachView();
        presenter.onDestroy();
    }
}
