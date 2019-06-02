package cn.sl.player.util;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.webkit.ValueCallback;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader implements LoaderManager.LoaderCallbacks{

    private final List<Music> musicList;
    private final Context context;
    private final ValueCallback<List<Music>> callback;

    public MusicLoader(Context context, ValueCallback<List<Music>> callback) {
        this.musicList = new ArrayList<>();
        this.context = context;
        this.callback = callback;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new MusicCursorLoader(context);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor) data;
        if(data == null){
            return;
        }

        int counter = 0;
        musicList.clear();
        while (cursor.moveToNext()) {
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));


            Music music = new Music();
            music.setId(counter);
            music.setSongId(id);
            music.setTitle(title);
            music.setAuthor(author);
            music.setAlbumId(albumId);
            music.setDuration((int) duration);
            music.setPath(path);
            music.setLrcPath(LoadMedia.getLrcPath(music.getPath()));

//            if (++counter <= 20) {
//                // 只加载前20首的缩略图
//                CoverLoader.get().loadThumb(music);
//            }
            musicList.add(music);
            counter++;
        }

        callback.onReceiveValue(musicList);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }



}

