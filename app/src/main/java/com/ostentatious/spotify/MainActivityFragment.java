package com.ostentatious.spotify;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistAdapter artistList;
    private AsyncTask<String, Integer, ArtistsPager> fetchArtists;

    public EditText searchBar;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistList = new ArtistAdapter(getActivity(), R.layout.song_list, new ArrayList<Artist>());
    }

    private void updateData(String search){
        if(fetchArtists != null)
            fetchArtists.cancel(true);
        fetchArtists = new FetchDataTask();
        fetchArtists.execute(search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        searchBar = (EditText) rootView.findViewById(R.id.artist_search_editText);
        ListView listView = (ListView) rootView.findViewById(R.id.artist_listView);

        listView.setAdapter(artistList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean mTwoPane = getResources().getBoolean(R.bool.large_layout);
                Artist artist = artistList.getItem(position);
                if(mTwoPane) {
                    Bundle args = new Bundle();
                    args.putString(Intent.EXTRA_TEXT, artist.id);

                    TopTracksFragment fragment = new TopTracksFragment();
                    fragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.tracks_pane, fragment, "fragment")
                            .commit();
                }
                else {
                    Intent intent = new Intent(getActivity(), TopTracks.class).putExtra(Intent.EXTRA_TEXT, artist.id);
                    startActivity(intent);
                }
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateData(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (isNetworkAvailable() == false)
            Toast.makeText(getActivity(), "No network found", Toast.LENGTH_SHORT).show();

        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class FetchDataTask extends AsyncTask<String, Integer, ArtistsPager> {
        protected ArtistsPager doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            try {
                return spotify.searchArtists(params[0]);
            }
            catch (RetrofitError e) {
                Log.e("Error:", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArtistsPager artists) {
            super.onPostExecute(artists);

            if (artists != null && !artists.artists.items.isEmpty()) {
                artistList.clear();
                artistList.addAll(artists.artists.items);
                artistList.notifyDataSetChanged();
            }
            else Toast.makeText(getActivity(), "No artists found", Toast.LENGTH_SHORT).show();
        }
    }
}
