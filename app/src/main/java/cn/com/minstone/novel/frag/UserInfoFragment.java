package cn.com.minstone.novel.frag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.res.values.CircleDimen;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.util.DisplayUtil;
import cn.com.minstone.novel.util.ImageUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/***
 * 用户信息界面
 *
 * @since 2018/1/31
 * @author king
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView ivAvatar;
    private TextView tvAccount;
    private TextView tvNick;
    private TextView tvSign;

    private RelativeLayout rlNick;

    private User currentUser;

    private File imageFile;
    private Uri imageUri;

    public static UserInfoFragment newInstance() {

        return new UserInfoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initView() {
        setTitle("个人信息");
        enableBack();
        ivBack.setOnClickListener(this);
        tvAccount = rootView.findViewById(R.id.tv_account);
        tvSign = rootView.findViewById(R.id.tv_sign);
        tvSign.setOnClickListener(this);
        tvNick = rootView.findViewById(R.id.tv_nick);
        rlNick = rootView.findViewById(R.id.rl_nick);
        rlNick.setOnClickListener(this);
        ivAvatar = rootView.findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        currentUser = BmobUser.getCurrentUser(User.class);

        if (currentUser != null) {
            tvSign.setText(currentUser.getSign() == null ? "暂无签名" : currentUser.getSign());
            tvAccount.setText(currentUser.getUsername());
            tvNick.setText(currentUser.getNick() == null ? "未填写" : currentUser.getNick());
            if (currentUser.getAvatar() != null) {
                ImageUtil.showImage(getActivity(), currentUser.getAvatar().getFileUrl(), ivAvatar);
            }
        } else {
            showToast("加载用户信息失败");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                pop();
                break;
            case R.id.iv_avatar:
                generateFile();
                showUpdateAvatarDialog();
                break;

            case R.id.tv_sign:
                showUpdateSignDialog();
                break;
            case R.id.rl_nick:
                showUpdateNickDialog();
                break;
        }
    }

    /**
     * 头像文件
     */
    private void generateFile() {
        File file = new File(getActivity().getExternalCacheDir(), "avatar.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageFile = file;
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    /**
     * 修改笔名对话框
     */
    private void showUpdateNickDialog() {

        new CircleDialog.Builder(getActivity())
                .setTitle("修改笔名")
                .setInputHint("请输入笔名")
                .setInputText(currentUser.getNick())
                .setInputHeight(DisplayUtil.dip2px(getActivity(), 44))
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        showToast("请输入笔名");
                        return;
                    }
                    updateNick(text);

                }).show();

    }

    /**
     * 修改笔名
     * @param nick
     */
    private void updateNick(String nick) {
        User newUser = new User();
        newUser.setNick(nick);
        newUser.update(currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    Log.e("king",e.getMessage(),e);
                    showToast("修改失败");
                    return;
                }
                showToast("修改成功");
                tvNick.setText(nick);
            }
        });
    }

    /**
     * 修改签名对话框
     */
    private void showUpdateSignDialog() {

        new CircleDialog.Builder(getActivity())
                .setTitle("修改个性签名")
                .setInputHint("请输入个性签名")
                .setInputText(currentUser.getSign())
                .setInputHeight(DisplayUtil.dip2px(getActivity(), 132))
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize = CircleDimen.CONTENT_TEXT_SIZE;
                    }
                })
                .configInput(new ConfigInput() {
                    @Override
                    public void onConfig(InputParams params) {
                        params.gravity = Gravity.START;
                    }
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        showToast("请输入个性签名");
                        return;
                    }
                    updateSign(text);

                }).show();

    }

    /**
     * 修改签名
     * @param sign
     */
    private void updateSign(String sign) {
        User newUser = new User();
        newUser.setSign(sign);
        newUser.update(currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    showToast("修改失败");
                    return;
                }
                showToast("修改成功");
                tvSign.setText(sign);
            }
        });
    }


    /**
     * 选择头像对话框
     */
    private void showUpdateAvatarDialog() {
        final String[] items = {"拍照", "从相册选择"};
        new CircleDialog.Builder(getActivity())
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        params.animStyle = R.style.PopupAnimation;
                    }
                })
                .setItems(items, (parent, view, position, id) -> {
                    if (position == 0) {
                        // 启动系统提供的拍照Activity
                        Intent it = new Intent();
                        it.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            it.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", imageFile);
                        } else {
                            imageUri = Uri.fromFile(imageFile);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(it, 108);

                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, 109);
                    }
                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .show();
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Uri outputUri = Uri.fromFile(imageFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", DisplayUtil.dip2px(getActivity(), 100));
        intent.putExtra("outputY", DisplayUtil.dip2px(getActivity(), 100));
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, 110);
    }

    /**
     * 上传头像
     *
     * @param filePath
     */
    private void uploadAvatar(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "图片不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading("上传中");
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e != null) {
                    hideLoading();
                    Log.e("king", e.getMessage(), e);
                    showToast("上传头像失败");
                    return;
                }
                updateAvatarInfo(bmobFile);
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    /**
     * 上传完毕，更新数据库记录
     * @param avatar
     */
    private void updateAvatarInfo(BmobFile avatar) {
        User user = new User();
        user.setAvatar(avatar);
        user.update(currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                hideLoading();
                if (e != null) {
                    showToast("修改失败");
                    return;
                }
                showToast("修改成功");
                ImageUtil.showImage(getActivity(), avatar.getFileUrl(), ivAvatar);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 108:
                    if (imageUri != null) {
                        cropPhoto(imageUri);
                    }
                    break;
                case 109://打开相册
                    if (data == null) {
                        return;
                    }
                    if (data != null) {
                        Uri uri = data.getData();
                        cropPhoto(uri);
                    }
                    break;
                case 110://裁剪完成
                    if (imageFile != null) {
                        uploadAvatar(imageFile.getAbsolutePath());
                    } else {
                        showToast("图片不存在");
                    }
                    break;
            }
        }
    }

}
