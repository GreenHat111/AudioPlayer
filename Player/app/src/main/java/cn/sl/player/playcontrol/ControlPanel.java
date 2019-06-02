package cn.sl.player.playcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sl.player.R;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.service.OnAudioEventListener;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;

public class ControlPanel implements OnAudioEventListener, View.OnClickListener {

    private ImageView ivPlayBarCover;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarAuthor;
    private ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private ImageView ivPlayBarPlayList;
    private View view;
    public ControlPanel(View view){
        this.view = view;
        initViews(view);
        setListeners();
        initData();
    }

    private void initData() {
        if(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()){
            Music music = AudioPlayer.get().getMusic();
            setDefaultCover(view.getContext(),music.getAlbumId(),music.getSongId());
        }else{
            Bitmap defaultCover = BitmapFactory.decodeResource(view.getResources(),R.mipmap.default_cover);
            ivPlayBarCover.setImageBitmap(LoadMedia.createCircleImage(defaultCover));
        }

    }




    private void setListeners() {
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
    }

    private void initViews(View view) {
        ivPlayBarCover = view.findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle = view.findViewById(R.id.tv_play_bar_title);
        tvPlayBarAuthor = view.findViewById(R.id.tv_play_bar_artist);
        ivPlayBarNext = view.findViewById(R.id.iv_play_bar_next);
        ivPlayBarPlay = view.findViewById(R.id.iv_play_bar_play);
        ivPlayBarPlayList = view.findViewById(R.id.iv_play_bar_playlist);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_bar_play:
                AudioPlayer.get().PlayPause();
                break;
            case R.id.iv_play_bar_next:
                AudioPlayer.get().next();
                break;
        }
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
        tvPlayBarTitle.setSelected(false);
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
        tvPlayBarTitle.setSelected(true);
    }

    @Override
    public void onChange(Music music) {
        if(music == null) return;
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarTitle.setSelected(true);
        tvPlayBarAuthor.setText(music.getAuthor());
//        ivPlayBarCover.setImageBitmap(music.getAlbumBitmap());
        setDefaultCover(view.getContext(),music.getAlbumId(),music.getSongId());
    }

    @Override
    public void onCycleChange(int cycleMode) {

    }

    private void setDefaultCover(Context context,long albumId,long songId){
        ivPlayBarCover.setImageBitmap(LoadMedia.createCircleImage(LoadMedia.getMusicBitmap(context,albumId,songId)));
    }

}
