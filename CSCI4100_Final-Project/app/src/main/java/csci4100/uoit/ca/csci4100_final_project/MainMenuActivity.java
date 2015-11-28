//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
TODO: Add boolean to make sure the downloading + adding games to the database only happens once
TODO: Add activity for looking at a list of already bought games
TODO: (with possibly manual adding of user-specified titles)
*/
public class MainMenuActivity extends Activity implements GameDataListener, DatabaseListener
{
    private static final String url = "http://www.gamespot.com/feeds/new_releases/";
    public static SoundPool soundPool;
    public static int buttonSound1_ID = -1;
    public static int buttonSound2_ID = -1;
    public static int cancelSound_ID = -1;
    public static int saveSound_ID = -1;
    public static int itemClickSound_ID = -1;

    @Override
    //@SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Parses the new game releases feed in an AsyncTask
        DownloadGameReleasesTask task = new DownloadGameReleasesTask(this);
        task.execute(url);

        AssetManager assetManager = this.getAssets();

        AssetFileDescriptor fd1;
        AssetFileDescriptor fd2;
        AssetFileDescriptor fd3;
        AssetFileDescriptor fd4;
        AssetFileDescriptor fd5;

        if((android.os.Build.VERSION.SDK_INT) >= 21){ // Checked sdk value, not an error
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            builder.setMaxStreams(10);
            soundPool = builder.build();
        }
        else{
            //noinspection deprecation
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        try
        {
            fd1 = assetManager.openFd("retro_beep.wav");
            fd2 = assetManager.openFd("laser-shot-silenced.wav");
            fd3 = assetManager.openFd("cancel_click.ogg");
            fd4 = assetManager.openFd("menusel.wav");
            fd5 = assetManager.openFd("item_beep.wav");

            buttonSound1_ID = soundPool.load(fd1, 0);
            buttonSound2_ID = soundPool.load(fd2, 1);
            cancelSound_ID = soundPool.load(fd3, 2);
            saveSound_ID = soundPool.load(fd4, 3);
            itemClickSound_ID = soundPool.load(fd5, 4);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        soundPool.release();
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
        }
    }

    public void showGameList(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        soundPool.play(buttonSound2_ID, 1, 1, 1, 0, 1);
        startActivity(intent);
    }

    public void viewAboutText(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        soundPool.play(buttonSound1_ID, 1, 1, 0, 0, 1);
        startActivity(intent);
    }
}
