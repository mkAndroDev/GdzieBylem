package pl.krawczyk.maciej.GdzieBylem.datasource;

public class MyLocation {

    private long id;
    private double lat;
    private double lng;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat(){
        return lat;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLng(){
        return lng;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}