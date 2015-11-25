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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//TODO: Scroll down to see the updated item in the ListView
public class ShowNewGameReleasesActivity extends Activity implements DatabaseListener
{
    public static final int MODIFY_GAME = 10;
    ArrayList<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_game_releases);

        //Gets the list of games from the database
        LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
        task.execute((short) 1);


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
    public void syncGames(final ArrayList<Game> games, short option)
    {
        if(option == 1 || option == 2)
        {
            if(!(games.isEmpty() || games == null))
            {
                ListView listView = (ListView) findViewById(R.id.game_listView);
                populateList(listView, games);

                this.games = games;

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
                    {
                        goToModifyScreen(position);
                    }
                });
            }

            if (option == 2)
            {
                Toast.makeText(this, R.string.mod_success, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateList(ListView listView, List<Game> data)
    {
        listView.setAdapter(new GameAdapter(this, data));
    }

    public void goToModifyScreen(int gamePosition)
    {
        Intent intent = new Intent(this, GameDetailAndModifyActivity.class);
        Bundle gamesBundle = new Bundle();
        gamesBundle.putParcelable("game", games.get(gamePosition));
        intent.putExtras(gamesBundle);
        intent.putExtra("game_position", gamePosition);
        startActivityForResult(intent, MODIFY_GAME);
    }

    public void backToMenu(View view)
    {
        finish();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result)
    {
        //Gets a result back from the AskQuestion activity
        super.onActivityResult(reqCode, resCode, result);
        if (resCode == Activity.RESULT_OK)
        {
            if(reqCode == MODIFY_GAME)
            {
                /*
                Updates the "willBuy" property of the game that was selected in the database
                and gets the updated list of games to update the ListView.
                */
                int gamePosition = result.getIntExtra("game_position", 0);
                Game receivedGame = games.get(gamePosition);
                receivedGame.setWhenWillBuy(result.getStringExtra("when_will_buy"));
                LoadDatabaseInfoTask task = new LoadDatabaseInfoTask(this, getApplicationContext());
                task.execute((short) 2, receivedGame);
            }
        }
    }
}
