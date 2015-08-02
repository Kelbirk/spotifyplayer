package com.ostentatious.spotify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public boolean mTwoPane;
    static final String FRAGMENT_TAG = "top tracks";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTwoPane = getResources().getBoolean(R.bool.large_layout);
        super.onCreate(savedInstanceState);
        if(mTwoPane && savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tracks_fragment, new TopTracksFragment(), FRAGMENT_TAG)
                    .commit();
        }
        else
            setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
