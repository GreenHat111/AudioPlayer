package cn.sl.player.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.sl.player.R;

public class LoadMedia {
    private static final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    public static List<Music> getMusic(Context context){

        List<Music> musicInfos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);

        int i=0;
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            Music mi = new Music();
            mi.setId(i);
            mi.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            System.out.println("ALBUM: "+cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            mi.setAuthor(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            mi.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            mi.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            mi.setLrcPath(getLrcPath(mi.getPath()));
            mi.setAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            mi.setSongId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            mi.setLrcPath(getLrcPath(mi.getPath()));
            //System.out.println(mi.getAuthor()+" "+mi.getTitle()+" "+mi.getDuration() + " " + mi.getLrcPath());
            musicInfos.add(mi);
            i++;
        }
        cursor.close();
        return musicInfos;
    }

    public static String getLrcPath(String path){
        String newPath = path.replace("mp3","lrc");
//        System.out.println(newPath);
        File file = new File(newPath);
        if(file.exists()){
            return file.getAbsolutePath();
        }else{
            return null;
        }
    }

    public static Bitmap getMusicBitmap(Context context,long albumId,long songId){
        Bitmap bitmap = null;

        if(albumId < 0 && songId < 0){
//            throw new IllegalArgumentException("没有这首歌");
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
        }

        if(albumId < 0) {
            Uri uri = Uri.parse("content://media/external/audio/media/"
                    + songId + "/albumart");
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fd);
            }else{
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
            }
        }else {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
            }
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fd);
            } else {
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
            }
        }
        return bitmap;
    }

    /**
     * 将图片剪裁为圆形
     */
    public static Bitmap createCircleImage(Bitmap source) {
        if (source == null) {
            return null;
        }
        int length = Math.min(source.getWidth(), source.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

}
