package biz.fatez.com.betimecheckin;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Fatez on 3/23/2016.
 */
public class Bean implements KvmSerializable {
    public String timeStamp_id;
    public String user_ad;
    public String timestamp;
    public String status;
    public String lat;
    public String lng;
    public String address;


    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLng() {
        return lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getTimeStamp_id() {
        return timeStamp_id;
    }
    public void setTimeStamp_id(String timeStamp_id) {
        this.timeStamp_id = timeStamp_id;
    }
    public String getUser_ad() {
        return user_ad;
    }
    public void setUser_ad(String user_ad) {
        this.user_ad = user_ad;
    }

    public Bean() {

    }

    public Bean(String timeStamp_id, String user_ad, String timestamp,
                String status, String lat, String lng, String address) {
        this.timeStamp_id = timeStamp_id;
        this.user_ad = user_ad;
        this.timestamp = timestamp;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.address = address;

    }


    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return timeStamp_id;
            case 1:
                return user_ad;
            case 2:
                return timestamp;
            case 3:
                return status;
            case 4:
                return lat;
            case 5:
                return lng;
            case 6:
                return address;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 7;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                timeStamp_id = value.toString();
                break;
            case 1:
                user_ad = value.toString();
                break;
            case 2:
                timestamp = value.toString();
                break;
            case 3:
                status = value.toString();
                break;
            case 4:
                lat = value.toString();
                break;
            case 5:
                lng = value.toString();
                break;
            case 6:
                address = value.toString();
                break;


            default:
                break;
        }


    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {

        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIMESTAMP_ID";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "USER_AD";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIMESTAMP";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STATUS";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LATITUDE";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LONGITUDE";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ADDRESS";
                break;

            default:
                break;
        }

    }
}