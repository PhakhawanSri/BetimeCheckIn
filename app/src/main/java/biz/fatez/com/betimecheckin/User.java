package biz.fatez.com.betimecheckin;

/**
 * Created by Fatez on 3/15/2016.
 */
public class User {
    private String user_name;
    private String user_email;
    private String lat_lng;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(String lat_lng) {
        this.lat_lng = lat_lng;
    }

    public User(String user_name, String user_email,String lat_lng, String time) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.lat_lng=lat_lng;
        this.time = time;

    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

}
