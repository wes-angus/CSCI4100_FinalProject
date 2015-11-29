package csci4100.uoit.ca.csci4100_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import csci4100.uoit.ca.csci4100_final_project.R;

public class PopupDialogActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_dialog);
    }

    public void noClick(View view)
    {
        setResult(RESULT_OK);
        finish();
    }

    public void yesClick(View view)
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
