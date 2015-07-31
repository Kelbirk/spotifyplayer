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

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Matthew on 6/22/2015.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    protected List<Artist> artists;
    protected Context context;

    public ArtistAdapter(Context context, int resource, List<Artist> artists) {
        super(context, resource, artists);
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.song_list, parent, false);

            viewHolder.artistName = (TextView) convertView.findViewById(R.id.artist_name_textView);
            viewHolder.artistImage = (ImageView) convertView.findViewById(R.id.artist_imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Artist artist = artists.get(position);

        viewHolder.artistName.setText(artist.name);

        int size = artist.images.size();

        if(artist.images != null && !artist.images.isEmpty()){
            Picasso.with(context).load(artist.images.get(size-1).url).into(viewHolder.artistImage);
        }


        return convertView;
    }


    private class ViewHolder {
        TextView artistName;
        ImageView artistImage;
    }
}
