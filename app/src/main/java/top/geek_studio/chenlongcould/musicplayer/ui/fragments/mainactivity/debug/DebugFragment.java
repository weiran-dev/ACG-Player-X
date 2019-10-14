package top.geek_studio.chenlongcould.musicplayer.ui.fragments.mainactivity.debug;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mmin18.widget.RealtimeBlurView;
import com.kabouzeid.appthemehelper.common.views.ATEPrimaryTextView;
import com.kabouzeid.appthemehelper.common.views.ATESecondaryTextView;
import com.kabouzeid.chenlongcould.musicplayer.R;
import com.lauzy.freedom.library.LrcView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import top.geek_studio.chenlongcould.musicplayer.helper.MusicPlayerRemote;
import top.geek_studio.chenlongcould.musicplayer.interfaces.MusicServiceEventListener;
import top.geek_studio.chenlongcould.musicplayer.interfaces.TransDataCallback;
import top.geek_studio.chenlongcould.musicplayer.model.DataViewModel;
import top.geek_studio.chenlongcould.musicplayer.model.NetSearchSong;
import top.geek_studio.chenlongcould.musicplayer.model.Song;
import top.geek_studio.chenlongcould.musicplayer.model.nmlrc.NmLrc2;
import top.geek_studio.chenlongcould.musicplayer.ui.activities.MainActivity;
import top.geek_studio.chenlongcould.musicplayer.ui.fragments.mainactivity.AbsMainActivityFragment;
import top.geek_studio.chenlongcould.musicplayer.util.MusicUtil;
import top.geek_studio.chenlongcould.musicplayer.util.NetPlayerUtil;
import top.geek_studio.chenlongcould.musicplayer.util.lrc.CustomLrcHelper;

/**
 * HomePage
 *
 * @author : chenlongcould
 * @date : 2019/10/03/10
 */
public class DebugFragment extends AbsMainActivityFragment implements MusicServiceEventListener {

    private static final String TAG = DebugFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.pic)
    AppCompatImageView imageView;

    @BindView(R.id.real_blur)
    RealtimeBlurView realtimeBlurView;

    @BindView(R.id.lrcView)
    LrcView mLrcView;

    @BindView(R.id.title)
    ATEPrimaryTextView title;

    @BindView(R.id.albumTitle)
    ATESecondaryTextView albumTitle;

    @BindView(R.id.albumImage)
    AppCompatImageView albumImage;

    private DataViewModel dataViewModel;

    private View statusBar;

    private Timer mTimer;

    public static DebugFragment newInstance() {
        return new DebugFragment();
    }

    private Handler handler = new Handler();

    @Override
    protected ViewGroup createRootView(@NotNull @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_apple_music, container, false);
        unbinder = ButterKnife.bind(this, view);

        statusBar = getMainActivity().getWindow().getDecorView().getRootView().findViewById(R.id.status_bar);
        statusBar.setVisibility(View.GONE);

        mLrcView.setOnLongClickListener(v -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getMainActivity());
            builder.title("LrcView")
                    .content("Power by https://i.a632079.me.")
////                    .negativeText("Hide LRC")
////                    .onNegative((dialog, which) -> mLrcView.setVisibility(View.INVISIBLE))
////                    .positiveText("Show LRC")
////                    .onPositive((dialog, which) -> mLrcView.setVisibility(View.VISIBLE))
                    .showListener(dialog -> dataViewModel.dialogs.add(dialog))
                    .build();
            builder.show();
            return false;
        });

        title.setTextColor(Color.BLACK);
        albumTitle.setTextColor(Color.BLACK);

        getMainActivity().setLightStatusbar(false);

        // init blur
        realtimeBlurView.setBlurRadius(300);
        realtimeBlurView.setDownsampleFactor(0.1f);

        imageView.setScaleX(2);
        imageView.setScaleY(2);

        // set animation
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 359f);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(17000);
        rotation.start();

        // set update
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (mLrcView != null)
                        mLrcView.updateTime(MusicPlayerRemote.getSongProgressMillis());
                });
            }
        }, 0, 100);

        mLrcView.setOnPlayIndicatorLineListener((time, content) -> {
//                Log.d(TAG, "onPlay: time: " + time + "   getTime: " + MusicPlayerRemote.getPosition() + "   content: " + content);
            if (!MusicPlayerRemote.isPlaying()) MusicPlayerRemote.resumePlaying();
            MusicPlayerRemote.seekTo((int) time);
        });

        updateImage();
        updateLrc();

        return (ViewGroup) view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataViewModel = ViewModelProviders.of(getMainActivity()).get(DataViewModel.class);
        getMainActivity().addMusicServiceEventListener(this);
    }

    /**
     * 更新背景
     */
    private void updateImage() {
        MainActivity activity = getMainActivity();
        if (activity == null) return;

        Song song = MusicPlayerRemote.getCurrentSong();
        if (song.id < 0 || song.albumId < 0) return;

        imageView.setImageURI(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId));
        albumImage.setImageURI(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId));

        title.setText(song.title);
        albumTitle.setText(song.albumName);

