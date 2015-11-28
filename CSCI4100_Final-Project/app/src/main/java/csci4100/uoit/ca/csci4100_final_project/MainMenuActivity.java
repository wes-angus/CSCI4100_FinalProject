//Authors: Wesley Angus & Montgomery Alban

package csci4100.uoit.ca.csci4100_final_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
TODO: Add boolean to make sure the downloading + adding games to the database only happens once
TODO: Add activity for looking at a list of already bought games
TODO: (with possibly manual adding of user-specified titles)
*/
public class MainMenuActivity extends Activity implements GameDataListener, DatabaseListener
{
    private static String url = "";
    private static SoundPool soundPool = null;
    public static int buttonSound1_ID = -1;
    public static int buttonSound2_ID = -1;
    public static int cancelSound_ID = -1;
    public static int saveSound_ID = -1;
    public static int itemClickSound_ID = -1;
    private static Map<Integer, Boolean> soundLoaded;
    private boolean added = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if(url.isEmpty())
        {
            url = "http://www." + getString(R.string.reference_feed_url);
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
                //noinspection deprecation
                soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                setSoundLoadedListener();
            }
        }

        try
        {
            fd1 = assetManager.openFd("retro_beep.wav");
            fd2 = assetManager.openFd("laser-shot-silenced.wav");
            fd3 = assetManager.openFd("item_beep.wav");
            fd4 = assetManager.openFd("cancel_click.ogg");
            fd5 = assetManager.openFd("menusel.wav");

            buttonSound1_ID = soundPool.load(fd1, 1);
            buttonSound2_ID = soundPool.load(fd2, 1);
            itemClickSound_ID = soundPool.load(fd3, 1);
            cancelSound_ID = soundPool.load(fd4, 1);
            saveSound_ID = soundPool.load(fd5, 1);

            soundLoaded.put(buttonSound1_ID, false);
            soundLoaded.put(buttonSound2_ID, false);
            soundLoaded.put(itemClickSound_ID, false);
            soundLoaded.put(cancelSound_ID, false);
            soundLoaded.put(saveSound_ID, false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setSoundLoadedListener()
    {
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            //Only play sound after it has loaded
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                if(sampleId == buttonSound1_ID)
                {
                    soundLoaded.put(buttonSound1_ID, true);
                    Log.i("SoundLoaded", "Button Sound 1 loaded!");
                }
                else if(sampleId == buttonSound2_ID)
                {
                    soundLoaded.put(buttonSound2_ID, true);
                    Log.i("SoundLoaded", "Button Sound 2 loaded!");
                }
                else if(sampleId == itemClickSound_ID)
                {
                    soundLoaded.put(itemClickSound_ID, true);
                    Log.i("SoundLoaded", "Item Click Sound loaded!");
                }
                else if(sampleId == cancelSound_ID)
                {
                    soundLoaded.put(cancelSound_ID, true);
                    Log.i("SoundLoaded", "Cancel Sound loaded!");
                }
                else if(sampleId == saveSound_ID)
                {
                    soundLoaded.put(saveSound_ID, true);
                    Log.i("SoundLoaded", "Save Sound loaded!");
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onPause()
    {
        super.onPause();


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

        //Adds the list of games to the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 0, data);
    }

    @Override
    public void syncGames(ArrayList<Game> games, short option)
    {
        if(option == 0)
        {
            Button button = (Button)findViewById(R.id.showGameList_btn);
            button.setEnabled(true);
            Toast.makeText(this, R.string.games_added, Toast.LENGTH_SHORT).show();
            added = true;
        }
    }

    public static void playSound(int soundID)
    {
        if(soundLoaded.get(soundID))
        {
            soundPool.play(soundID, 1, 1, 1, 0, 1);
        }
    }

    public void showGameList(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        playSound(buttonSound2_ID);
        startActivity(intent);
    }



    public void viewAboutText(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        playSound(buttonSound1_ID);
        startActivity(intent);
    }
}
