package pl.krawczyk.maciej.GdzieBylem.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import pl.krawczyk.maciej.GdzieBylem.R;

public class BaseActivity extends Activity{

    private boolean isItemAboutVisible = true;
    private boolean isItemMapVisible = true;

    // Show and hide button About in Action Bar. Should by use in onCreate in SettingsActivity.
    protected void setItemAboutVisible(Boolean isVisible){
        this.isItemAboutVisible = isVisible;
    }

    // Show and hide button Settings in Action Bar. Should by use in onCreate in AboutActivity and SettingsActivity.
    protected void setItemMapVisible(Boolean isVisible){
        this.isItemMapVisible = isVisible;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItemAbout = menu.findItem(R.id.menuItemAbout);
        MenuItem menuItemMap = menu.findItem(R.id.menuItemMap);

        menuItemAbout.setVisible(isItemAboutVisible);
        menuItemMap.setVisible(isItemMapVisible);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case (R.id.menuItemAbout):
                Intent startAbout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(startAbout);
                break;
            case (R.id.menuItemMap):
                Intent startMapOfLocations = new Intent(getApplicationContext(), MapOfLocationsActivity.class);
                startActivity(startMapOfLocations);
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }
}