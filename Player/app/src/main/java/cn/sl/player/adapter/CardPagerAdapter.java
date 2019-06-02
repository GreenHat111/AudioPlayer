package cn.sl.player.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.service.AudioPlayer;
import cn.sl.player.util.LoadMedia;
import cn.sl.player.util.Music;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter{

    private List<CardView> cardViews;
    private List<Music> muscis;
    private float mBaseElevation;

    public CardPagerAdapter(List<Music> music){
        cardViews = new ArrayList<>();
        muscis = new ArrayList<>();
        muscis.addAll(music);
        for(Music m:muscis){
            cardViews.add(null);
        }
    }



    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return cardViews.get(position);
    }

    @Override
    public int getCount() {
        return muscis.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_album_view,container,false);
        container.addView(view);
        bind(muscis.get(position),view,position);

        CardView cardView = view.findViewById(R.id.album_card_view);
        if(mBaseElevation == 0){
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        cardViews.set(position, cardView);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        cardViews.set(position, null);
    }

    private void bind(Music music, View view, final int position){
        ImageView albumImg = view.findViewById(R.id.album_img);
        TextView albumTitle = view.findViewById(R.id.album_title);
        TextView albumAuthor = view.findViewById(R.id.album_author);
        FrameLayout albumLyt = view.findViewById(R.id.album_lyt);

        albumImg.setImageBitmap(LoadMedia.getMusicBitmap(view.getContext(),music.getAlbumId(),music.getSongId()));
        albumTitle.setText(music.getTitle());
        albumAuthor.setText(music.getAuthor());

        albumLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayer.get().play(position);
            }
        });
    }

}
