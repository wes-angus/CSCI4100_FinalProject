//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

//TODO: Add Save/Cancel buttons to layout and implement database updating
public class ShowNewGameReleasesActivity extends Activity implements DatabaseListener,
        GameDataListener
{
    //TODO: Replace internet functionality with database functionality
    //I will remove this later
    private static final String url = "http://www.giantbomb.com/feeds/new_releases/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_game_releases);

        //I will remove this later
        //Loads the internet file in an AsyncTask
        DownloadGameReleasesTask task = new DownloadGameReleasesTask(this);
        task.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_new_game_releases, menu);
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
    public void syncGames(final List<Game> games, short option)
    {
        if(option == 1)
        {
            if(!games.isEmpty())
            {
                ListView listView = (ListView) findViewById(R.id.game_listView);
                populateList(listView, games);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
                    {
                        String url = games.get(position).getLink();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    public void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new GameAdapter(this, data));
    }

    //I will remove this later
    @Override
    public void setGames(final List<Game> data)
    {
        ListView listView = (ListView) findViewById(R.id.game_listView);
        populateList(listView, data);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                String url = data.get(position).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}
