//Authors: Wesley Angus & Montgomery Alban

package csci4100.uoit.ca.csci4100_final_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
TODO: Maybe improve search more
TODO: Add more comments
TODO: Maybe add broadcast receiver for headphones being plugged/unplugged
TODO: Add button to clear all data from the database
*/
//Main menu activity from which the other activities are accessed
public class MainMenuActivity extends Activity implements GameDataListener, DatabaseListener
{
    private static final String prefs_filename = "loadOnce";
    private String url = "";
    private static SoundPool soundPool = null;
    public static int buttonSound1_ID = -1;
    public static int buttonSound2_ID = -1;
    public static int cancelSound_ID = -1;
    public static int confirmSound_ID = -1;
    public static int itemClickSound_ID = -1;
    private static Map<Integer, Boolean> soundLoaded;
    private boolean added = false;
    private Set<String> downloadedGames;

    private GoogleApiClient mGoogleApiClient;    
    
    @SuppressLint("NewApi") //Used to suppress false "errors"
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Setting up the google geographic api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .build();
        
        if(url.isEmpty())
        {
            url = "http://www." + getString(R.string.reference_feed_url);//Url for the feed to parse
        }
        if(!added)
        {
            //Parses the new game releases feed in an AsyncTask
            DownloadGameReleasesTask task = new DownloadGameReleasesTask(this);
            task.execute(url);
        }

        AssetManager assetManager = this.getAssets();
        AssetFileDescriptor fd1;
        AssetFileDescriptor fd2;
        AssetFileDescriptor fd3;
        AssetFileDescriptor fd4;
        AssetFileDescriptor fd5;
        soundLoaded = new HashMap<>();

        //Checks the android version to use the correct version of the SoundPool constructor
        if((android.os.Build.VERSION.SDK_INT) >= 21) // Checked sdk value, not an error
        {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            builder.setMaxStreams(5);
            if(soundPool == null)
            {
                soundPool = builder.build();
                setSoundLoadedListener();
            }
        }
        else
        {
            if(soundPool == null)
            {
                //cancel deprecation error
                //noinspection deprecation
                soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                setSoundLoadedListener();
            }
        }

