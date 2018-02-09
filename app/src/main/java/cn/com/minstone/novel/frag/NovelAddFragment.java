package cn.com.minstone.novel.frag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.com.minstone.novel.R;
import cn.com.minstone.novel.adapter.NovelTypeAdapter;
import cn.com.minstone.novel.base.BaseFragment;
import cn.com.minstone.novel.bean.Novel;
import cn.com.minstone.novel.bean.User;
import cn.com.minstone.novel.event.NovelUpdateEvent;
import cn.com.minstone.novel.util.DisplayUtil;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/***
 * 新增小说界面
 *
 * @since 2018/1/30
 * @author king
 */

public class NovelAddFragment extends BaseFragment implements View.OnClickListener {

    private EditText etName;
    private EditText etIntroduction;
    private ImageView ivAddCover;
    private TextView tvOk;
    private Spinner spinner;

    private List<String> typeList;

    private String name;
    private String introduction;
    private String imagePath;
    private String novelType;


    public static NovelAddFragment newInstance() {

        return new NovelAddFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_novel;
    }

    @Override
    protected void initView() {
        setTitle("创建小说");
        enableBack();
        ivBack.setOnClickListener(this);
        etName = rootView.findViewById(R.id.et_name);
        etIntroduction = rootView.findViewById(R.id.et_introduction);
        ivAddCover = rootView.findViewById(R.id.iv_add_cover);
        ivAddCover.setOnClickListener(this);
        tvOk = rootView.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(this);
        spinner = rootView.findViewById(R.id.sp_type);

    }

    @Override
    protected void initData() {
        if (typeList == null) {
            typeList = new ArrayList<>();
        }
        String[] typeArray = getResources().getStringArray(R.array.novelType);
        novelType = typeArray[0];
        typeList = Arrays.asList(typeArray);
        NovelTypeAdapter adapter = new NovelTypeAdapter(getContext(), typeList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                novelType = typeList.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                pop();
                break;
            case R.id.iv_add_cover:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 109);

                break;
            case R.id.tv_ok:
                name = etName.getText().toString().trim();
                introduction = etIntroduction.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("作品名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(novelType)) {
                    showToast("请选择小说类型");
                    return;
                }
                DisplayUtil.hideSoftKeyboard(getActivity(), etName);
                showLoading();
                if (imagePath != null) {
                    uploadCover(imagePath);
                    return;
                }

                createNovel(name, introduction, null);
                break;
        }
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(getActivity().getExternalCacheDir(), "cover.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePath = file.getAbsolutePath();
        Uri outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 4);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", DisplayUtil.dip2px(getActivity(), 120));
        intent.putExtra("outputY", DisplayUtil.dip2px(getActivity(), 160));
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, 110);
    }

    /**
     * 创建小说
     *
     * @param name
     * @param introduction
     * @param cover
     */
    private void createNovel(String name, String introduction, BmobFile cover) {
        User user = BmobUser.getCurrentUser(User.class);
        Novel novel = new Novel();
        novel.setName(name);
        novel.setIntroduction(introduction);
        novel.setAuthor(user);
        novel.setCover(cover);
        novel.setType(novelType);
        novel.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                hideLoading();
                if (e != null) {
                    Toast.makeText(getActivity(), "创建失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBusActivityScope.getDefault(getActivity()).post(new NovelUpdateEvent());
                Toast.makeText(getActivity(), "创建成功", Toast.LENGTH_SHORT).show();
                pop();
            }
        });
    }

    /**
     * 上传封面图片
     *
     * @param filePath
     */
    private void uploadCover(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "图片不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e != null) {
                    hideLoading();
                    Log.e("king", e.getMessage(), e);
                    showToast("上传封面失败");
                    return;
                }
                createNovel(name, introduction, bmobFile);
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ivAddCover.setImageBitmap(bitmap);
                    break;
            }
        }
    }
}
