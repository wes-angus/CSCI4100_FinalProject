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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
TODO: Add checks for recently removed/bought games so they aren't re-added to the database
TODO: Add activity for looking at a list of already bought games
TODO: (with possibly manual adding of user-specified titles)
TODO: Remove recently removed games whose release date is a week past
*/
public class MainMenuActivity extends Activity implements GameDataListener, DatabaseListener
{
    private static final String prefs_filename = "newGameList_prefs";
    private String url = "";
    private static SoundPool soundPool = null;
    public static int buttonSound1_ID = -1;
    public static int buttonSound2_ID = -1;
    public static int cancelSound_ID = -1;
    public static int confirmSound_ID = -1;
    public static int itemClickSound_ID = -1;
    private static Map<Integer, Boolean> soundLoaded;
    private boolean added = false;
    private static Set<String> boughtGames;
    private static Set<String> removedGames;
    private static Map<String, String> removedGameDates;

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

        boughtGames = new LinkedHashSet<>();
        removedGames = new HashSet<>();
        removedGameDates = new HashMap<>();

        if(savedInstanceState != null)
        {
            List<String> boughtGameList = savedInstanceState.getStringArrayList("bought_game_list");
            if(boughtGameList != null)
            {
                boughtGames.addAll(boughtGameList);
            }
            List<String> removedGameList = savedInstanceState.getStringArrayList("removed_game_list");
            if(removedGameList != null)
            {
                removedGames.addAll(removedGameList);
            }
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
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the current activity state
        savedInstanceState.putBoolean("added", added);
        savedInstanceState.putStringArrayList("bought_game_list", getBoughtGameList());
        savedInstanceState.putStringArrayList("removed_game_list", new ArrayList<>(removedGames));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore activity state from saved instance
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
        editor.putBoolean("added", false);
        editor.putStringSet("bought_games", boughtGames);
        editor.putStringSet("removed_games", removedGames);
        for (String game : removedGames)
        {
            editor.putString(game, removedGameDates.get(game));
        }
        editor.apply();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences(prefs_filename, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("added", added);
        editor.putStringSet("bought_games", boughtGames);
        editor.putStringSet("removed_games", removedGames);
        for (String game : removedGames)
        {
            editor.putString(game, removedGameDates.get(game));
        }
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(prefs_filename, Activity.MODE_PRIVATE);
        added = prefs.getBoolean("added", false);
        boughtGames = prefs.getStringSet("bought_games", new LinkedHashSet<String>());
        removedGames = prefs.getStringSet("removed_games", new HashSet<String>());
        for (Iterator<String> i = removedGames.iterator(); i.hasNext();)
        {
            String game = i.next();
            String gameDate = prefs.getString(game, "");
            removedGameDates.put(game, gameDate);
            /*
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            Date curDate = calendar.getTime();
            try
            {
                Date date = dateFormat.parse(gameDate);
                long diffTime = curDate.getTime() - date.getTime();
                long diffDays = diffTime / (1000 * 60 * 60 * 24);
                if(diffDays > 7)
                {
                    i.remove();
                    removedGameDates.remove(game);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            */
        }
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

        //Check if games were removed from the game list, so they don't get re-added to the list
        List<Game> gamesToAdd = new ArrayList<>();
        for (Game game : data)
        {
            if(!removedGames.contains(game.getTitle()))
            {
                gamesToAdd.add(game);
            }
        }

        //Adds the list of games to the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 0, gamesToAdd);
    }

    @Override
    public void syncGames(ArrayList<Game> games, short option)
    {
        if(option == 0)
        {
            Button button = (Button)findViewById(R.id.showGameList_btn);
            button.setEnabled(true);
            added = true;
            Toast.makeText(this, R.string.games_added, Toast.LENGTH_SHORT).show();
        }
    }

    public static void addBoughtGame(Game game)
    {
        boughtGames.add(game.getTitle());
        addRecentlyRemovedGame(game);
    }

    public static void addRecentlyRemovedGame(Game game)
    {
        removedGames.add(game.getTitle());
        removedGameDates.put(game.getTitle(), game.getReleaseDate());
    }

    public static ArrayList<String> getBoughtGameList()
    {
        if(boughtGames != null)
        {
            return new ArrayList<>(boughtGames);
        }
        return new ArrayList<>();
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

    public void viewBoughtList(View view)
    {
        /*
        Intent intent = new Intent(this, ViewBoughtGameListActivity.class);
        playSound(buttonSound1_ID);
        startActivity(intent);
        */
    }
}
