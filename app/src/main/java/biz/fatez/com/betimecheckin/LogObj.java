package biz.fatez.com.betimecheckin;

/**
 * Created by Fatez on 3/15/2016.
 */
public class LogObj {
    private String timestampId;
    private String userAd;
    private String timestamp;
    private String status;
    private String latlng;
    private String address;
    private String ip_address;


    public LogObj(String timestampId, String userAd,String timestamp, String status, String latlng, String address ,String ip_address) {
        this.timestampId = timestampId;
        this.userAd = userAd;
        this.timestamp = timestamp;
        this.status = status;
        this.latlng = latlng;
        this.address = address;
        this.ip_address = ip_address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampId() {
        return timestampId;
    }

    public void setTimestampId(String timestampId) {
        this.timestampId = timestampId;
    }

    public String getUserAd() {
        return userAd;
    }

    public void setUserAd(String userAd) {
        this.userAd = userAd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
