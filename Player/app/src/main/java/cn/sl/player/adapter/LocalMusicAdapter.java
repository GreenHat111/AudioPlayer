package cn.sl.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;
import cn.sl.player.util.Music;

public class LocalMusicAdapter extends BaseAdapter {

    private List<Music> musicList = new ArrayList<>();

    public LocalMusicAdapter(List<Music> music){
        this.musicList = music;
    }

    public LocalMusicAdapter(){

    }

    @Override
    public int getCount() {
        if(musicList.isEmpty()) return 0;
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Music music = (Music) getItem(position);
        if(music.isShow()){
            viewHolder.playTitle.setSelected(true);
            viewHolder.playHorn.setVisibility(View.VISIBLE);
        }else{
            viewHolder.playTitle.setSelected(false);
            viewHolder.playHorn.setVisibility(View.GONE);
        }

        viewHolder.playAuthor.setText(music.getAuthor());
        viewHolder.playTitle.setText(music.getTitle());

        return convertView;
    }



    public class ViewHolder{
        ImageView playHorn,playMore;
        TextView playTitle,playAuthor;
        public ViewHolder(View view){
            playAuthor = view.findViewById(R.id.tv_play_author);
            playTitle = view.findViewById(R.id.tv_play_title);
            playHorn = view.findViewById(R.id.ic_play_horn);
            playMore = view.findViewById(R.id.ic_play_more);
        }

    }


    public void updateView(int position, ListView mListView) {
        cancelAllView(mListView);
        int firstvisible = mListView.getFirstVisiblePosition();
        int lastvisibale = mListView.getLastVisiblePosition();
        if(position>=firstvisible && position<=lastvisibale){
            View view = mListView.getChildAt(position - firstvisible);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            //然后使用viewholder去更新需要更新的view。
            viewHolder.playTitle.setSelected(true);
            viewHolder.playHorn.setVisibility(View.VISIBLE);
            System.out.println("1");
        }
    }



    public void cancelAllView(ListView listView){

        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        for(int i=visibleFirstPosi;i<=visibleLastPosi;i++) {
            View view = listView.getChildAt(i - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.playTitle.setSelected(false);
            holder.playHorn.setVisibility(View.GONE);
        }
    }

    public void cancelView(int position, ListView listView){
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.playTitle.setSelected(false);
            holder.playHorn.setVisibility(View.GONE);
        }
    }
}
