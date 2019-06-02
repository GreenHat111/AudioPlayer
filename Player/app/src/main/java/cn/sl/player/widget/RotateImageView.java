package cn.sl.player.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import cn.sl.player.service.AudioPlayer;
import cn.sl.player.service.OnAudioEventListener;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;

public class RotateImageView extends android.support.v7.widget.AppCompatImageView implements OnAudioEventListener {
    public static final int STATE_PLAYING = 0;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_STOP = 2;
    private int state = STATE_STOP;

    private ObjectAnimator objectAnimator;

    public RotateImageView(Context context) {
        this(context, null);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AudioPlayer.get().addAudioEventListener(this);
        initAnimator();
        initImage();
    }

    private void initImage() {
        Music music = AudioPlayer.get().getMusic();
        this.setImageBitmap(LoadMedia.createCircleImage(LoadMedia.getMusicBitmap(getContext(),music.getAlbumId(),music.getSongId())));
    }

    private void initAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(this,"rotation",0f,360f);
        objectAnimator.setDuration(10000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);

    }

    public void start(){
        if(state == STATE_PLAYING) return;
        objectAnimator.start();
        state = STATE_PLAYING;
    }

    public void resume(){
        if(state == STATE_PLAYING || state == STATE_STOP) return;
        objectAnimator.resume();
        state = STATE_PLAYING;
    }

    public void pause(){
        if(state == STATE_STOP || state == STATE_PAUSE) return;
        state = STATE_PAUSE;
        objectAnimator.pause();
    }

    public boolean isPlaying(){
        return state == STATE_PLAYING;
    }
    public boolean isPause(){
        return state == STATE_PAUSE;
    }

    public boolean isStop(){
        return state == STATE_STOP;
    }

    public void stop(){
        if(state == STATE_STOP) return;
        state = STATE_STOP;
        objectAnimator.end();
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onPlayerPause() {
        pause();
    }

    @Override
    public void onPlayerStart() {
        if(isPause()){
            resume();
        }else if (isStop()){
            start();
        }
    }

    @Override
    public void onChange(Music music) {
        this.setImageBitmap(LoadMedia.createCircleImage(LoadMedia.getMusicBitmap(getContext(),music.getAlbumId(),music.getSongId())));
        stop();
    }

    @Override
    public void onCycleChange(int cycleMode) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AudioPlayer.get().removeAudioEventListener(this);
    }
}
