package cn.sl.player.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.adapter.FragmentAdapter;
import cn.sl.player.adapter.PlayAdapter;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.service.OnAudioEventListener;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;
import cn.sl.player.util.TimeUtil;
import cn.sl.player.widget.LrcView;
import cn.sl.player.widget.RotateImageView;

public class PlayFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        OnAudioEventListener, View.OnClickListener {

    private static final String TAG = PlayFragment.class.getSimpleName();
    private TextView tvTitle,tvAuthor;
    private SeekBar seekBar;
    private TextView tvCurrentTime,tvTotalTime;
    private ImageView ivPlay,ivNext,ivPre,ivBack,ivMode;
    private RotateImageView rotateImageView;
    private LrcView lrcView;
    private ViewPager viewPager;
    private List<View> viewList = new ArrayList<>();
    private PlayAdapter playAdapter;
    private ImageView circle1,circle2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AudioPlayer.get().addAudioEventListener(this);
        View view = inflater.inflate(R.layout.play_fragment,container,false);
        initViews(view);
        initData();
        initView();
        setListeners();
        return view;
    }

    private void initView() {
        rotateImageView = new RotateImageView(getContext());
        lrcView = new LrcView(getContext());
        viewList.add(rotateImageView);
        viewList.add(lrcView);
        playAdapter = new PlayAdapter(getContext(), viewList);
        viewPager.setAdapter(playAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if(i==2){
                    if (viewPager.getCurrentItem() == 0){
                        circle1.setImageResource(R.mipmap.red_circle);
                        circle2.setImageResource(R.mipmap.white_circle);
                    }else{
                        circle1.setImageResource(R.mipmap.white_circle);
                        circle2.setImageResource(R.mipmap.red_circle);
                    }
                }
            }
        });

    }

    private void initData() {

        if(AudioPlayer.get().isPlaying())    ivPlay.setSelected(true);
        else     ivPlay.setSelected(false);
        int duration = AudioPlayer.get().getPlayDuration();
        seekBar.setMax(duration);
        seekBar.setProgress(AudioPlayer.get().getCurPosition());
        tvTotalTime.setText(TimeUtil.duration2Time(duration));
        Music music = AudioPlayer.get().getMusic();
        if(music != null){
            tvTitle.setText(music.getTitle());
            tvAuthor.setText(music.getAuthor());
//            rotateImage.setImageBitmap(music.getAlbumBitmap());

        }
        setModeImg(AudioPlayer.get().getCycleMode());
        tvTitle.setSelected(true);

    }


    private void setListeners() {
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPre.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

        rotateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        lrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

    }
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvAuthor = view.findViewById(R.id.tv_artist);
        seekBar = view.findViewById(R.id.seek_bar);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        ivPlay = view.findViewById(R.id.iv_play);
        ivNext = view.findViewById(R.id.iv_next);
        ivPre = view.findViewById(R.id.iv_prev);
        ivBack = view.findViewById(R.id.iv_back);
        ivMode = view.findViewById(R.id.iv_mode);
        viewPager = view.findViewById(R.id.play_view_pager);
        circle1 = view.findViewById(R.id.circle_1);
        circle2 = view.findViewById(R.id.circle_2);
//        rotateImage = view.findViewById(R.id.rotate_img);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            AudioPlayer.get().seekTo(progress);
            tvCurrentTime.setText(TimeUtil.duration2Time(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPublish(int progress) {
//        Log.i("PlayFragment",String.valueOf(progress));
        seekBar.setProgress(progress);
        tvCurrentTime.setText(TimeUtil.duration2Time(progress));
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);

    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);

        tvTitle.setSelected(true);
    }

    @Override
    public void onChange(Music music) {
        if(music == null) return;
        seekBar.setMax(music.getDuration());
        seekBar.setProgress(0);
        tvTitle.setText(music.getTitle());
        tvAuthor.setText(music.getAuthor());
//        rotateImage.setImageBitmap(music.getAlbumBitmap());


    }

    @Override
    public void onCycleChange(int cycleMode) {
       setModeImg(cycleMode);
    }
    private void setModeImg(int cycleMode){
        if(cycleMode == AudioPlayer.CYCLE_LIST){
            ivMode.setImageResource(R.mipmap.ic_order_play);
            Toast.makeText(getContext(),"顺序播放",Toast.LENGTH_SHORT).show();
        }else if(cycleMode == AudioPlayer.CYCLE_RANDOM){
            ivMode.setImageResource(R.mipmap.ic_random_play);
            Toast.makeText(getContext(),"随机播放",Toast.LENGTH_SHORT).show();
        }else if(cycleMode == AudioPlayer.CYCLE_SINGLE){
            ivMode.setImageResource(R.mipmap.ic_single_play);
            Toast.makeText(getContext(),"单曲循环",Toast.LENGTH_SHORT).show();
        }
        ivMode.setMinimumWidth(35);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play:
                AudioPlayer.get().PlayPause();
                tvTotalTime.setText(TimeUtil.duration2Time(AudioPlayer.get().getPlayMusic().getDuration()));
                break;
            case R.id.iv_next:
                AudioPlayer.get().next();
                tvTotalTime.setText(TimeUtil.duration2Time(AudioPlayer.get().getPlayMusic().getDuration()));
                break;
            case R.id.iv_prev:
                AudioPlayer.get().previous();
                tvTotalTime.setText(TimeUtil.duration2Time(AudioPlayer.get().getPlayMusic().getDuration()));
                break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_mode:
                AudioPlayer.get().changeCycleMode();
                break;

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().removeAudioEventListener(this);
    }
}
