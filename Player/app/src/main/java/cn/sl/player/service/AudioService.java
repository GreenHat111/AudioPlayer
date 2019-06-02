package cn.sl.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import cn.sl.player.util.LoadMedia;

public class AudioService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder{
        public AudioService getService(){
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AudioPlayer.get().init(this);
//        AudioPlayer.get().setMusicList(LoadMedia.getMusic(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().stopPlayer();
        AudioPlayer.get().getMediaPlayer().release();
    }
}
