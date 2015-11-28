//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

/*
TODO: Implement "bought" checkbox to move games that are checked to a separate List(Set)
TODO: Add more "whenWillBuy" values + add one that removes the game immediately from the database
*/
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
        String[] whenWillBuyValues = getResources().getStringArray(R.array.options);
        String neverWillBuy = whenWillBuyValues[whenWillBuyValues.length - 1];
        if(spinner.getSelectedItem().toString().equals(neverWillBuy))
        {
            Intent c_intent1 = new Intent(this, PopupDialogActivity.class);
            c_intent1.putExtra("already_bought", false);
        }
        else
        {
            CheckBox checkBox = (CheckBox) findViewById(R.id.cBox_bought);
            if (checkBox.isChecked())
            {
                Intent c_intent2 = new Intent(this, PopupDialogActivity.class);
                c_intent2.putExtra("already_bought", true);
            }
            else
            {
                Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
                intent.putExtra("when_will_buy", spinner.getSelectedItem().toString());
                intent.putExtra("game_position", getIntent().getIntExtra("game_position", 0));
                setResult(Activity.RESULT_OK, intent);
                MainMenuActivity.playSound(MainMenuActivity.saveSound_ID);
                finish();
            }
        }
    }

    public void cancelModify(View view)
    {
        setResult(Activity.RESULT_CANCELED);
        MainMenuActivity.playSound(MainMenuActivity.cancelSound_ID);
        finish();
    }

    public void openGameLink(View view)
    {
        String url = game.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainMenuActivity.playSound(MainMenuActivity.buttonSound2_ID);
        startActivity(intent);
    }
}
