package csci4100.uoit.ca.csci4100_final_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import csci4100.uoit.ca.csci4100_final_project.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void backToMenu(View view)
    {
        MainMenuActivity.soundPool.play(MainMenuActivity.sound1_ID, 1, 1, 0, 0, 1);
        finish();
    }
}
