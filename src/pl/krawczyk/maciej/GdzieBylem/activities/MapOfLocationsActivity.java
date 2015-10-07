package pl.krawczyk.maciej.GdzieBylem.activities;

import android.os.Bundle;
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
import java.util.List;

public class MapOfLocationsActivity extends BaseActivity {

    private LocationsDataSource locationsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_of_locations);

        setItemMapVisible(false);

        GoogleMap googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMapForMapOFLocations)).getMap();

        MapsInitializer.initialize(this);

        locationsDataSource = new LocationsDataSource(this);

        List<MyLocation> listOfLocations = null;
        try {
            locationsDataSource.open();
            listOfLocations = locationsDataSource.getAllLocations();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (listOfLocations != null) {
            for (MyLocation listOfLocation : listOfLocations) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(listOfLocation.getLat(), listOfLocation.getLng()))
                        .title(listOfLocation.getName()));
            }
        }

        if (listOfLocations != null) {
            if (listOfLocations.size() == 0){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NewLocationActivity.LODZ, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(0), 2000, null);
            } else if (listOfLocations.size() == 1){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listOfLocations.get(0).getLat(), listOfLocations.get(0).getLng()), 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            } else if (listOfLocations.size() > 1){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listOfLocations.get(0).getLat(), listOfLocations.get(0).getLng()), 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(0), 2000, null);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationsDataSource.close();
    }
}