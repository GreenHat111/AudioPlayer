package cn.sl.player.util;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Music implements Serializable {
    private int id;
    private String title;
    private String author;
    private String path;
    private String lrcPath;     //歌词文件
    private int duration;
    private long albumId;
    private long songId;
    private boolean show;


    public Music() {
        this.show = false;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getSongId() {
        return songId;
    }

    public String getLrcPath() {
        if(lrcPath == null) return null;
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        if(lrcPath == null) this.lrcPath = null;
        else
            this.lrcPath = lrcPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPath() {
        return path;
    }


    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", path='" + path + '\'' +
                ", lrcPath='" + lrcPath + '\'' +
                ", duration=" + duration +
                ", albumId=" + albumId +
                ", songId=" + songId +
                '}';
    }
}
