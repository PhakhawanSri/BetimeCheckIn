package biz.fatez.com.betimecheckin;

/**
 * Created by Fatez on 4/29/2016.
 */

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalendarViewActivity extends AppCompatActivity implements CalendarPickerController {

    NodeList nodelist;
    ProgressDialog pDialog;
    // Insert image URL
    String URL = "http://intranet.betimes.biz/btcheckin_demo/Svc/dataEventAll.ashx";

    ArrayList<String> strDate = new ArrayList<String>();
    ArrayList<String> eDate = new ArrayList<String>();
    ArrayList<String> atext = new ArrayList<String>();

   // ArrayList<Integer> startTime = new ArrayList<Integer>();
    ArrayList<Integer> startDay = new ArrayList<Integer>();
    ArrayList<Integer> startMonth = new ArrayList<Integer>();
    ArrayList<Integer> startYear= new ArrayList<Integer>();
    ArrayList<Integer> startHour= new ArrayList<Integer>();
    ArrayList<Integer> startSec= new ArrayList<Integer>();

    //ArrayList<Integer> endTime = new ArrayList<Integer>();
    ArrayList<Integer> endDay = new ArrayList<Integer>();
    ArrayList<Integer> endMonth = new ArrayList<Integer>();
    ArrayList<Integer> endYear= new ArrayList<Integer>();
    ArrayList<Integer> endHour= new ArrayList<Integer>();
    ArrayList<Integer> endSec= new ArrayList<Integer>();



    List<CalendarEvent> eventList = new ArrayList<>();

    Calendar minDate;
    Calendar maxDate;

    private static final String LOG_TAG = CalendarViewActivity.class.getSimpleName();

    @Bind(R.id.activity_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.agenda_calendar_view)
    AgendaCalendarView mAgendaCalendarView;

    // region Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendarview);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        new DownloadXML().execute(URL);

    }

    // endregion

    // region Interface - CalendarPickerController

    @Override
    public void onDaySelected(DayItem dayItem) {
        Log.d(LOG_TAG, String.format("Selected day: %s", dayItem));
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Log.d(LOG_TAG, String.format("Selected event: %s", event));
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }


    // getNode function
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    // DownloadXML AsyncTask
    class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(CalendarViewActivity.this);
            // Set progressbar title
            pDialog.setTitle("Loading data");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... Url) {

            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
                nodelist = doc.getElementsByTagName("event");

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void args) {

            for (int temp = 0; temp < nodelist.getLength(); temp++) {
                Node nNode = nodelist.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String startDate = getNode("start_date", eElement);
                    System.out.println(startDate);

                    String[] splitStrDate= startDate.split(" ");
                   // This to split date to put data to calendar
                    String strdatePart = splitStrDate[0];
                    String strtimePart = splitStrDate[1];

                    String[] splitStrTime = strtimePart.split(":");
                    String strHour = splitStrTime[0];
                    String strSec = splitStrTime[1];


                    int stHour = Integer.parseInt(strHour);
                    int stSec = Integer.parseInt(strSec);

                    String[] splitStrDatePart = strdatePart.split("-");
                    String strYear = splitStrDatePart[0];
                    int stYear = Integer.parseInt(strYear);
                    String strMonth = splitStrDatePart[1];
                    int stMonth = Integer.parseInt(strMonth);
                    String strDay = splitStrDatePart[2];
                    int stDay = Integer.parseInt(strDay);

                    startHour.add(stHour);
                    startSec.add(stSec);
                    startDay.add(stDay);
                    startMonth.add(stMonth);
                    startYear.add(stYear);
                    strDate.add(startDate);

                    String endDate = getNode("end_date", eElement);
                    //  System.out.println(endDate);

                    String[] splitEDate= endDate.split(" ");
                    // This to split date to put data to calendar
                    String enddatePart = splitEDate[0];
                    String endtimePart = splitEDate[1];

                    String[] splitEndTime = endtimePart.split(":");
                    String eHour = splitEndTime[0];
                    String eSec = splitEndTime[1];


                    int enHour = Integer.parseInt(eHour);
                    int enSec = Integer.parseInt(eSec);


                    String[] endDateSplit = enddatePart.split("-");
                    String eYear = endDateSplit[0];
                    int enYear = Integer.parseInt(eYear);
                    String eMonth = endDateSplit[1];
                    int enMonth = Integer.parseInt(eMonth);
                    String eDay = endDateSplit[2];
                    int enDay = Integer.parseInt(eDay);

                    endHour.add(enHour);
                    endSec.add(enSec);
                    endDay.add(enDay);
                    endMonth.add(enMonth);
                    endYear.add(enYear);
                    eDate.add(endDate);

                    String description = getNode("text", eElement);
                    // System.out.println(description);
                    atext.add(description);

                }
            }

            for(int i = 0; i<atext.size(); i++) {

                Random random = new Random();
                int randomColor = random.nextInt(5);

                Calendar startTime1 = Calendar.getInstance();
                startTime1.set(Calendar.YEAR, startYear.get(i));
                startTime1.set(Calendar.MONTH, startMonth.get(i) - 1);
                startTime1.set(Calendar.DATE, startDay.get(i));
                Calendar endTime1 = Calendar.getInstance();
                endTime1.set(Calendar.YEAR, endYear.get(i));
                endTime1.set(Calendar.MONTH, endMonth.get(i) - 1);
                endTime1.set(Calendar.DATE, endDay.get(i));

                BaseCalendarEvent event1 = null;
                if(randomColor==0) {
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.orange_dark), startTime1, endTime1, true);
                }else if(randomColor==1){
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.yellow), startTime1, endTime1, true);
                }else if(randomColor==2){
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.pink), startTime1, endTime1, true);
                }else if(randomColor==3){
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.deep_purple), startTime1, endTime1, true);
                }else if(randomColor==4){
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.blue_dark), startTime1, endTime1, true);
                }else if(randomColor==5){
                    event1 = new BaseCalendarEvent(atext.get(i), "A wonderful journey!", (startHour.get(i) + "." + startSec.get(i) + "-" + endHour.get(i) + "." + endSec.get(i)),
                            ContextCompat.getColor(CalendarViewActivity.this, R.color.accent), startTime1, endTime1, true);
                } else{

                }
                eventList.add(event1);
            }
            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), CalendarViewActivity.this);
            mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());
            // Close progressbar
            pDialog.dismiss();
        }

    }


}

