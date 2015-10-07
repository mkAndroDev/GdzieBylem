package pl.krawczyk.maciej.GdzieBylem.activities;

import android.os.Bundle;
import android.view.MenuItem;
import pl.krawczyk.maciej.GdzieBylem.R;

public class AboutActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setItemMapVisible(false);
        setItemAboutVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }
}