        try
        {
            //Open the sound files and load the sounds
            fd1 = assetManager.openFd("retro_beep.wav");
            fd2 = assetManager.openFd("laser-shot-silenced.wav");
            fd3 = assetManager.openFd("item_beep.wav");
            fd4 = assetManager.openFd("cancel_click.ogg");
            fd5 = assetManager.openFd("menusel.wav");

            buttonSound1_ID = soundPool.load(fd1, 1);
            buttonSound2_ID = soundPool.load(fd2, 1);
            itemClickSound_ID = soundPool.load(fd3, 1);
            cancelSound_ID = soundPool.load(fd4, 1);
            confirmSound_ID = soundPool.load(fd5, 1);

            soundLoaded.put(buttonSound1_ID, false);
            soundLoaded.put(buttonSound2_ID, false);
            soundLoaded.put(itemClickSound_ID, false);
            soundLoaded.put(cancelSound_ID, false);
            soundLoaded.put(confirmSound_ID, false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setSoundLoadedListener()
    {
        //Once the sounds are loaded, save the soundLoaded value to
        //the map so the sound only plays if it has finished loading
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            //Only play sound after it has loaded
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundLoaded.put(sampleId, true);
                if (sampleId == buttonSound1_ID) {
                    Log.i("SoundLoaded", "Button Sound 1 loaded!");
                } else if (sampleId == buttonSound2_ID) {
                    Log.i("SoundLoaded", "Button Sound 2 loaded!");
                } else if (sampleId == itemClickSound_ID) {
                    Log.i("SoundLoaded", "Item Click Sound loaded!");
                } else if (sampleId == cancelSound_ID) {
                    Log.i("SoundLoaded", "Cancel Sound loaded!");
                } else if (sampleId == confirmSound_ID) {
                    Log.i("SoundLoaded", "Save Sound loaded!");
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState)
    {
        //Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        //Save the state of adding games to the database so the feed is not
        //downloaded every time the user navigates back to the main menu
        savedInstanceState.putBoolean("added", added);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        //Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        //Restore the state of adding games to the database so the feed is not
        //downloaded every time the user navigates back to the main menu
        savedInstanceState.getBoolean("added", false);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        soundPool.release();
        soundPool = null;

        SharedPreferences prefs = getSharedPreferences(prefs_filename, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //Resets the downloading state so that it downloads
        //the feed next time you start up the application
        editor.putBoolean("added", false);
        editor.apply();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences(prefs_filename, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //Save the state of adding games to the database so the feed is not
        //downloaded every time the user navigates back to the main menu
        editor.putBoolean("added", added);
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(prefs_filename, Activity.MODE_PRIVATE);
        //Restore the state of adding games to the database so the feed is not
        //downloaded every time the user navigates back to the main menu
        added = prefs.getBoolean("added", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    @Override
    public void setGames(final List<Game> data)
    {
        Toast.makeText(this, R.string.games_downloaded, Toast.LENGTH_SHORT).show();
        downloadedGames = new HashSet<>();
        for (Game downloadedGame : data)
        {
            downloadedGames.add(downloadedGame.getTitle());
        }

        //Adds the list of games to the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 0, data);
    }

    @Override
    public void syncGames(List<Game> games, short option)
    {
        if(option == 0) //Returns after items are added to the database
        {
            //Get the list of removed games from the database
            LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
            task.execute((short) 5);
        }
        else if(option == 5) //Returns after the list of removed games is obtained
        {
            List<Game> gamesToRemove = new ArrayList<>();
            for (int i = 0; i < games.size(); i++)
            {
                /*
                If the removed game is not found in the feed,
                add it to a list of games to remove from the database
                */
                if(!downloadedGames.contains(games.get(i).getTitle()))
                {
                    gamesToRemove.add(games.get(i));
                }
            }
            if(gamesToRemove.isEmpty())
            {
                Button button = (Button) findViewById(R.id.showGameList_btn);
                button.setEnabled(true);
                added = true;
                Toast.makeText(this, R.string.games_added, Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Remove the old removed games from the database
                LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                task.execute((short) 3, gamesToRemove);
            }
        }
        //Returns after some/all of the removed games are deleted from the database
        else if(option == 3)
        {
            //Enables the button to let you view the list
            Button button = (Button) findViewById(R.id.showGameList_btn);
            button.setEnabled(true);
            added = true; //Stops the feed from being downloaded more than once after starting
            Toast.makeText(this, R.string.games_added, Toast.LENGTH_SHORT).show();
        }
    }

    //Play the given sound if it has loaded
    public static void playSound(int soundID)
    {
        if(soundLoaded.get(soundID))
        {
            soundPool.play(soundID, 1, 1, 1, 0, 1);
        }
    }

    //Starts the activity to view the list of new game releases
    public void showGameList(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        playSound(buttonSound2_ID);
        startActivity(intent);
    }

    //Starts the activity to view the "about" text for the app
    public void viewAboutText(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        playSound(buttonSound1_ID);
        startActivity(intent);
    }

    //Starts the activity to view the list of purchased games
    public void viewBoughtList(View view)
    {
        Intent intent = new Intent(this, ViewBoughtGameListActivity.class);
        playSound(buttonSound1_ID);
        startActivity(intent);
    }

    // utility changed to just call the picker
    public void viewMap(View view) {
        //Intent showMapIntent = new Intent(MainMenuActivity.this, MapsActivity.class);
        //startActivity(showMapIntent);
        onPickButtonClick(view);
    }

    final int REQUEST_PLACE_PICKER = 1;
    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            Log.e("PlacePicker", "repair");
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            Log.e("PlacePicker", "play services not available");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latLng = place.getLatLng();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            Intent showMapIntent = new Intent(MainMenuActivity.this, MapsActivity.class);
            StoreDetails storeDetails = new StoreDetails(name.toString(),
                                address.toString(),
                                Html.fromHtml(attributions).toString(),
                                latLng);
            showMapIntent.putExtra("dest", storeDetails);
            startActivity(showMapIntent);

        } else if(requestCode == REQUEST_PLACE_PICKER) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("PlacePicker", "Failed pick");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
