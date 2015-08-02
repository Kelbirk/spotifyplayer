package com.ostentatious.spotify;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private String id;
    boolean mIsLargeLayout;
    private TrackAdapter trackList;

    private AsyncTask<String, Integer, Tracks> trackFetcher;
    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.song_listView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getString(Intent.EXTRA_TEXT);
        } else {
            id = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        }

        trackList = new TrackAdapter(getActivity(), R.layout.song_list, new ArrayList<Track>());
        trackFetcher = new FetchDataTask();
        trackFetcher.execute();

        listView.setAdapter(trackList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long identification) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PlayerFragment newFragment = PlayerFragment.newInstance(trackList.tracks, position);
                if (mIsLargeLayout) {
                    // The device is using a large layout, so show the fragment as a dialog
                    newFragment.show(fm, "dialog");
                } else {
                    // The device is smaller, so show the fragment fullscreen
                    FragmentTransaction transaction = fm.beginTransaction();
                    // For a little polish, specify a transition animation
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    // To make it fullscreen, use the 'content' root view as the container
                    // for the fragment, which is always the root view for the activity
                    transaction.add(android.R.id.content, newFragment)
                            .addToBackStack(null).commit();
                }
            }
        });

        return rootView;
    }

    private class FetchDataTask extends AsyncTask<String, Integer, Tracks> {
        protected Tracks doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map<String, Object> map = new HashMap<>();
            map.put("country", Locale.getDefault().getCountry());
            try {
                return spotify.getArtistTopTrack(id, map);
            }
            catch (RetrofitError error) {
                Log.e("Error:", error.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Tracks tracks) {
            super.onPostExecute(tracks);

            if (tracks != null && !tracks.tracks.isEmpty()) {
                trackList.clear();
                trackList.addAll(tracks.tracks);
                trackList.notifyDataSetChanged();
            }
            else {
                Toast.makeText(getActivity(), "Top tracks are unavailable for this artist", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
