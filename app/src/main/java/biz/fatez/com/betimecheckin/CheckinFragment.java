package biz.fatez.com.betimecheckin;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class CheckinFragment extends Fragment {

    // latitude and longitude
    double latitude = 13.1;
    double longitude = 100.0;
    private GoogleMap googleMap;
    Marker mMarker;
    LocationManager lm;
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    String userAddress;
    TextView displayUserLocationTV;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {


            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (mMarker != null)
                mMarker.remove();

            // แปลงเพื่อเอาไปไว้ show
            String sLat = Double.toString(latitude);
            String sLng = Double.toString(longitude);

            List<Address> a = getAddress(coordinate);
            userAddress = a.get(0).getAddressLine(0).toString() + "-" + (getAddress(coordinate).get(0).getAddressLine(1) + " - " + getAddress(coordinate).get(0).getAddressLine(2)).toString();


            sp = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
            editor = sp.edit();
            editor.putString("spLatitude", sLat);
            editor.putString("spLongitude", sLng);
            editor.putString("spUserAddress", userAddress);
            editor.commit();

            mMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude)).title(sLat + "," + sLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    coordinate, 16));

            // display location user
            displayUserLocationTV = (TextView) getView().findViewById(R.id.location_text);
            displayUserLocationTV.setText(userAddress);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onResume() {
        super.onResume();

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

            if (isNetwork) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);
                Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                }
            }
        if (isGPS) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                latitude = loc.getLatitude();
                latitude = loc.getLongitude();
            }
        }
    }

    public List<Address> getAddress(LatLng point) {

        try {
            Geocoder geocoder;

            List<Address> addresses;
            geocoder = new Geocoder(getActivity());
            if (point.latitude != 0 || point.longitude != 0) {
                addresses = geocoder.getFromLocation(point.latitude,
                        point.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                System.out.println(address + " - " + city + " - " + country);

                return addresses;

            } else {
                Toast.makeText(getActivity(), "latitude and longitude are null",
                        Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void onPause() {
        super.onPause();


        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        lm.removeUpdates(listener);
    }
}
