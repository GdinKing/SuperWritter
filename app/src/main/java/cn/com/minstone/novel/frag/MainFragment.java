package cn.com.minstone.novel.frag;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import cn.com.minstone.novel.R;
import cn.com.minstone.novel.base.BaseFragment;
import me.yokeyword.fragmentation.SupportFragment;

/***
 * 主Fragment，管理首页两个Tab
 *
 * @since 2018/1/30
 * @author king
 */

public class MainFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rgTabs;
    private RadioButton rbBook;
    private RadioButton rbMy;

    private SupportFragment[] mFragments = new SupportFragment[2];


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {
        rgTabs = rootView.findViewById(R.id.rg_tabs);
        rbBook = rootView.findViewById(R.id.rb_book);
        rbMy = rootView.findViewById(R.id.rb_my);
        rgTabs.setOnCheckedChangeListener(this);
        if(isNightMode){
            rbBook.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.tab_book_shell_dark,0,0);
            rbMy.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.tab_my_dark,0,0);
        }else{
            rbBook.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.tab_book_shell,0,0);
            rbMy.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.tab_my,0,0);
        }
        if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }

    @Override
    protected void initData() {

        initFragment();
    }

    /**
     * 初始化首页两个Tab
     */
    private void initFragment() {

        SupportFragment firstFragment = findChildFragment(NovelListFragment.class);
        if (firstFragment == null) {
            mFragments[0] = NovelListFragment.newInstance();//书架
            mFragments[1] = MyFragment.newInstance();//我的
            loadMultipleRootFragment(R.id.container, 0,
                    mFragments[0],
                    mFragments[1]);
        } else {
            mFragments[0] = firstFragment;
            mFragments[1] = findChildFragment(MyFragment.class);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {

            case R.id.rb_book:
                showHideFragment(mFragments[0], mFragments[1]);
                break;

            case R.id.rb_my:
                showHideFragment(mFragments[1], mFragments[0]);
                break;
        }

    }

    /**
     * 打开Fragment，类似startActivity
     * @param fragment
     */
    public void openFragment(SupportFragment fragment){
        start(fragment);
    }

    public void closeFragment(){
        pop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 100) {

            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "获取权限成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "没有读写存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
