package csci4100.uoit.ca.csci4100_final_project;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String str = getString(R.string.about_txt);
        int i_index = str.indexOf("\n");
        int b_index1_start = str.indexOf(":") + 1;
        int b_index1_end = str.indexOf("\n", b_index1_start);
        int b_index2_start = str.indexOf(":", b_index1_end) + 1;
        int b_index2_end = str.indexOf("\n", b_index2_start);
        int b_index3 = str.indexOf(":", b_index2_end) + 1;
        SpannableString text = new SpannableString(str);
        text.setSpan(new StyleSpan(Typeface.ITALIC), 0, i_index, 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), b_index1_start, b_index1_end, 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), b_index2_start, b_index2_end, 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), b_index3, text.length(), 0);
        TextView textView = (TextView) findViewById(R.id.about_tView);
        textView.setText(text, TextView.BufferType.SPANNABLE);
    }

    public void backToMenu(View view)
    {
        MainMenuActivity.soundPool.play(MainMenuActivity.buttonSound1_ID, 1, 1, 0, 0, 1);
        finish();
    }
}
