package biz.fatez.com.betimecheckin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class UserStatusFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<User> users;
    private UsersAdapter adapter;

    boolean timeoutexcep = false, httpexcep = false, generalexcep = false;
    Bean[] personList2;

    String[] TimestampId, UserAd, Timestamp, Status,
            Latitude, Longitude, Address;

    String username;

    public static final String PREFS_NAME = "LoginPrefs";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_status, container, false);


        SharedPreferences userAd = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        username = userAd.getString("spUsername", "");

        new propdetail().execute();
        return rootView;

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        
    }

    class propdetail extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading data");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://intranet.betimes.biz/btcheckin/service.asmx";
            final String SOAP_ACTION = "http://tempuri.org/GetCheckinStatusByUser";
            final String METHOD_NAME = "GetCheckinStatusByUser";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


            request.addProperty("user_ad", username);

            Bean C = new Bean();
            PropertyInfo pi = new PropertyInfo();
            pi.setName("Bean");
            pi.setValue(C);
            pi.setType(C.getClass());
            request.addProperty(pi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Bean", new Bean().getClass());
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.debug = true;

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();

                Log.i("myApp", request.toString());
                System.out.println("check type : " + response);

                envelope.addMapping(NAMESPACE, "Fatez", new Bean().getClass());

                personList2 = new Bean[response.getPropertyCount()];
                users = new ArrayList<>();
                for (int j = 0; j < personList2.length; j++) {
                    SoapObject so = (SoapObject) response.getProperty(j);
                    Bean person2 = new Bean();
                    person2.timeStamp_id = so.getProperty(0).toString();
                    person2.user_ad = so.getProperty(1).toString();
                    String time = person2.timestamp = so.getProperty(2).toString();

                    // Split timedate format to divice time and day
                    String[] parts = time.split("T", 2);
                    String partDay = parts[0];
                    String partHour = parts[1];

                    // delete  "-"
                    String[] partDaySplit = partDay.split("[-]", 3);
                    String year = partDaySplit[0];
                    String month = partDaySplit[1];
                    String day = partDaySplit[2];

                    // change to พ.ศ.
                    int thaiyear = Integer.parseInt(year);
                    int sumyear = thaiyear + 543;

                    // Change number to Name of month
                    if (month.equals("01")) {
                        month = "ม.ค.";
                    } else if (month.equals("02")) {
                        month = "ก.พ.";
                    } else if (month.equals("03")) {
                        month = "มี.ค.";
                    } else if (month.equals("04")) {
                        month = "เม.ย.";
                    } else if (month.equals("05")) {
                        month = "พ.ค.";
                    } else if (month.equals("06")) {
                        month = "มิ.ย.";
                    } else if (month.equals("07")) {
                        month = "ก.ค.";
                    } else if (month.equals("08")) {
                        month = "ส.ค.";
                    } else if (month.equals("09")) {
                        month = "ก.ย.";
                    } else if (month.equals("10")) {
                        month = "ต.ค.";
                    } else if (month.equals("11")) {
                        month = "พ.ย.";
                    } else if (month.equals("12")) {
                        month = "ธ.ค.";
                    }

                    // Split time format
                    String[] spHour = partHour.split(":");
                    String hour = spHour[0];
                    String sec = spHour[1];
                    String mils = spHour[2];

              String chkStatus = person2.status = so.getProperty(3).toString();

                    if(chkStatus.equals("Check In")){
                        chkStatus = "IN";
                    }else {
                        chkStatus = "OUT";
                    }

                    String lat2 = person2.lat = so.getProperty(4).toString();
                    String lng2 = person2.lng = so.getProperty(5).toString();

                    double dblat = Double.parseDouble(lat2);
                    double dblng = Double.parseDouble(lng2);

                    // Do lat lag to 2 point
                    String latCut = String.format("%.2f", dblat);
                    String lngCut = String.format("%.2f", dblng);
                    person2.address = so.getProperty(6).toString();
                    personList2[j] = person2;
                    users.add(new User(chkStatus, person2.address, (day + " " + month + " " + sumyear + " " + hour + ":" + sec + " น."), (latCut + "," + lngCut)));
                }

                TimestampId = new String[personList2.length];
                UserAd = new String[personList2.length];
                Timestamp = new String[personList2.length];
                Status = new String[personList2.length];
                Latitude = new String[personList2.length];
                Longitude = new String[personList2.length];
                Address = new String[personList2.length];

                for (int i = 0; i < personList2.length; i++) {
                    TimestampId[i] = Arrays.asList(personList2[i].timeStamp_id).toString();
                    UserAd[i] = Arrays.asList(personList2[i].user_ad).toString();
                    Timestamp[i] = Arrays.asList(personList2[i].timestamp).toString();
                    Status[i] = Arrays.asList(personList2[i].status).toString();
                    Latitude[i] = Arrays.asList(personList2[i].lat).toString();
                    Longitude[i] = Arrays.asList(personList2[i].lng).toString();
                    Address[i] = Arrays.asList(personList2[i].address).toString();

                    // print
                    System.out.println("----------------------");
                    System.out.println(TimestampId[i]);
                    System.out.println(UserAd[i]);
                    System.out.println(Timestamp[i]);
                    System.out.println(Status[i]);
                    System.out.println(Latitude[i]);
                    System.out.println(Longitude[i]);
                    System.out.println(Address[i]);
                }

            } catch (SocketTimeoutException e) {
                timeoutexcep = true;
                e.printStackTrace();
            } catch (ConnectException e) {
                httpexcep = true;
                e.printStackTrace();
            } catch (Exception e) {
                generalexcep = true;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            if (timeoutexcep) {
                Toast.makeText(getActivity(), "Unable to connect to server, Please try again later", Toast.LENGTH_LONG).show();
            } else if (httpexcep) {
                Toast.makeText(getActivity(), "Please check your Internet connection", Toast.LENGTH_LONG).show();
            } else if (generalexcep) {
                Toast.makeText(getActivity(), "Please try later", Toast.LENGTH_LONG).show();
            } else {

                display();
            }
            timeoutexcep = false;
            httpexcep = false;
            generalexcep = false;
        }
    }

    public void display() {
        adapter = new UsersAdapter(users);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}



