package pl.krawczyk.maciej.GdzieBylem.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import pl.krawczyk.maciej.GdzieBylem.R;
import pl.krawczyk.maciej.GdzieBylem.datasource.LocationsDataSource;
import pl.krawczyk.maciej.GdzieBylem.datasource.MyLocation;

import java.sql.SQLException;

public class NewLocationActivity extends BaseActivity implements View.OnClickListener, LocationListener, View.OnKeyListener {

    public static final LatLng LODZ = new LatLng(51.759798, 19.461584);
    private static final String SHAREDPREFSSETTINGS = "SettingsPref";
    private static final String CURRENT_LOCATION_LAT = "currentLocationLat";
    private static final String CURRENT_LOCATION_LNG = "currentLocationLng";
    private static final int LOCATION_REFRESH_INTERVAL_TIME = 50 * 60000;
    private static final int LOCATION_REFRESH_DELTA = 15000;
    private static final int CREATE_MODE = 0;
    private static final int EDIT_MODE = 1;

    private EditText editTextLocationName;
    private Button buttonSaveLocation;
    private LocationManager locationManager;
    private String provider;
    private LatLng currentLocation = null;
    private GoogleMap googleMap;
    private LocationsDataSource locationsDataSource;
    private int LAUNCH_MODE = CREATE_MODE;
    private long idOfLocation;

    private void saveLocationInDataBase(){
        MyLocation myLocation = new MyLocation();
        myLocation.setLat(currentLocation.latitude);
        myLocation.setLng(currentLocation.longitude);
        myLocation.setName(editTextLocationName.getText().toString());

        locationsDataSource.createLocation(myLocation);
        Toast.makeText(this, getString(R.string.location_saved), Toast.LENGTH_LONG).show();
        locationsDataSource.close();

        finish();
    }

    public void editLocationInDataBase(){
        locationsDataSource.editLocation(idOfLocation, editTextLocationName.getText().toString());
        Toast.makeText(this, getText(R.string.location_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setCurrentLocationOnMap(LatLng location, boolean withMarker) {

        MapsInitializer.initialize(this);

        if (googleMap != null){
            googleMap.clear();
        }

        if (withMarker) {
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(getString(R.string.your_location)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
    }

    private void saveCurrentLocation(LatLng currentLocation) {
        SharedPreferences settings = this.getSharedPreferences(SHAREDPREFSSETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(CURRENT_LOCATION_LAT, (float) currentLocation.latitude).apply();
        editor.putFloat(CURRENT_LOCATION_LNG, (float) currentLocation.longitude).apply();
    }

    private LatLng getCurrentLocationFromSPREF() {
        SharedPreferences settings = this.getSharedPreferences(SHAREDPREFSSETTINGS, MODE_PRIVATE);
        double latitude = settings.getFloat(CURRENT_LOCATION_LAT, 0);
        double longitude = settings.getFloat(CURRENT_LOCATION_LNG, 0);
        return new LatLng(latitude, longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        buttonSaveLocation = (Button) findViewById(R.id.buttonSaveLocation);
        editTextLocationName = (EditText) findViewById(R.id.editTextLocationName);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap))
                .getMap();

        locationsDataSource = new LocationsDataSource(this);

        try {
            locationsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                idOfLocation = b.getLong(MainActivity.LOCATION_ID);
                MyLocation myLocation = locationsDataSource.getLocationWithId(idOfLocation);
                double latitude = myLocation.getLat();
                double longitude = myLocation.getLng();
                currentLocation = new LatLng(latitude, longitude);
                editTextLocationName.setText(myLocation.getName());

                LAUNCH_MODE = EDIT_MODE;
            }
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(provider, LOCATION_REFRESH_INTERVAL_TIME, LOCATION_REFRESH_DELTA, this);

        if (LAUNCH_MODE == CREATE_MODE && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(getString(R.string.turn_on_location_label));

            alertDialogBuilder
                    .setMessage(R.string.turn_on_location_description)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.turn_on), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (getCurrentLocationFromSPREF() != null) {
            currentLocation = getCurrentLocationFromSPREF();
            if (currentLocation != null && currentLocation.longitude != 0) {
                setCurrentLocationOnMap(currentLocation, true);
            } else {
                setCurrentLocationOnMap(LODZ, false);
            }
        }

        buttonSaveLocation.setOnClickListener(this);
        editTextLocationName.setOnKeyListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonSaveLocation.getId()) {
            if (LAUNCH_MODE == CREATE_MODE) {
                if (editTextLocationName != null && !editTextLocationName.getText().toString().equals("")) {
                    if (currentLocation != null && currentLocation.latitude != 0) {
                        saveLocationInDataBase();
                    } else {
                        Toast.makeText(this, getString(R.string.no_current_location), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_location_name), Toast.LENGTH_LONG).show();
                }
            } else if (LAUNCH_MODE == EDIT_MODE) {
                if (editTextLocationName != null) {
                    if (!editTextLocationName.getText().toString().equals("")) {
                        editLocationInDataBase();
                    } else {
                        Toast.makeText(this, getText(R.string.no_location_name), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurrentLocationFromSPREF() != null) {
            currentLocation = getCurrentLocationFromSPREF();
            if (currentLocation.longitude != 0) {
                setCurrentLocationOnMap(currentLocation, true);
            }
        } else if (locationManager != null) {
            locationManager.requestLocationUpdates(provider, LOCATION_REFRESH_INTERVAL_TIME, LOCATION_REFRESH_DELTA, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentLocation != null) {
            saveCurrentLocation(currentLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (LAUNCH_MODE == CREATE_MODE) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            currentLocation = new LatLng(latitude, longitude);
            setCurrentLocationOnMap(currentLocation, true);
            saveCurrentLocation(currentLocation);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        if (LAUNCH_MODE == CREATE_MODE) {
            setCurrentLocationOnMap(currentLocation, true);
        }
    }

    @Override
    public void onProviderEnabled(String s) {
        buttonSaveLocation.setEnabled(true);
    }

    @Override
    public void onProviderDisabled(String s) {
        if (LAUNCH_MODE == CREATE_MODE) {
            Toast.makeText(this, getString(R.string.gps_is_off), Toast.LENGTH_LONG).show();
            buttonSaveLocation.setEnabled(false);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (LAUNCH_MODE == CREATE_MODE){
                        saveLocationInDataBase();
                    } else if (LAUNCH_MODE == EDIT_MODE){
                        editLocationInDataBase();
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}