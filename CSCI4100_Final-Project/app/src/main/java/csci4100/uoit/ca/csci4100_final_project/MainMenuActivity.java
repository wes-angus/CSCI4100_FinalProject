//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends Activity implements GameDataListener, DatabaseListener
{
    private static final String url = "http://www.gamespot.com/feeds/new-releases/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Parses the new game releases feed in an AsyncTask
        DownloadGameReleasesTask task = new DownloadGameReleasesTask(this);
        task.execute(url);
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
            Toast.makeText(this, R.string.games_added, Toast.LENGTH_SHORT).show();
        }
    }

    public void showGameList(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        startActivity(intent);
    }

    public void viewAboutText(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
