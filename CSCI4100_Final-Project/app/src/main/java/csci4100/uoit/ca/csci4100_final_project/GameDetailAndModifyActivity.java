//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
Activity that allows you to change the "whenWillBuy" property
of a game and lets you view more information about said game
*/
public class GameDetailAndModifyActivity extends Activity
{
    public static final int DELETE_DIALOG = 13;

    Game game;
    Spinner spinner;
    boolean bought = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail_and_modify);

        Bundle bundle = getIntent().getExtras();
        game = bundle.getParcelable("game");//Gets the game sent by ShowNewGameReleasesActivity
        spinner = (Spinner) findViewById(R.id.willBuy_spinner);
        if(game != null)
        {
            //Put all of the selectable "whenWillBuy" values in the spinner
            populateSpinner(spinner, R.array.options, game.getWhenWillBuy());
            showGameInfo(game);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState )
    {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the current spinner selection
        savedInstanceState.putInt("spinner_pos", spinner.getSelectedItemPosition());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore activity state from saved instance
        spinner.setSelection(savedInstanceState.getInt("spinner_pos"));
    }

    //Populates the textFields with info about the game
    private void showGameInfo(Game game)
    {
        TextView titleView = (TextView) findViewById(R.id.title_txt);
        titleView.setText(game.getTitle());
        TextView rDateView = (TextView) findViewById(R.id.rDate_txt);
        rDateView.setText(game.getReleaseDate());
        TextView descView = (TextView) findViewById(R.id.desc_txt);
        descView.setText(game.getDescription());
    }

    //Puts all of the possible whenWillBuy values into the spinner
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
        //Gets the array of "whenWillBuy" values
        String[] whenWillBuyValues = getResources().getStringArray(R.array.options);
        //neverWillBuy = "Will Never Buy It"
        String neverWillBuy = whenWillBuyValues[whenWillBuyValues.length - 1];
        //If the game will be removed...
        if(spinner.getSelectedItem().toString().equals(neverWillBuy))
        {
            /*
            ...open a dialog to confirm whether the user wishes
            to remove the game from the new game list or not
            */
            Intent c_intent1 = new Intent(this, PopupDialogActivity.class);
            bought = false;
            c_intent1.putExtra("already_bought", false);
            startActivityForResult(c_intent1, DELETE_DIALOG);
        }
        else
        {
            CheckBox checkBox = (CheckBox) findViewById(R.id.cBox_bought);
            //If the game will be labeled as "bought"...
            if (checkBox.isChecked())
            {
                /*
                ...open a dialog to confirm whether the user wishes to treat
                the game as a "bought" game, moving it from the new game list
                to the bought game list and preventing modification of this game
                */
                Intent c_intent2 = new Intent(this, PopupDialogActivity.class);
                bought = true;
                c_intent2.putExtra("already_bought", true);
                startActivityForResult(c_intent2, DELETE_DIALOG);
            }
            else
            {
                /*
                Sends the selected whenWillBuy value to the
                previous activity to update the database
                */
                Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
                intent.putExtra("when_will_buy", spinner.getSelectedItem().toString());
                setResult(Activity.RESULT_OK, intent);
                MainMenuActivity.playSound(MainMenuActivity.confirmSound_ID);
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

    public static void startGameLinkActivity(Game game, Activity activity)
    {
        //Opens the link to the game in the user's preferred browser
        String url = game.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        MainMenuActivity.playSound(MainMenuActivity.buttonSound2_ID);
        activity.startActivity(intent);
    }

    public void openGameLink(View view)
    {
        startGameLinkActivity(game, this);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result)
    {
        //Gets a result back from PopupDialogActivity
        super.onActivityResult(reqCode, resCode, result);
        if (resCode == Activity.RESULT_OK)
        {
            if(reqCode == DELETE_DIALOG)
            {
                Intent intent = new Intent(this, ShowNewGameReleasesActivity.class);
                if(!bought)
                {
                    //Sets the whenWillBuy value to the "removed game" value: "Will Never Buy"
                    intent.putExtra("when_will_buy", spinner.getSelectedItem().toString());
                }
                else
                {
                    //Sets the whenWillBuy value to the "bought game" value: "Bought"
                    intent.putExtra("when_will_buy", getString(R.string.bought));
                }
                /*
                Sends the selected whenWillBuy value to the
                previous activity to update the database
                */
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public void createCalendarEvent(View view)
    {
        //Creates a calendar event concerning the game on the game's release date
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                ShowNewGameReleasesActivity.parseDatePattern, Locale.US);
        try
        {
            Date date = dateFormat.parse(game.getReleaseDate());
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.getTime())
                    .putExtra(CalendarContract.Events.TITLE, "Release of " + game.getTitle());
            startActivity(intent);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
}
