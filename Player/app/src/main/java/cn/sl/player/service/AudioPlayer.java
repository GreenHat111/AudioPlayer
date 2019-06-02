package cn.sl.player.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;

public class AudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final int CYCLE_LIST = 1;
    public static final int CYCLE_RANDOM = 2;
    public static final int CYCLE_SINGLE = 3;
    private int cycle_state = CYCLE_LIST;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING= 2;
    private static final int STATE_PAUSE = 3;
    private int state = STATE_IDLE;
    private int curIndex = 0;
    private Handler handler = null;     //发送进度条

    private static final long TIME_UPDATE = 300L;
    private List<Music> musicList = new ArrayList<>();
    //每个需要 回调函数的 集合
    private final List<OnAudioEventListener> listeners = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    public static AudioPlayer get(){
        return AudioClass.audioPlayer;
    }

    public static class AudioClass{
        private static AudioPlayer audioPlayer = new AudioPlayer();
    }

    public void setMusicList(List<Music> musicList){
        if(this.musicList == null)
            this.musicList = musicList;
        else{
            this.musicList.clear();
            this.musicList = musicList;
        }
        System.out.println("成功设置音乐");
    }
    public void init(Context context){
//        musicList = LoadMedia.getMusic(context);

        curIndex = 0;
        handler = new Handler(Looper.getMainLooper());
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }



    public void addAudioEventListener(OnAudioEventListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeAudioEventListener(OnAudioEventListener listener){
        listeners.remove(listener);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(isPreparing()){
            startPlayer();
        }

    }

    public void PlayPause(){
        if(isPlaying()){
            pausePlayer();
        }else if(isPausing()){
            startPlayer();
        }else if(isIdle()){
            play(curIndex);
        }else if(isPreparing()){
            stopPlayer();
        }
    }

    private void startPlayer(){
        mediaPlayer.start();
        state = STATE_PLAYING;
        handler.post(mPublishRunnable);
        for (OnAudioEventListener listener:listeners){
            listener.onPlayerStart();
        }
    }

    public void stopPlayer(){
        if(isIdle()) return;
        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if(isPlaying()){
                for(OnAudioEventListener listener:listeners){
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
                handler.postDelayed(this,TIME_UPDATE);
            }
        }
    };

    private void pausePlayer(){
        if(!isPlaying()) return;
        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        for (OnAudioEventListener listener:listeners){
            listener.onPlayerPause();
        }
    }

    public void changeCycleMode(){
        if(cycle_state < CYCLE_SINGLE){
            cycle_state++;
        }else{
            cycle_state = CYCLE_LIST;
        }
        for(OnAudioEventListener listener:listeners){
            listener.onCycleChange(cycle_state);
        }
    }

    public int getCycleMode(){
        return cycle_state;
    }

    public void next(){
        if(musicList.isEmpty()) return;
        if(isPlaying()){
            pausePlayer();
            state = STATE_PAUSE;
        }
        switch (cycle_state){
            case CYCLE_LIST:
                play(curIndex + 1);
                break;
            case CYCLE_RANDOM:
                play((int)Math.abs(Math.random()*(musicList.size())));
                break;
            case CYCLE_SINGLE:
                play(curIndex);
                break;
        }

    }

    public void previous(){
        if(musicList.isEmpty()) return;
        if(isPlaying()){
            pausePlayer();
            state = STATE_PAUSE;
        }
        switch (cycle_state){
            case CYCLE_LIST:
                play(curIndex - 1);
                break;
            case CYCLE_RANDOM:
                play((int)Math.abs(Math.random()*(musicList.size())));
                break;
            case CYCLE_SINGLE:
                play(curIndex);
                break;
        }
    }

    public void seekTo(int msec){
        if(isPlaying() || isPausing()){
            mediaPlayer.seekTo(msec);
            for(OnAudioEventListener listener : listeners){
                listener.onPublish(msec);
            }
        }
    }

    public void play(int position) {
        if(musicList.isEmpty()) return;
        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }
        curIndex = position;
        Music music = musicList.get(curIndex);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepare();
            state = STATE_PREPARING;
            for(OnAudioEventListener listener:listeners){
                listener.onChange(music);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isPlaying(){
        return state == STATE_PLAYING;
    }

    public boolean isPausing(){
        return state == STATE_PAUSE;
    }

    public boolean isIdle(){
        return state == STATE_IDLE;
    }

    public boolean isPreparing(){
        return state == STATE_PREPARING;
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public Music getMusic(){
        if(musicList.isEmpty()) return null;
        return musicList.get(curIndex);
    }

    public int getMusicListSize(){
        if(musicList.isEmpty()) return 0;
        return musicList.size();
    }

    public List<Music> getMusicList(){
        System.out.println("读取音乐");
        return musicList;
    }
    public int getCurIndex(){
        return curIndex;
    }
    public Music getPlayMusic(){
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(curIndex);
    }

    public int getPlayDuration(){
        if(musicList.isEmpty()) return 0;
        if(isPlaying() || isPausing()) return mediaPlayer.getDuration();
        return musicList.get(curIndex).getDuration();
    }

    public int getCurPosition(){
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }
}
