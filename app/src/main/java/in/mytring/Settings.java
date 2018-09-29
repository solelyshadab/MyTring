package in.mytring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by shadabbaig on 15/02/17.
 */

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        createSpinner();
    }

    public void createSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.language_dropdownlist);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void mobileDashboard(View v)
    {
        Intent i = new Intent(Settings.this, in.mytring.MainActivity.class);
        startActivity(i);
        finish();
    }

    public void wifiDashboard(View v)
    {
        Intent j = new Intent(Settings.this, WifiActivity.class);
        startActivity(j);
        finish();
    }

    public void switchNetwork(View v)
    {
        Intent i = new Intent(Settings.this, SwitchNetworkActivity.class);
        startActivity(i);
        finish();
    }


    public void speedTest(View v)
    {
        Intent j = new Intent(Settings.this, SpeedTestActivity.class);
        startActivity(j);
        finish();
    }

}
