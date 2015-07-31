package com.ostentatious.spotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Matthew on 7/1/2015.
 */
public class TrackAdapter extends ArrayAdapter<Track> {

    protected List<Track> tracks;
    protected Context context;

    public TrackAdapter(Context context, int resource, List<Track> tracks) {
        super(context, resource, tracks);
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.song_list, parent, false);

            viewHolder.trackName = (TextView) convertView.findViewById(R.id.artist_name_textView);
            viewHolder.albumName = (TextView) convertView.findViewById(R.id.album_name_textView);
            viewHolder.albumImage = (ImageView) convertView.findViewById(R.id.artist_imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Track track = tracks.get(position);

        viewHolder.trackName.setText(track.name);
        viewHolder.albumName.setText(track.album.name);

        int size = track.album.images.size();

        if(track.album.images != null && !track.album.images.isEmpty()){
            Picasso.with(context).load(track.album.images.get(size-1).url).into(viewHolder.albumImage);
        }

        return convertView;
    }


    private class ViewHolder {
        TextView trackName;
        TextView albumName;
        ImageView albumImage;
    }
}