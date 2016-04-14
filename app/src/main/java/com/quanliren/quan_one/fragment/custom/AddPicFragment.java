package com.quanliren.quan_one.fragment.custom;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.date.PhotoAlbumMainActivity_;
import com.quanliren.quan_one.activity.date.PublishActivity_;
import com.quanliren.quan_one.custom.RoundAngleImageView;
import com.quanliren.quan_one.fragment.base.BaseFragment;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.add_pic_fragment)
public class AddPicFragment extends BaseFragment {

    //图片路径
    public ArrayList<String> imagePath = new ArrayList<String>();

    public static final String DEFAULT = "default";

    public static final int Album = 2, Camera = 1;

    //图片总数
    @FragmentArg
    int maxNum = 3;
    //列数
    @FragmentArg
    int colNum = 4;
    //图片宽度大小，根据列数计算
    int imageWidth = 0;

    //父窗体
    @ViewById(R.id.pic_ll)
    LinearLayout pic_ll;

    //默认文字
    TextView textView;

    //临时生成的照相文件
    public String cameraPath;

    //被点击的图片
    ImageView tempImageView = null;

    @Override
    public void init() {
        super.init();

        //计算图片宽度
        imageWidth = (int) ((float) (getResources().getDisplayMetrics().widthPixels - ImageUtil
                .dip2px(getActivity(), 24 + (colNum - 1) * colNum)) / colNum);

        initSource(imagePath);
    }

    @UiThread
    public void initSource(ArrayList<String> list) {
        if (getActivity() instanceof PublishActivity_) {
            ((PublishActivity_)getActivity()).isPbPicSelected();
        }

        List<String> imagePath = (ArrayList<String>) list.clone();
        //移除所有view
        pic_ll.removeAllViews();

        //如果已添加的图片小于最大数量，则添加一个临时值
        if (imagePath.size() < maxNum) {
            imagePath.add("-1");
        }

        //计算总列数
        int lines = imagePath.size() / colNum;
        if (imagePath.size() % colNum > 0) {
            lines++;
        }
        //生成LinerLayout行
        for (int i = 0; i < lines; i++) {
            pic_ll.addView(createLayout());
        }

        for (int i = 0; i < imagePath.size(); i++) {
            //计算在第几行
            int num = i == 0 ? 0 : i / colNum;
            LinearLayout ll = (LinearLayout) pic_ll.getChildAt(num);

            ImageView iv;
            //创建图片
            ll.addView(iv = createImageView(i));

            //如果不是临时图，设置图片
            if (!imagePath.get(i).equals("-1")) {
                iv.setTag(imagePath.get(i));
                //赋值图片
                ImageLoader.getInstance().displayImage(Util.FILE + imagePath.get(i), iv);
            }
            //如果没有图片，显示提示文字
            if (imagePath.size() == 1) {
                ll.addView(textView = createTextView());
            }
        }
    }

    public ImageView createImageView(int i) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageWidth, imageWidth);
        int margin = ImageUtil.dip2px(getActivity(), 2);
        lp.setMargins(margin, margin, margin, margin);
        RoundAngleImageView image = new RoundAngleImageView(getActivity());
        image.setLayoutParams(lp);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageResource(R.drawable.publish_add_pic_icon_big);
        image.setTag(DEFAULT);
        image.setOnClickListener(imgClick);
        image.setId(i);
        return image;
    }

    View.OnClickListener imgClick = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            add_pic_btn(arg0);
        }
    };

    public TextView createTextView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = ImageUtil.dip2px(getActivity(), 10);
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(lp);
        tv.setText("添加图片");
        tv.setTextSize(14);
        tv.setTextColor(getResources().getColor(R.color.manage_member_text));
        return tv;
    }

    public LinearLayout createLayout() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setGravity(Gravity.CENTER_VERTICAL);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(lp);
        return ll;
    }

    public void add_pic_btn(View v) {
        tempImageView = (ImageView) v;
        if (v.getTag().toString().equals(AddPicFragment.DEFAULT)) {
            new AlertDialog.Builder(getActivity()).setItems(new String[]{"相机", "从相册中选择"}, menuClick).create().show();
        } else {
            new AlertDialog.Builder(getActivity()).setItems(new String[]{"删除"}, menuDeleteClick).create().show();
        }
        Utils.closeSoftKeyboard(getActivity());
    }

    DialogInterface.OnClickListener menuClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    if (Util.existSDcard()) {
                        Intent intent = new Intent(); // 调用照相机
                        String messagepath = StaticFactory.APKCardPath;
                        File fa = new File(messagepath);
                        if (!fa.exists()) {
                            fa.mkdirs();
                        }
                        cameraPath = messagepath + new Date().getTime();// 图片路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(cameraPath)));
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, AddPicFragment.Camera);
                    } else {
                        Toast.makeText(getActivity(), "亲，请检查是否安装存储卡!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    if (Util.existSDcard()) {
                        String messagepath = StaticFactory.APKCardPath;
                        File fa = new File(messagepath);
                        if (!fa.exists()) {
                            fa.mkdirs();
                        }
                        PhotoAlbumMainActivity_.intent(AddPicFragment.this).maxnum(maxNum).paths(imagePath).startForResult(AddPicFragment.Album);
                    } else {
                        Toast.makeText(getActivity(), "亲，请检查是否安装存储卡!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    DialogInterface.OnClickListener menuDeleteClick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    removeByView(tempImageView);
                    break;
            }
        }
    };

    public void removeByView(View view) {
        String url = view.getTag().toString();
        removeByPath(url);
    }

    public void removeByPath(String url) {
        imagePath.remove(url);

        File file = new File(url);
        if (file.exists()) {
            file.delete();
        }
        initSource(imagePath);
    }

    @OnActivityResult(Camera)
    public void onCameraResult(Intent data) {
        if (cameraPath != null) {
            File fi = new File(cameraPath);
            if (fi != null && fi.exists()) {
                ImageUtil.downsize(cameraPath, cameraPath, getActivity());
                addPath(cameraPath);
            }
        }
    }

    @OnActivityResult(Album)
    public void onAlubmResult(Intent data) {
        if (data == null) {
            return;
        }
        ArrayList<String> list = data.getStringArrayListExtra("images");
        customShowDialog("正在处理");
        replaceList(list);
    }

    public void addPath(String url) {
        imagePath.add(url);
        initSource(imagePath);
    }

    @Background
    public void replaceList(ArrayList<String> list) {
        imagePath = list;
        initSource(imagePath);
    }
}
