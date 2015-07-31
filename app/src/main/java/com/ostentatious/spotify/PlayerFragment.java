package com.ostentatious.spotify;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment {

    String id;
    private TextView artistName;
    private TextView albumName;
    private ImageView albumImage;
    private MediaPlayer mediaPlayer;
    private boolean playing = true;
    private int position;
    private SeekBar seekBar;
    private TextView trackName;
    private List<Track> tracks;
    private Track track;

    private AsyncTask<String, Integer, Track> trackFetcher;

    static PlayerFragment newInstance(List<Track> tracks, int position) {
        PlayerFragment paf = new PlayerFragment();
        paf.tracks = new ArrayList<Track>();
        paf.tracks.addAll(tracks);
        paf.position = position;
        return paf;
    }

    public PlayerFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        track = tracks.get(position);
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        artistName = (TextView) rootView.findViewById(R.id.artist_track_textView);
        albumName = (TextView) rootView.findViewById(R.id.album_track_textView);
        trackName = (TextView) rootView.findViewById(R.id.track_name_textView);
        albumImage = (ImageView) rootView.findViewById(R.id.album_track_imageView);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaStream();

        final Handler mHandler = new Handler();
        //Make sure you update Seekbar on UI thread
        getActivity().runOnUiThread(new Runnable() {


            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    Log.e("Test:", Integer.toString(mCurrentPosition));
                    seekBar.setProgress(mCurrentPosition);
                    seekBar.refreshDrawableState();
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });

        final ImageButton playButton = (ImageButton) rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    playing = !playing;
                    mediaPlayer.pause();
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                }
                else {
                    playing = !playing;
                    mediaPlayer.start();
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        final ImageButton backButton = (ImageButton) rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    position--;
                    track = tracks.get(position);
                    mediaPlayer.reset();
                    mediaStream();
                }
            }
        });

        final ImageButton forwardButton = (ImageButton) rootView.findViewById(R.id.forward_button);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < tracks.size() - 1) {
                    position++;
                    track = tracks.get(position);
                    mediaPlayer.reset();
                    mediaStream();
                }
            }
        });

        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void mediaStream() {
        if (track != null) {
            trackName.setText(track.name);
            albumName.setText(track.album.name);
            if (track.album.images != null && !track.album.images.isEmpty()) {
                Picasso.with(getActivity()).load(track.album.images.get(0).url).into(albumImage);
            }
            artistName.setText(track.artists.get(0).name);
        }

        try {
            mediaPlayer.setDataSource(track.preview_url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch(IOException exception) {
            Log.e("Error: ", "Invalid URL");
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }
}
