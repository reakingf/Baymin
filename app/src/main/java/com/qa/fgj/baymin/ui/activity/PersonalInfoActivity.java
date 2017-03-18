package com.qa.fgj.baymin.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.qa.fgj.baymin.widget.EditableDialog;
import com.qa.fgj.baymin.widget.ModifyPasswordDialog;
import com.qa.fgj.baymin.widget.RoundImageView;
import com.qa.fgj.baymin.widget.SelectableDialog;
import com.qa.fgj.baymin.widget.ShowTipDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.util.Patterns.EMAIL_ADDRESS;

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
    RelativeLayout modifyPasswordLayout;
    TextView logout;

    private SelectableDialog avatarDialog;
    private EditableDialog modifyNameDialog;
    private EditableDialog modifyEmailDialog;
    private ModifyPasswordDialog modifyPasswordDialog;
    private ShowTipDialog logoutDialog;

    private PhotoUtils photoUtils;
    private boolean isAllowOpenAlbum = true;
    private boolean isAllowOpenCamera = true;
    private String avatarName;
    private static final int AVATAR_WITH = 100;
    private static final int AVATAR_HEIGHT = 100;
    //存储拍照后指定的图片路径
    private Uri imgUri;
    private String newAvatarPath;
    private boolean isChange = false;

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
        modifyPasswordLayout = (RelativeLayout) findViewById(R.id.modify_password_layout);
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
        modifyPasswordLayout.setOnClickListener(this);
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
                showSetAvatarDialog();
                break;
            case R.id.nickname_layout:
                showModifyNicknameDialog();
                break;
            case R.id.email_layout:
                showModifyEmailDialog();
                break;
            case R.id.modify_password_layout:
                showModifyPasswordDialog();
                break;
            case R.id.logout:
                showLogoutDialog();
                break;
        }
    }

    private void showSetAvatarDialog() {
        avatarDialog = new SelectableDialog(this);
        avatarDialog.setTitleText(getString(R.string.set_user_image));
        avatarDialog.setFirstItem(getString(R.string.selectFromAlbum), new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                if (isAllowOpenAlbum){
                    presenter.openAlbum();
                } else {
                    ToastUtil.show(getString(R.string.request_storage_tip));
                }
                avatarDialog.dismiss();
            }
        });
        avatarDialog.setSecondItem(getString(R.string.takePhoto), new SelectableDialog.ItemClickListener() {
            @Override
            public void onClick() {
                if (isAllowOpenCamera){
                    imgUri = presenter.openCamera(avatarName);
                } else {
                    ToastUtil.show(getString(R.string.request_camera_tip));
                }
                avatarDialog.dismiss();
            }
        });
//        dialog.setButton(getString(R.string.cancel), null);
        avatarDialog.show();
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
        modifyNameDialog = new EditableDialog(this);
        modifyNameDialog.setTitleText(getString(R.string.change_nickname));
        modifyNameDialog.setEditTextHint(getString(R.string.change_nickname_hint));
        modifyNameDialog.setPositiveButton(null, new EditableDialog.onPositiveButtonClick(){
            @Override
            public void onClick() {
                if (modifyNameDialog.getEditText().length() >= 3) {
                    changeNickName(modifyNameDialog.getEditText());
                    modifyNameDialog.dismiss();
                } else {
                    modifyNameDialog.setEditTextError(getString(R.string.username_wrong_length));
                }
            }
        });
        modifyNameDialog.setNegativeButton(null, new EditableDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                modifyNameDialog.dismiss();
            }
        });
        modifyNameDialog.show();
    }

    private void showModifyEmailDialog(){
        modifyEmailDialog = new EditableDialog(this);
        modifyEmailDialog.setTitleText(getString(R.string.change_email));
        modifyEmailDialog.setEditTextHint(getString(R.string.change_email_hint));
        modifyEmailDialog.setPositiveButton(null, new EditableDialog.onPositiveButtonClick(){
            @Override
            public void onClick() {
                if (EMAIL_ADDRESS.matcher(modifyEmailDialog.getEditText()).matches()) {
                    changeEmail(modifyEmailDialog.getEditText());
                    modifyEmailDialog.dismiss();
                } else {
                    modifyEmailDialog.setEditTextError(getString(R.string.wrong_email));
                }
            }
        });
        modifyEmailDialog.setNegativeButton(null, new EditableDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                modifyEmailDialog.dismiss();
            }
        });
        modifyEmailDialog.show();
    }

    private void showModifyPasswordDialog(){
        modifyPasswordDialog = new ModifyPasswordDialog(this);
        modifyPasswordDialog.setTitleText(getString(R.string.change_password));
        modifyPasswordDialog.setPositiveButton(getString(R.string.yes), new ModifyPasswordDialog.onPositiveButtonClick() {
            @Override
            public void onClick() {
                String srcPassword = modifyPasswordDialog.getSrcPassword();
                String newPassword = modifyPasswordDialog.getNewPassword();
                String confirmPassword = modifyPasswordDialog.getConfirmPassword();
                modifyPasswordLayout(modifyPasswordDialog, srcPassword, newPassword, confirmPassword);
            }
        });
        modifyPasswordDialog.setNegativeButton(getString(R.string.cancel), new ModifyPasswordDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                modifyPasswordDialog.dismiss();
            }
        });
        modifyPasswordDialog.show();
    }

    private void changeNickName(String newName){
        if (newName != null && !nickname.getText().toString().equals(newName)){
            nickname.setText(newName);
            isChange = true;
        }
    }

    private void changeEmail(String newEmail){
        if (newEmail != null && !email.getText().toString().equals(newEmail)){
            email.setText(newEmail);
            isChange = true;
        }
    }

    private void modifyPasswordLayout(ModifyPasswordDialog dialog, String srcPassword,
                                      String newPassword, String confirmPassword) {
        if (!validatePassword(srcPassword)){
            dialog.setSrcPasswordError(getString(R.string.psw_wrong_length));
        } else if (!validatePassword(newPassword)){
            dialog.setNewPasswordError(getString(R.string.psw_wrong_length));
        } else if (!newPassword.equals(confirmPassword)){
            dialog.setConfirmPasswordError(getString(R.string.wrong_to_confirm_psw));
        }
        // TODO: 2017/3/18 验证原密码
    }

    private boolean validatePassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
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

    private void showLogoutDialog() {
        logoutDialog = new ShowTipDialog(this);
        logoutDialog.setTitleText("提示信息");
        logoutDialog.setContentText(getString(R.string.log_out));
        logoutDialog.setPositiveButton(getString(R.string.yes), new ShowTipDialog.onPositiveButtonClick() {
            @Override
            public void onClick() {
                presenter.logout();
                logoutDialog.dismiss();
            }
        });
        logoutDialog.setNegativeButton(getString(R.string.cancel), new ShowTipDialog.onNegativeButtonClick() {
            @Override
            public void onClick() {
                logoutDialog.dismiss();
            }
        });
        logoutDialog.show();
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    private void destroyDialog(Dialog dialog){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDialog(avatarDialog);
        destroyDialog(modifyNameDialog);
        destroyDialog(modifyEmailDialog);
        destroyDialog(modifyPasswordDialog);
        destroyDialog(logoutDialog);
        presenter.detachView();
        presenter.onDestroy();
    }
}
