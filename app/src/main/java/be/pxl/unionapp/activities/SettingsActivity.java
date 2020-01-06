package be.pxl.unionapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import be.pxl.unionapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Door het klikken op de up-button, wordt de user terug gebracht naar het scherm waar hij vandaan kwam

        init();
    }

    private void init() {
        setTitle(R.string.action_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
