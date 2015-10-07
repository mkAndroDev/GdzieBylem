package pl.krawczyk.maciej.GdzieBylem.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import pl.krawczyk.maciej.GdzieBylem.R;
import pl.krawczyk.maciej.GdzieBylem.datasource.MyLocation;
import pl.krawczyk.maciej.GdzieBylem.datasource.LocationsDataSource;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    public static final String LOCATION_ID = "locationId";

    private Button buttonAddLocation;
    private LocationsDataSource locationsDataSource;
    private ListView listViewOfLocations;
    private List<MyLocation> listOfMyLocations = null;

    private void setListOnListView(){
        listOfMyLocations = locationsDataSource.getAllLocations();

        if (listOfMyLocations.size() != 0){
            ArrayAdapter<MyLocation> adapter = new ArrayAdapter<MyLocation>(this,
                    android.R.layout.simple_list_item_1, listOfMyLocations);

            listViewOfLocations.setAdapter(adapter);
        }
    }

    private long getIdOfLocation(int positionOnListView){
        long idOfLocation = -1;
        if (listViewOfLocations != null){
            idOfLocation = listOfMyLocations.get(positionOnListView).getId();
        }
        return idOfLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        listViewOfLocations = (ListView) findViewById(R.id.listViewOfLocations);
        buttonAddLocation = (Button) findViewById(R.id.buttonAddLocation);

        locationsDataSource = new LocationsDataSource(this);
        try {
            locationsDataSource.open();
        } catch (SQLException e) {
            Toast.makeText(this, getText(R.string.data_base_error), Toast.LENGTH_SHORT).show();
        }

        setListOnListView();

        buttonAddLocation.setOnClickListener(this);
        listViewOfLocations.setOnItemLongClickListener(this);
        listViewOfLocations.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonAddLocation.getId()){
            Intent startNewLocationActivity = new Intent(getApplicationContext(), NewLocationActivity.class);
            startActivity(startNewLocationActivity);        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListOnListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationsDataSource.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent startViewNote = new Intent(this, LocationPreviewActivity.class);
        Bundle b = new Bundle();
        b.putLong(LOCATION_ID, getIdOfLocation(position));
        startViewNote.putExtras(b);
        startActivity(startViewNote);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, final long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.array_for_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent startViewNote = new Intent(MainActivity.this, NewLocationActivity.class);
                    Bundle b = new Bundle();
                    b.putLong(LOCATION_ID, getIdOfLocation(position));
                    startViewNote.putExtras(b);
                    startActivity(startViewNote);
                    dialog.dismiss();
                } else if (which == 1) {
                    locationsDataSource.deleteLocationWithId(getIdOfLocation(position));
                    Toast.makeText(MainActivity.this, getText(R.string.location_deleted), Toast.LENGTH_SHORT).show();
                    setListOnListView();
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return true;
    }
}