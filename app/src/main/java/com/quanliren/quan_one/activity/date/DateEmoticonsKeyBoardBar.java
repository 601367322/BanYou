package com.quanliren.quan_one.activity.date;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean;
import com.quanliren.quan_one.custom.emoji.AutoHeightLayout;
import com.quanliren.quan_one.custom.emoji.EmoteGridView;
import com.quanliren.quan_one.custom.emoji.EmoteView;
import com.quanliren.quan_one.custom.emoji.EmoticonsEditText;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shen on 2015/12/14.
 */

@EViewGroup(R.layout.date_detail_bottom)
public class DateEmoticonsKeyBoardBar extends AutoHeightLayout implements View.OnFocusChangeListener, TextView.OnEditorActionListener, EmoteGridView.EmoticonListener {

    @ViewById
    EmoticonsEditText et_chat;
    @ViewById
    LinearLayout editLl;
    @ViewById
    Button sendBtn;
    @FragmentById
    EmoteView chatEivInputview;
    @ViewById
    RelativeLayout chatLayoutEmote;
    @ViewById
    ImageView chatFaceBtn;

    private AtomicBoolean alreadyInflated = new AtomicBoolean(false);

    public DateEmoticonsKeyBoardBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        if (!alreadyInflated.get()) {
            alreadyInflated.compareAndSet(false, true);


            setAutoHeightLayoutView(chatLayoutEmote);

            et_chat.setOnFocusChangeListener(this);

            et_chat.setOnEditorActionListener(this);

            et_chat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    emojiShow.compareAndSet(true, false);
                }
            });

            et_chat.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!et_chat.isFocused()) {
                        et_chat.setFocusable(true);
                        et_chat.setFocusableInTouchMode(true);
                    }
                    return false;
                }
            });
            et_chat.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        setEditableState(true);
                    } else {
                        setEditableState(false);
                    }
                }
            });

            chatEivInputview.setEditText(et_chat);

            chatEivInputview.setListener(this);

            editLl.setSelected(true);

        }
    }

    private void setEditableState(boolean b) {
        if (b) {
            et_chat.setFocusable(true);
            et_chat.setFocusableInTouchMode(true);
            et_chat.requestFocus();
        } else {
            et_chat.setFocusable(false);
            et_chat.setFocusableInTouchMode(false);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (chatLayoutEmote != null && chatLayoutEmote.isShown() && emojiShow.get()) {
                    hideAutoView();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void hideAutoView() {
        super.hideAutoView();
        chatFaceBtn.setImageResource(R.drawable.face_btn_selector);
    }

    public void hideEmotion() {
        super.emotionClose();
        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().hide(chatEivInputview).commitAllowingStateLoss();
    }

    public void openEmotion() {
        super.emotionOpen();
        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().show(chatEivInputview).commitAllowingStateLoss();
    }

    @Click({R.id.chat_face_btn, R.id.send_btn})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.chat_face_btn) {
            switch (mKeyboardState) {
                case KEYBOARD_STATE_NONE:
                case KEYBOARD_STATE_BOTH:
                    emotionOpen();
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    chatFaceBtn.setImageResource(R.drawable.chat_borad_btn);
                    openEmotion();
                    break;
                case KEYBOARD_STATE_FUNC:
                    emotionClose();
                    editRequestFocus();
                    Utils.openSoftKeyboard(mContext, et_chat);
                    chatFaceBtn.setImageResource(R.drawable.face_btn_selector);
                    hideEmotion();
                    break;
            }
        } else if (id == R.id.send_btn) {
            if (isFastDoubleClick()) {
                Util.toast(mContext, "太快了，休息一下~");
                return;
            }
            String t = et_chat.getText().toString().trim();
            if (!TextUtils.isEmpty(t)) {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnSendBtnClick(t);
                }
            } else {
                Util.toast(mContext, "请输入内容");
            }
            sendBtnClick();
        }
    }


    public void sendBtnClick() {
        et_chat.setText("");
    }

    public void editRequestFocus() {
        et_chat.setFocusable(true);
        et_chat.setFocusableInTouchMode(true);
        et_chat.requestFocus();
        editLl.setSelected(true);
    }


    @Override
    public void OnSoftPop(final int height) {
        super.OnSoftPop(height);
        post(new Runnable() {
            @Override
            public void run() {
                emotionClose();
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
                }
                chatFaceBtn.setImageResource(R.drawable.face_btn_selector);
                    hideEmotion();
            }
        });
    }


    @Override
    public void OnSoftClose(final int height) {
        super.OnSoftClose(height);
        post(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
                }
                if (!emojiShow.get()) {
                    setAutoViewHeight(0);
                    mKeyboardState = KEYBOARD_STATE_NONE;
                }
            }
        });
    }

    @Override
    public void OnSoftChanegHeight(final int height) {
        super.OnSoftChanegHeight(height);
        post(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
                }
            }
        });
    }

    KeyBoardBarViewListener mKeyBoardBarViewListener;

    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) {
        this.mKeyBoardBarViewListener = l;
    }

    public interface KeyBoardBarViewListener {

        public void OnKeyBoardStateChange(int state, int height);

        public void OnSendBtnClick(String msg);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendBtn.performClick();
            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            chatLayoutEmote.setVisibility(View.GONE);
        }
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onEmoticonClick(EmoticonActivityListBean.EmoticonZip.EmoticonImageBean bean) {
        if (isFastDoubleClick()) {
            Util.toast(mContext, "太快了，休息一下~");
            return;
        }
    }

    @Override
    public void onEmoticonLongPress(EmoticonActivityListBean.EmoticonZip.EmoticonImageBean bean, int[] xy, int[] wh) {

    }

    @Override
    public void onEmoticonLongPressCancle() {

    }
}
