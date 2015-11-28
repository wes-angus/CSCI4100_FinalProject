//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GameDetailAndModifyActivity extends Activity
{
    Game game;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail_and_modify);

        Bundle bundle = getIntent().getExtras();
        game = bundle.getParcelable("game");
        spinner = (Spinner) findViewById(R.id.willBuy_spinner);
        if(game != null) {
            populateSpinner(spinner, R.array.options, game.getWhenWillBuy());
            showGameInfo(game);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState )
    {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the current spinner selection
        savedInstanceState.putInt("spinner_pos", spinner.getSelectedItemPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore activity state from saved instance
        spinner.setSelection(savedInstanceState.getInt("spinner_pos"));
    }

    private void showGameInfo(Game game)
    {
        TextView titleView = (TextView) findViewById(R.id.title_txt);
        titleView.setText(game.getTitle());
        TextView rDateView = (TextView) findViewById(R.id.rDate_txt);
        rDateView.setText(game.getReleaseDate());
        TextView descView = (TextView) findViewById(R.id.desc_txt);
        descView.setText(game.getDescription());
    }

    private void populateSpinner(Spinner spinner, int arrayId, String whenWillBuy)
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(!whenWillBuy.isEmpty())
        {
            spinner.setSelection(adapter.getPosition(whenWillBuy));
        }
    }

    public void saveWillBuyProperty(View view)
    {
        Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
        intent.putExtra("when_will_buy", spinner.getSelectedItem().toString());
        intent.putExtra("game_position", getIntent().getIntExtra("game_position", 0));
        setResult(Activity.RESULT_OK, intent);
        MainMenuActivity.soundPool.play(MainMenuActivity.saveSound_ID, 1, 1, 3, 0, 1);
        finish();
    }

    public void cancelModify(View view)
    {
        setResult(Activity.RESULT_CANCELED);
        MainMenuActivity.soundPool.play(MainMenuActivity.cancelSound_ID, 1, 1, 2, 0, 1);
        finish();
    }

    public void openGameLink(View view)
    {
        String url = game.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainMenuActivity.soundPool.play(MainMenuActivity.buttonSound1_ID, 1, 1, 0, 0, 1);
        startActivity(intent);
    }
}
