package top.geek_studio.chenlongcould.musicplayer.model;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

/**
 * ViewModel
 *
 * @author : chenlongcould
 * @date : 2019/09/05/15
 */
public class DataViewModel extends ViewModel {

    /**
     * 对话框(临时) 集合
     */
    public List<DialogInterface> dialogs = new LinkedList<>();

    /**
     * SongsData
     * */
    private MutableLiveData<List<Song>> songsData = new MutableLiveData<>();

    private MutableLiveData<List<Album>> albumsData = new MutableLiveData<>();

    private MutableLiveData<List<Playlist>> playlistsData = new MutableLiveData<>();

    private MutableLiveData<List<Artist>> artistsData = new MutableLiveData<>();

    //////////////////////// songs /////////////////////////

    public void putSongs(@NonNull List<Song> songs) {
        songsData.postValue(songs);
    }

    @NonNull
    public MutableLiveData<List<Song>> getSongData() {
        return songsData;
    }

    public void setSongsUpdateObs(AppCompatActivity activity, DataUpdateCallback<Song> callback) {
        songsData.observe(activity, callback::onUpdate);
    }

    //////////////////////// songs /////////////////////////

    //////////////////////// album /////////////////////////

    public void putAlbums(@NonNull List<Album> data) {
        albumsData.postValue(data);
    }

    @NonNull
    public MutableLiveData<List<Album>> getAlbumsData() {
        return albumsData;
    }

    public void setAlbumsUpdateObs(AppCompatActivity activity, DataUpdateCallback<Album> callback) {
        albumsData.observe(activity, callback::onUpdate);
    }

    //////////////////////// album /////////////////////////

    //////////////////////// artist /////////////////////////

    public void putArtists(@NonNull List<Artist> data) {
        artistsData.postValue(data);
    }

    @NonNull
    public MutableLiveData<List<Artist>> getArtistsData() {
        return artistsData;
    }

    public void setArtistsUpdateObs(AppCompatActivity activity, DataUpdateCallback<Artist> callback) {
        artistsData.observe(activity, callback::onUpdate);
    }

    //////////////////////// artist /////////////////////////

    //////////////////////// playlist /////////////////////////

    public void putPlaylists(@NonNull List<Playlist> data) {
        playlistsData.postValue(data);
    }

    @NonNull
    public MutableLiveData<List<Playlist>> getPlaylistData() {
        return playlistsData;
    }

    public void setPlaylistsUpdateObs(AppCompatActivity activity, DataUpdateCallback<Playlist> callback) {
        playlistsData.observe(activity, callback::onUpdate);
    }

    //////////////////////// playlist /////////////////////////

    public interface DataUpdateCallback <T> {
        void onUpdate(List<T> data);
    }
}