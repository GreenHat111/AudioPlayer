package cn.sl.player.service;

import cn.sl.player.util.Music;

public interface OnAudioEventListener {
    //进度条改变
    void onPublish(int progress);
    //音乐暂停
    void onPlayerPause();
    //音乐播放
    void onPlayerStart();
    //音乐改变
    void onChange(Music music);

    //音乐播放模式
    void onCycleChange(int cycleMode);
}