//        Glide.with(getMainActivity())
//                .load(MusicUtil.getMediaStoreAlbumCoverUri(MusicPlayerRemote.getCurrentSong().albumId))
//                .crossFade().into(imageView);

//        SongGlideRequest.Builder.from(Glide.with(getActivity()), MusicPlayerRemote.getCurrentSong())
//                .checkIgnoreMediaStore(getMainActivity()).build()
//                .into(imageView);
    }

    private void updateLrc() {
        final Song song = MusicPlayerRemote.getCurrentSong();
        if (song == null || song.id < 0) return;

        NetPlayerUtil.search(getMainActivity(), song.title, new TransDataCallback<NetSearchSong>() {
            @Override
            public void onTrans(@NonNull NetSearchSong data) {
                if (data.getResult() == null || data.getResult().getSongCount() == 0) {
                    onError();
                    return;
                }

                NetPlayerUtil.getLrc(getMainActivity(), (int) data.getResult().getSongs().get(0).getId(), new TransDataCallback<NmLrc2>() {
                    @Override
                    public void onTrans(@NonNull NmLrc2 data) {
                        final List<String> lrcs = new LinkedList<>();

                        if (data.getLrc() != null) {
                            final String l1 = data.getLrc().getLyric();
                            lrcs.add(l1);
                            Log.d(TAG, "onTrans: LRC: " + l1);
                        }
                        if (data.getTlyric() != null) {
                            final String l2 = data.getTlyric().getLyric();
                            lrcs.add(l2);
                            Log.d(TAG, "onTrans: TLRC: " + l2);
                        }
                        if (data.getKlyric() != null) {
                            final String l3 = data.getKlyric().getLyric();
                            lrcs.add(l3);
                            Log.d(TAG, "onTrans: KLRC: " + l3);
                        }

                        if (lrcs.size() == 0) {
                            onError();
                            return;
                        }

//                        final String finalTarget = target;
                        if (!isAdded()) return;
                        getMainActivity().runOnUiThread(() -> {
                            try {
                                mLrcView.setLrcData(CustomLrcHelper.getLrc(lrcs.get(0)));
                            } catch (IOException e) {
                                e.printStackTrace();
                                onError();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        if (isAdded()) {
                            getMainActivity().runOnUiThread(() -> {
                                Toast.makeText(getMainActivity(), "Get Lrc (Lrc Error) -> Error", Toast.LENGTH_SHORT).show();
                                mLrcView.setLrcData(new ArrayList<>());
                                mLrcView.setEmptyContent("None");
                            });
                        }
                    }
                });
            }

            @Override
            public void onError() {
                if (!isAdded()) return;
                getMainActivity().runOnUiThread(() -> {
                    Toast.makeText(getMainActivity(), "Get Lrc (NetSearch) -> Error", Toast.LENGTH_SHORT).show();
                    mLrcView.setLrcData(new ArrayList<>());
                    mLrcView.setEmptyContent("None");
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        statusBar.setVisibility(View.VISIBLE);
        mTimer.cancel();
        mLrcView = null;
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }

    @Override
    public void onServiceConnected() {
        if (mLrcView != null) {
            mLrcView.setLrcData(new ArrayList<>());
            mLrcView.setEmptyContent("Loading...");
        }
        updateImage();
        updateLrc();
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {
        if (mLrcView != null) {
            mLrcView.setLrcData(new ArrayList<>());
            mLrcView.setEmptyContent("Loading...");
        }
        updateImage();
        updateLrc();
    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onMediaStoreChanged() {

    }
}
