package top.geek_studio.chenlongcould.musicplayer.model;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public abstract class AbsCustomPlaylist extends Playlist {
    public AbsCustomPlaylist(int id, String name) {
        super(id, name, 0L);
    }

    public AbsCustomPlaylist() {
    }

    public AbsCustomPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    public abstract List<Song> getSongs(Context context);
}
