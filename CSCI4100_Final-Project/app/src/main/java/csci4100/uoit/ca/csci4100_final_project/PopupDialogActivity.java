//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

//Activity that represents a confirmation dialog
public class PopupDialogActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_dialog);

        //Sets the relevant dialog text based on the extras passed to it
        TextView textView = (TextView) findViewById(R.id.confirm_tView);
        boolean bought_checked = getIntent().getBooleanExtra("already_bought", false);
        boolean resetConfirm = getIntent().getBooleanExtra("want_to_reset", false);
        if(bought_checked)
        {
            textView.setText(getString(R.string.add_to_bought_list_dialog));
        }
        else
        {
            if(resetConfirm)
            {
                textView.setText(getString(R.string.reset_dialog));
            }
        }
    }

    public void yesClick(View view)
    {
        MainMenuActivity.playSound(MainMenuActivity.confirmSound_ID);
        //Tells the activity that started it that the user chose to confirm
        setResult(RESULT_OK);
        finish();
    }

    public void noClick(View view)
    {
        MainMenuActivity.playSound(MainMenuActivity.cancelSound_ID);
        //Tells the activity that started it that the user chose to cancel
        setResult(RESULT_CANCELED);
        finish();
    }
}
