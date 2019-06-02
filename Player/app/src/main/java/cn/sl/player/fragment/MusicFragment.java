package cn.sl.player.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.adapter.LocalMusicAdapter;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.service.OnAudioEventListener;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;
import cn.sl.player.util.MusicLoader;

public class MusicFragment extends Fragment implements View.OnClickListener,ListView.OnItemClickListener,
        OnAudioEventListener {

    private LocalMusicAdapter localMusicAdapter;
    private ListView musicList;
    private TextView musicTotal;
    private LinearLayout playLyt;
    private List<Music> musics = new ArrayList<>();
    private TextView scanText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment, container, false);
        AudioPlayer.get().addAudioEventListener(this);
        musicList = view.findViewById(R.id.play_fragment_list);
        playLyt = view.findViewById(R.id.play_fragment_lyt);
        scanText = view.findViewById(R.id.scan_music);
        musicTotal = view.findViewById(R.id.play_fragment_total_string);
        localMusicAdapter = new LocalMusicAdapter(musics);
        musicList.setAdapter(localMusicAdapter);
        musicList.setOnItemClickListener(this);
        playLyt.setOnClickListener(this);
        loadMusic();

        return view;
    }

    private void loadMusic() {
        // 通过异步加载的方式加载数据
        musicList.setVisibility(View.GONE);
        scanText.setVisibility(View.VISIBLE);
        getActivity().getLoaderManager().initLoader(0,null,
                new MusicLoader(getContext(), new ValueCallback<List<Music>>() {
                    @Override
                    public void onReceiveValue(List<Music> value) {
                            musics.clear();
                            musics.addAll(value);
                            musicList.setVisibility(View.VISIBLE);
                            scanText.setVisibility(View.GONE);
                            localMusicAdapter.notifyDataSetChanged();
                            AudioPlayer.get().setMusicList(musics);
                            musicTotal.setText("播放全部(共" + String.valueOf(musics.size()) + "首)");
                    }
                }));
//        musics = LoadMedia.getMusic(getContext());

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(musics != null && localMusicAdapter != null){
            musicList.setAdapter(localMusicAdapter);
        }else{
            musics = AudioPlayer.get().getMusicList();
            localMusicAdapter = new LocalMusicAdapter(musics);
        }
    }

    @Override
    public void onClick(View v) {
        AudioPlayer.get().play(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
//        musicList.setItemChecked(position,true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().removeAudioEventListener(this);
    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPlayerStart() {
        int position = AudioPlayer.get().getCurIndex();
        for(Music music:musics){
            if(position == music.getId()){
                music.setShow(true);
            }else{
                music.setShow(false);
            }
        }
//        localMusicAdapter.updateView(position, musicList);
        localMusicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChange(Music music) {

    }

    @Override
    public void onCycleChange(int cycleMode) {

    }
}
