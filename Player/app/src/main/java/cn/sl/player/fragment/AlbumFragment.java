package cn.sl.player.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.adapter.CardPagerAdapter;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;
import cn.sl.player.util.MusicLoader;

public class AlbumFragment extends Fragment {

    private ViewPager viewPager;
    private CardPagerAdapter cardPagerAdapter;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews(getView());
//        loadMusic();
        initData();
    }

    private void loadMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    List<Music> musicList = AudioPlayer.get().getMusicList();
                    if(!musicList.isEmpty()) {
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.album_view_pager);
        textView = view.findViewById(R.id.album_txt);
    }

    private void initData() {
        cardPagerAdapter = new CardPagerAdapter(LoadMedia.getMusic(getContext()));
        viewPager.setAdapter(cardPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
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
                    int a = viewPager.getCurrentItem();
                    textView.setText(String.valueOf(a + 1));
                }
            }
        });
    }
}
