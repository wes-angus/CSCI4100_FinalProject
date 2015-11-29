//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class PopupDialogActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_dialog);

        TextView textView = (TextView) findViewById(R.id.confirm_tView);
        boolean bought_checked = getIntent().getBooleanExtra("already_bought", false);
        if(bought_checked)
        {
            textView.setText(getString(R.string.add_to_bought_list_dialog));
        }
    }

    public void yesClick(View view)
    {
        MainMenuActivity.playSound(MainMenuActivity.confirmSound_ID);
        setResult(RESULT_OK);
        finish();
    }

    public void noClick(View view)
    {
        MainMenuActivity.playSound(MainMenuActivity.cancelSound_ID);
        setResult(RESULT_CANCELED);
        finish();
    }
}
