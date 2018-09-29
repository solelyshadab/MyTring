package in.mytring;
/**
 * Created by Prem on 2/10/2017.
 */

public class CellTowerData {
    private int mcc;
    private int mnc;
    private int lac_id;
    private long cell_id;
    private double longitude;
    private double latitude;
    private String radio_type;

    public CellTowerData (int mcc, int mnc, int lac_id, long cell_id, double longitude, double latitude, String radio_type){
        super();
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac_id = lac_id;
        this.cell_id = cell_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radio_type = radio_type;
    }


    public int getMcc() {
        return mcc;
    }
    public void setMcc(int mcc) {
        this.mcc = mcc;
    }
    public int getMnc() {
        return mnc;
    }
    public void setMnc(int mnc) {
        this.mnc = mnc;
    }
    public int getLac_id() {
        return lac_id;
    }
    public void setLac_id(int lac_id) {
        this.lac_id = lac_id;
    }
    public long getCell_id() {
        return cell_id;
    }
    public void setCell_id(long cell_id) {
        this.cell_id = cell_id;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getRadio_type() {
        return radio_type;
    }
    public void setRadio_type(String radio_type) {
        this.radio_type = radio_type;
    }


    public String toString() {
        return "CellTowerData [ MCC=" + mcc + " MNC=" + mnc + " LAC_ID=" + lac_id + " CELL_ID="+ cell_id + " Longitude=" + longitude + " Latitude=" + latitude
                + " Radio_Type=" + radio_type + "]" ;
    }


}
