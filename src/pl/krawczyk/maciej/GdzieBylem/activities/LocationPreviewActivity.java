package pl.krawczyk.maciej.GdzieBylem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import pl.krawczyk.maciej.GdzieBylem.R;
import pl.krawczyk.maciej.GdzieBylem.datasource.LocationsDataSource;
import pl.krawczyk.maciej.GdzieBylem.datasource.MyLocation;

import java.sql.SQLException;

public class LocationPreviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_preview);

        TextView textViewLocationName = (TextView)findViewById(R.id.textViewLocationName);
        GoogleMap googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();

        LocationsDataSource locationsDataSource = new LocationsDataSource(this);

        try {
            locationsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent != null){
            Bundle b = intent.getExtras();
            if (b != null){
                long idOfLocation = b.getLong(MainActivity.LOCATION_ID);
                MyLocation myLocation = locationsDataSource.getLocationWithId(idOfLocation);
                LatLng latLngOfLocation = new LatLng(myLocation.getLat(), myLocation.getLng());
                textViewLocationName.setText(myLocation.getName());

                googleMap.addMarker(new MarkerOptions()
                    .title(textViewLocationName.getText().toString())
                    .position(latLngOfLocation));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfLocation, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                locationsDataSource.close();
            } else {
                locationsDataSource.close();
                finish();
            }
        }

    }
}