package domain;

public class DoAndSi {
    String name;
    double latitude;
    double lonitude;
    int zoomlevel;

    public DoAndSi(String name, double latitude, double lonitude, int zoomlevel){
        this.name = name;
        this.latitude = latitude;
        this.lonitude = lonitude;
        this.zoomlevel = zoomlevel;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLonitude() {
        return lonitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLonitude(double lonitude) {
        this.lonitude = lonitude;
    }

    public int getZoomlevel() {
        return zoomlevel;
    }

    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = zoomlevel;
    }
}
