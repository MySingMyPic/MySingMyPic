package com.ylsg365.pai.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.EmojiconRecentsManager;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.People;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseFragmentDialog;

import java.util.Arrays;
import java.util.List;


/**
 * 选择分享项目dialog
 * @author object1984
 */
public class ChatFragment extends BaseFragmentDialog implements OnClickListener, EmojiconRecents {
    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = -1;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;
    private boolean mUseSystemDefault = false;

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";
    private Context activity;
    private TextView exit;
    private ImageView myfrends;
    private ImageView sina;
    private ImageView wechat;
    private OnShareClickListener clickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_chat, container, false);


        final ViewPager emojisPager = (ViewPager) rootView.findViewById(R.id.emojis_pager);
        // we handle recents
        EmojiconRecents recents = this;
        mEmojisAdapter = new EmojisPagerAdapter(getFragmentManager(), Arrays.asList(
//                EmojiconRecentsGridFragment.newInstance(mUseSystemDefault),
                EmojiconGridFragment.newInstance(People.DATA, recents, mUseSystemDefault)
        ));
        emojisPager.setAdapter(mEmojisAdapter);

        emojisPager.setCurrentItem(0);

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(rootView.getContext());
        int page = mRecentsManager.getRecentPage();

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if (clickListener != null) {

            switch (v.getId()) {
                case R.id.exit:

                    break;
                case R.id.myfrends:
                    clickListener.onYinClick();
                    break;
                case R.id.sina:
                    clickListener.onSinaClick();

                    break;
                case R.id.wechat:
                    clickListener.onWechatClick();
                    break;

            }
        }
        dismiss();
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {

    }

    public interface OnShareClickListener {

        public void onWechatClick();  //微信

        public void onSinaClick();     //新浪

        public void onYinClick();      //我的好友

    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        clickListener = listener;
    }


    private static class EmojisPagerAdapter extends FragmentStatePagerAdapter {
        private List<EmojiconGridFragment> fragments;

        public EmojisPagerAdapter(FragmentManager fm, List<EmojiconGridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}

