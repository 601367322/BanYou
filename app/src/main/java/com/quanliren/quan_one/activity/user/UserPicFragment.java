package com.quanliren.quan_one.activity.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.date.PhotoAlbumMainActivity_;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity_;
import com.quanliren.quan_one.adapter.UserInfoPicAdapter;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.NoScrollGridView;
import com.quanliren.quan_one.custom.RoundProgressBar;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.UserTableDao;
import com.quanliren.quan_one.fragment.base.BaseFragment;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.user_pic_fragment_222)
public class UserPicFragment extends BaseFragment implements
        UserInfoPicAdapter.OnImageClickListener, OnPageChangeListener {

    @ViewById(R.id.viewpager)
    ViewPager viewpager;
    @ViewById(R.id.album_selecter)
    View album_selecter;
    @ViewById(R.id.page_select0)
    ImageView page_select0;
    @ViewById(R.id.page_select1)
    ImageView page_select1;

    @FragmentArg
    ArrayList<ImageBean> listSource = new ArrayList<>();
    @FragmentArg
    String userId;
    @FragmentArg
    boolean needPage;
    @FragmentArg
    int maxLine;
    @FragmentArg
    boolean needAddBtn;
    @FragmentArg
    boolean isGroup;
    @FragmentArg
    int groupType;
    @FragmentArg
    String groupId;
    int imgid;

    ArrayList<ImageBean> copyList = new ArrayList<>();
    PicPageAdapter adapter;
    User user;

    public void setList(ArrayList<ImageBean> list) {

        this.listSource = list;
        //复制一份
        this.copyList = (ArrayList<ImageBean>) list.clone();

        //显示添加按钮
        if (needAddBtn) {
            if (isGroup && this.copyList.size() == 8) {
            } else {
                this.copyList.add(new ImageBean(true));
            }
        }

        if (copyList != null && copyList.size() > 0) {
            //得到总行数
            int count = copyList.size() / 4;
            if (copyList.size() % 4 > 0) {
                count++;
            }
            //如果需要分页
            if (needPage) {
                count = Math.min(count, maxLine);
            }
            viewpager.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, getResources()
                    .getDisplayMetrics().widthPixels / 4 * count + ImageUtil.dip2px(getActivity(), 4)));

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                viewpager.setAdapter(adapter = new PicPageAdapter());
            }

            if (adapter.getCount() > 1) {
                album_selecter.setVisibility(View.VISIBLE);
            } else {
                album_selecter.setVisibility(View.GONE);
            }

            viewpager.addOnPageChangeListener(this);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                page_select0.setImageResource(R.drawable.ic_album_selected);
                page_select1.setImageResource(R.drawable.ic_album_normal);
                break;
            case 1:
                page_select1.setImageResource(R.drawable.ic_album_selected);
                page_select0.setImageResource(R.drawable.ic_album_normal);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void init() {
        super.init();
        user = ac.getUserInfo();
        setList(listSource);
    }

    public NoScrollGridView createGridView() {
        NoScrollGridView gridview = new NoScrollGridView(getActivity());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        gridview.setGravity(Gravity.CENTER);
        gridview.setNumColumns(4);
        gridview.setPadding(ImageUtil.dip2px(getActivity(), 4),
                ImageUtil.dip2px(getActivity(), 4),
                ImageUtil.dip2px(getActivity(), 4),
                ImageUtil.dip2px(getActivity(), 4));
        gridview.setVerticalSpacing(ImageUtil.dip2px(getActivity(), 4));
        gridview.setHorizontalSpacing(ImageUtil.dip2px(getActivity(), 4));
        gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridview.setLayoutParams(lp);
        return gridview;
    }

    class PicPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (needPage) {
                //得到总行数
                int count = copyList.size() / 4;
                if (copyList.size() % 4 > 0) {
                    count++;
                }
                int page = count / maxLine;
                if (count % maxLine > 0) {
                    page++;
                }
                return page;
            }
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridview = createGridView();
            List<ImageBean> imgs = new ArrayList<ImageBean>();
            for (int j = position * maxLine * 4; j < copyList.size(); j++) {
                imgs.add(copyList.get(j));
                if (imgs.size() == maxLine * 4) {
                    break;
                }
            }
            UserInfoPicAdapter adapter = new UserInfoPicAdapter(getActivity(),
                    imgs, UserPicFragment.this);
            gridview.setAdapter(adapter);
            container.addView(gridview);
            return gridview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void addImg() {
        uploadImg();
    }

    @Override
    public void imgClick(ImageBean id) {
        int position = -1;
        for (ImageBean ib : listSource) {
            if (ib.imgid == id.imgid && !ib.imgpath.startsWith(Util.FILE)) {
                position = listSource.indexOf(ib);
            }
        }
        if (position > -1) {
            MessageList ml = new MessageList();
            ml.imgList = listSource;
            ImageBrowserActivity_.intent(getActivity()).mPosition(position).mProfile((ArrayList<ImageBean>) ml.imgList).start();
        }

    }

    @Override
    public void imgLongClick(final ImageBean ibs) {
        if (isGroup && groupType == 2) {
            if (listSource.indexOf(ibs) != 0) {
                groupAblmUpload(ibs, 0);
            } else {
                groupAblmUpload(ibs, 1);
            }
        } else {
            if (user != null && user.getId().equals(userId)) {
                userAblmUpload(ibs);
            }
        }
    }

    public static final int EXCHANGEALBUM = 5, EXCHANGECAMERA = 4;

    public void groupAblmUpload(final ImageBean ibs, int type) {
        imgid = ibs.imgid;
        if (type == 0) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setItems(new String[]{"删除", "相册", "拍照"},
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    final int which) {
                                    switch (which) {
                                        case 0:
                                            RequestParams rp = getAjaxParams();
                                            rp.put("groupId", groupId);
                                            rp.put("imgid", ibs.imgid + "");
                                            rp.put("actiontype", "1");
                                            String progress = ("正在删除");
                                            ac.finalHttp.post(URL.EDIT_GROUP_ABLM, rp, new MyJsonHttpResponseHandler(getActivity(), progress) {
                                                @Override
                                                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                                    int index = 0;
                                                    for (ImageBean ib : listSource) {
                                                        if (ib.imgid == ibs.imgid) {
                                                            index = listSource
                                                                    .indexOf(ib);
                                                            break;
                                                        }
                                                    }
                                                    listSource.remove(index);
                                                    setList(listSource);
                                                    getActivity().setResult(Activity.RESULT_OK);
                                                }
                                            });
                                            break;
                                        case 1:
                                            callAlbum(null, 1, EXCHANGEALBUM);
                                            break;
                                        case 2:
                                            callCamera(EXCHANGECAMERA);
                                            break;
                                    }

                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else if (type == 1) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setItems(new String[]{"相册", "拍照"},
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    final int which) {
                                    RequestParams rp = getAjaxParams();
                                    rp.put("groupId", groupId);
                                    rp.put("imgid", ibs.imgid + "");
                                    switch (which) {
                                        case 0:
                                            callAlbum(null, 1, EXCHANGEALBUM);
                                            break;
                                        case 1:
                                            callCamera(EXCHANGECAMERA);
                                            break;
                                    }

                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

    }

    public void userAblmUpload(final ImageBean ibs) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setItems(new String[]{"设为头像", "删除"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                final int which) {
                                RequestParams rp = getAjaxParams();
                                rp.put("imgid", ibs.imgid + "");
                                if (which == 0) {
                                    rp.put("actiontype", "0");
                                } else if (which == 1) {
                                    rp.put("actiontype", "2");
                                }
                                String progress = "";
                                switch (which) {
                                    case 0:
                                        progress = ("正在设置");
                                        break;
                                    case 1:
                                        progress = ("正在删除");
                                        break;
                                }
                                ac.finalHttp.post(URL.SET_PICTURE, rp, new MyJsonHttpResponseHandler(getActivity(), progress) {
                                    @Override
                                    public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                        switch (which) {
                                            case 0:
                                                User user1 = ac.getUserInfo();
                                                user1.setAvatar(ibs.imgpath);
                                                UserTableDao.getInstance(getActivity()).updateUser(user1);
                                                Intent i = new Intent(UserInfoActivity.USERINFO_UPDATE_UI);
                                                if (getActivity() != null)
                                                    getActivity().sendBroadcast(i);
                                                break;
                                            case 1:
                                                int index = 0;
                                                for (ImageBean ib : listSource) {
                                                    if (ib.imgid == ibs.imgid) {
                                                        index = listSource
                                                                .indexOf(ib);
                                                        break;
                                                    }
                                                }
                                                listSource.remove(index);
                                                user.setImglist(listSource);
                                                UserTableDao.getInstance(getActivity()).updateUser(user);
                                                setList(listSource);
                                                break;
                                        }
                                    }
                                });
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private String picPath;
    public static final int Album = 6, Camera = 1;

    public void uploadImg() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("添加图片")
                .setItems(new String[]{"相册", "拍照"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 1:
                                        if (Util.existSDcard()) {
                                            Intent intent = new Intent(); // 调用照相机
                                            String messagepath = StaticFactory.APKCardPath;
                                            File fa = new File(messagepath);
                                            if (!fa.exists()) {
                                                fa.mkdirs();
                                            }
                                            picPath = messagepath
                                                    + new Date().getTime();// 图片路径
                                            intent.putExtra(
                                                    MediaStore.EXTRA_OUTPUT,
                                                    Uri.fromFile(new File(picPath)));
                                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(intent, Camera);
                                        } else {
                                            showCustomToast("亲，请检查是否安装存储卡!");
                                        }
                                        break;
                                    case 0:
                                        if (isGroup) {
                                            callAlbum(new ArrayList<String>(), 8, Album);
                                        } else {
                                            callAlbum(new ArrayList<String>(), user.getIsvip() == 0 ? 8 : 16, Album);
                                        }
                                        break;
                                }
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void replaceList(ArrayList<String> list) {
        List<String> liststr = new ArrayList<String>();
        for (String string : list) {

            if (!string.equals("")) {
                ImageUtil.downsize(string, string = StaticFactory.APKCardPath
                        + string.hashCode(), getActivity());
                liststr.add(string);
            }
        }
        uploadImgByList(liststr, 0);
        initSource();

    }

    public void uploadImgByList(final List<String> list, final int position) {
        if (position == list.size()) {
            return;
        }
        RequestParams rp = getAjaxParams();
        try {
            rp.put("file", new File(list.get(position)));

            this.listSource.add(new ImageBean(Util.FILE + list.get(position)));
            initSource();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = URL.UPLOAD_ALBUM_IMG;
        if (isGroup && groupType == 2) {
            rp.put("groupId", groupId);
            url = URL.UPLOAD_GROUP_ABLM;
        }
        ac.finalHttp.post(url, rp, new MyJsonHttpResponseHandler() {

            RoundProgressBar rpb = null;

            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                try {
                    ImageBean ibs = listSource.get(listSource.size() - 1);
                    ibs.imgid = jo.getJSONObject(URL.RESPONSE).getInt("imgid");
                    ibs.imgpath = jo.getJSONObject(URL.RESPONSE).getString("imgurl");
                    if (isGroup) {
                        getActivity().setResult(Activity.RESULT_OK);
                    } else {
                        user.setImglist(listSource);

                        DBHelper.userTableDao.updateUser(user);
                    }

                    if (listSource.size() == maxLine * 4 + 1) {
                        viewpager.setCurrentItem(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rpb != null)
                        rpb.setVisibility(View.GONE);
                }
                if (position == list.size() - 1) {
                    setList(listSource);
                } else {
                    uploadImgByList(list, position + 1);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int progress = (int) (((float) bytesWritten / (float) totalSize) * 100);
                if (progress > 0 && progress < 100) {
                    if (rpb == null) {
                        try {
                            int viewposition = UserPicFragment.this.listSource.size() - 1;
                            View pagerView = viewpager.getChildAt(viewposition > maxLine * 4 - 1 ? 1 : 0);
                            if (pagerView instanceof NoScrollGridView) {
                                Object obj = ((NoScrollGridView) pagerView).getChildAt(viewposition);
                                if (obj != null) {
                                    rpb = (RoundProgressBar) ((View) obj).findViewById(R.id.loadProgressBar);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        rpb.setVisibility(View.VISIBLE);
                        rpb.setProgress((int) (((float) bytesWritten / (float) totalSize) * 100));
                    }
                }
            }
        });
    }

    ProgressDialog progressDialog = null;

    void initSource() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        setList(listSource);
    }

    /**
     * 调用相机，传参请求码
     *
     * @param requestCode
     */
    void callCamera(int requestCode) {
        if (Util.existSDcard()) {
            Intent intent = new Intent(); // 调用照相机
            String messagepath = StaticFactory.APKCardPath;
            File fa = new File(messagepath);
            if (!fa.exists()) {
                fa.mkdirs();
            }
            picPath = messagepath
                    + new Date().getTime();// 图片路径
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(picPath)));
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, requestCode);
        } else {
            showCustomToast("亲，请检查是否安装存储卡!");
        }
    }

    /**
     * 访问相册
     * @param list
     * @param maxNum 可选择的最多张数
     * @param requestCode 请求码
     */
    void callAlbum(ArrayList<String> list, int maxNum, int requestCode) {
        if (Util.existSDcard()) {
            String messagepath = StaticFactory.APKCardPath;
            File fa = new File(messagepath);
            if (!fa.exists()) {
                fa.mkdirs();
            }
            if (list == null) {
                PhotoAlbumMainActivity_.intent(UserPicFragment.this).maxnum(maxNum).startForResult(requestCode);
            } else {
                ArrayList<String> tempList = new ArrayList<String>();
                for (int i = 0; i < listSource.size(); i++) {
                    tempList.add("");
                }
                PhotoAlbumMainActivity_.intent(UserPicFragment.this).maxnum(maxNum).paths(tempList).startForResult(requestCode);
            }

        } else {
            showCustomToast("亲，请检查是否安装存储卡!");
        }
    }

    /**
     * 访问手机相册，替换群图片
     *
     * @param resultCode
     * @param data
     */
    @OnActivityResult(EXCHANGEALBUM)
    void exchangeAlbum(int resultCode, Intent data) {
        if (resultCode == 1) {
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra("images");
            exchangePhotoPost(list.get(0), imgid);
        }
    }

    /**
     * 访问手机相机拍照，替换群图片
     *
     * @param resultCode
     * @param data
     */
    @OnActivityResult(EXCHANGECAMERA)
    void exchangeCamera(int resultCode, Intent data) {
        if (picPath != null) {
            File fi = new File(picPath);
            if (fi != null && fi.exists()) {
                ImageUtil.downsize(picPath, picPath, getActivity());
                exchangePhotoPost(picPath, imgid);
            }
            fi = null;
        }
    }

    /**
     * 根据群图片的imgid上传图片，替换原来的图片
     *
     * @param url
     * @param id
     */
    void exchangePhotoPost(String url, int id) {

        RequestParams rp = getAjaxParams();
        try {
            rp.put("file", new File(url));
            if (id != 0) {
                rp.put("imgid", id);
            }
            rp.put("groupId", groupId);
            int index=this.listSource.size()-1;
            for (ImageBean bean : this.listSource) {
                if (bean.imgid == imgid) {
                    index=this.listSource.indexOf(bean);
                    bean.imgpath=Util.FILE + url;
                }
            }
            initSource();
            final int finalIndex = index;
            ac.finalHttp.post(URL.UPLOAD_GROUP_ABLM, rp, new MyJsonHttpResponseHandler(getActivity(),Util.progress_arr[4]) {

                RoundProgressBar rpb = null;

                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    getActivity().setResult(Activity.RESULT_OK);
                    if (rpb != null)
                        rpb.setVisibility(View.GONE);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    int progress = (int) (((float) bytesWritten / (float) totalSize) * 100);
                    if (progress > 0 && progress < 100) {
                        if (rpb == null) {
                            try {
                                int viewposition = finalIndex;
                                View pagerView = viewpager.getChildAt(viewposition > maxLine * 4 - 1 ? 1 : 0);
                                if (pagerView instanceof NoScrollGridView) {
                                    Object obj = ((NoScrollGridView) pagerView).getChildAt(viewposition);
                                    if (obj != null) {
                                        rpb = (RoundProgressBar) ((View) obj).findViewById(R.id.loadProgressBar);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            rpb.setVisibility(View.VISIBLE);
                            rpb.setProgress((int) (((float) bytesWritten / (float) totalSize) * 100));
                        }
                    }
                }

                @Override
                public void onFailRetCode(JSONObject jo) {
                    super.onFailRetCode(jo);
                    if (rpb != null)
                        rpb.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Camera) {
            if (picPath != null) {
                File fi = new File(picPath);
                if (fi != null && fi.exists()) {
                    ImageUtil.downsize(picPath, picPath, getActivity());
                    List<String> strs = new ArrayList<String>();
                    strs.add(picPath);
                    uploadImgByList(strs, 0);
                }
                fi = null;
            }
        } else if (requestCode == Album) {
            if (data == null) {
                return;
            }
            final ArrayList<String> list = data.getStringArrayListExtra("images");
            progressDialog = Util.progress(getActivity(), "正在处理");
            replaceList(list);
        }
    }
}
