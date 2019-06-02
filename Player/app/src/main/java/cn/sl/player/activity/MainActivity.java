package cn.sl.player.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.adapter.FragmentAdapter;
import cn.sl.player.fragment.AlbumFragment;
import cn.sl.player.fragment.MusicFragment;
import cn.sl.player.fragment.PlayFragment;
import cn.sl.player.playcontrol.ControlPanel;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.service.AudioService;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AudioService audioService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            audioService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
        }
    };

    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private MusicFragment musicFragment;
    private AlbumFragment albumFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    private FrameLayout playLyt;        //底部播放布局
    private NavigationView navView;
    private ImageView icMenu;
    private TextView tvLocalMusic,tvHistoryMusic;

    private ControlPanel controlPanel;

    private boolean isFragmentShow = false;
    private PlayFragment playFragment;

    // Nav
    private ImageView navMusicAlbum;
    private TextView navMusicTitle;
    private TextView navMusicAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
        setListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService();
    }

    private void setListeners() {
        tvLocalMusic.setOnClickListener(this);
        tvHistoryMusic.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tvLocalMusic.setSelected(true);
                    tvHistoryMusic.setSelected(false);
                } else {
                    tvLocalMusic.setSelected(false);
                    tvHistoryMusic.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        icMenu.setOnClickListener(this);
        playLyt.setOnClickListener(this);

    }

    private void initData() {
        isFragmentShow = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);

        musicFragment = new MusicFragment();
        albumFragment = new AlbumFragment();
        fragmentList.add(musicFragment);
        fragmentList.add(albumFragment);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        tvLocalMusic.setSelected(true);
        tvHistoryMusic.setSelected(false);

        controlPanel = new ControlPanel(playLyt);
        AudioPlayer.get().addAudioEventListener(controlPanel);

    }

    private void initViews() {
        drawerLayout =  findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.view_pager);
        navView = findViewById(R.id.nav_view);
        playLyt = findViewById(R.id.fl_play_bar);
        tvHistoryMusic = findViewById(R.id.tv_history_music);
        tvLocalMusic = findViewById(R.id.tv_local_music);
        icMenu = findViewById(R.id.ic_menu);

        View navHeaderView = navView.getHeaderView(0);
        navMusicAlbum = navHeaderView.findViewById(R.id.nav_music_album);
        navMusicTitle = navHeaderView.findViewById(R.id.nav_music_title);
        navMusicAuthor = navHeaderView.findViewById(R.id.nav_music_author);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_local_music:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_history_music:
                viewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showFragment();
                break;
            case R.id.ic_menu:
                if(isFragmentShow) return;

                drawerLayout.openDrawer(Gravity.START);
                if(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()){
                    Music music = AudioPlayer.get().getMusic();
                    navMusicAlbum.setImageBitmap(LoadMedia.createCircleImage(LoadMedia.getMusicBitmap(getApplicationContext(),
                            music.getAlbumId(),music.getSongId())));
                    navMusicAuthor.setText(music.getAuthor());
                    navMusicTitle.setText(music.getTitle());
                }
                break;
        }
    }

    private void showFragment() {
        if(isFragmentShow) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_up,0);
        if(playFragment == null){
            playFragment = new PlayFragment();
            fragmentTransaction.replace(R.id.play_fragment,playFragment);
        }else{
            fragmentTransaction.show(playFragment);
        }
        isFragmentShow = true;
        fragmentTransaction.commit();

    }

    private void hideFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0,R.anim.fragment_slide_down);
        ft.hide(playFragment);
        ft.commitAllowingStateLoss();
        isFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if(playFragment != null && isFragmentShow){
//            drawerLayout.closeDrawer(Gravity.START);
            hideFragment();
            isFragmentShow = false;
            return;
        }
        super.onBackPressed();
    }

    private void bindService(){
        bindService(new Intent(this,AudioService.class),serviceConnection, Service.BIND_AUTO_CREATE);
    }



    //防止返回键 直接将activity销毁 使用这种方法 按下返回键后 会将activity放在后台
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果页面在playFragment中 返回键跳转到 主界面
        if(isFragmentShow && playFragment != null && keyCode == KeyEvent.KEYCODE_BACK){
            onBackPressed();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().removeAudioEventListener(controlPanel);
        unbindService(serviceConnection);
    }



}
