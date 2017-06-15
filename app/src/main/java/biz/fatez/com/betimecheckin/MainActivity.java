package biz.fatez.com.betimecheckin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;


import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener {
    // floating button
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;


    // SP auto login
    public static final String PREFS_NAME = "LoginPrefs";

    // Pager
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    MaterialTabHost tabHost;
    private Resources res;

    static boolean errored = false;
    int responeStatus;
    String userAddress;
    String lat;
    String lng;
    String username = null;

    ProgressDialog PD;
    SharedPreferences sp, sp2;
    SharedPreferences.Editor editor;

    int hour;
    int mils;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.menu1);
        floatingActionButton1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab12);
        floatingActionButton2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab22);
        floatingActionButton3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab32);



        // If username null set to Login page.
        SharedPreferences userAd = getSharedPreferences(PREFS_NAME, 0);
        username = userAd.getString("spUsername", "");
        if (username == null) {
            finish();
        }

        //System.out.print("Username :" + username);

        // get data from SP
        sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        userAddress = sp.getString("spUserAddress", "Location");
        lat = sp.getString("spLatitude", "Location");
        lng = sp.getString("spLongitude", "Location");


        System.out.print(userAddress);

        // Call current time
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        mils = c.get(Calendar.MINUTE);
        System.out.println("YEHE : " + hour);
        System.out.println("MIS : " + mils);

        final int HOUR_OF_DAY = 8;

        System.out.println("HOUR_OF_DAY :" + c.get(Calendar.HOUR_OF_DAY));
        System.out.println("HOUR : " + c.get(Calendar.HOUR));

         builder = new AlertDialog.Builder(this);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                builder.setTitle("CHECK IN")
                        .setMessage("Are you sure to Check in ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // check after 9 o'clocks check in late
                                if (hour > HOUR_OF_DAY) {
                                    Intent intent = new Intent(MainActivity.this, CheckinActivity.class);
                                    startActivity(intent);
                                    Toast toast = Toast.makeText(MainActivity.this, "You Check in late!", Toast.LENGTH_LONG);
                                    toast.show();
                                    finish();
                                    // Before 9 o'clocks check in
                                } else {
                                    AsyncCallCheckINWS task = new AsyncCallCheckINWS();
                                    task.execute();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();// create and show the alert dialog

            }
        });
        // check out button
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.setTitle("CHECK OUT")
                        .setMessage("Are you sure to Check out ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AsyncCallCheckOutWS task = new AsyncCallCheckOutWS();
                                task.execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();// create and show the alert dialog

            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("logged");
                editor.commit();
                finish();


            }
        });



        res = this.getResources();
        // init toolbar (old action bar)
        // Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        // toolbar.setTitleTextColor(Color.WHITE);
        // this.setSupportActionBar(toolbar);

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);
        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this)
            );
        }


    }

    // Call service checkin
    private class AsyncCallCheckINWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            //Call Web Method
            responeStatus = CheckinWS.invokeCheckinWS(username, lat, lng, userAddress, "SetUserCheckin");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            PD = new ProgressDialog(MainActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();

            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (responeStatus == 1) {
                    Toast toast = Toast.makeText(MainActivity.this, "Check In Complete!", Toast.LENGTH_LONG);
                    toast.show();
                    PD.dismiss();

                } else if (responeStatus == -1) {
                    //Set Error message
                    Toast toast2 = Toast.makeText(MainActivity.this, "Data incomplete!", Toast.LENGTH_LONG);
                    toast2.show();
                    PD.dismiss();
                } else {

                    Toast toast3 = Toast.makeText(MainActivity.this, "Failed, try again!", Toast.LENGTH_LONG);
                    toast3.show();
                    PD.dismiss();
                }
                //Error status is true
            } else {
                Toast toast4 = Toast.makeText(MainActivity.this, "Error occured in invoking webservice!", Toast.LENGTH_LONG);
                toast4.show();
                PD.dismiss();

            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    // Call check out service
    private class AsyncCallCheckOutWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            //Call Web Method
            responeStatus = CheckinWS.invokeCheckinWS(username, lat, lng, userAddress, "SetUserCheckout");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            PD = new ProgressDialog(MainActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();


            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (responeStatus == 1) {
                    Toast toast = Toast.makeText(MainActivity.this, "Check Out Complete!", Toast.LENGTH_LONG);
                    toast.show();
                    PD.dismiss();

                } else if (responeStatus == -1) {
                    //Set Error message
                    Toast toast2 = Toast.makeText(MainActivity.this, "Data incomplete!", Toast.LENGTH_LONG);
                    toast2.show();
                    PD.dismiss();
                } else {

                    Toast toast3 = Toast.makeText(MainActivity.this, "Failed, try again!", Toast.LENGTH_LONG);
                    toast3.show();
                    PD.dismiss();
                }
                //Error status is true
            } else {
                Toast toast4 = Toast.makeText(MainActivity.this, "Error occured in invoking webservice!", Toast.LENGTH_LONG);
                toast4.show();
                PD.dismiss();

            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    public void onTabSelected(MaterialTab tab) {
// when the tab is clicked the pager swipe content to the tab position
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if (position == 0)
                return new CheckinFragment();
            else if (position == 1)
                return new UserStatusFragment();

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "tab 1";
                case 1:
                    return "tab 2";
                default:
                    return null;
            }
        }
    }

    private Drawable getIcon(int position) {
        switch (position) {
            case 0:
                return res.getDrawable(R.drawable.ic_location_on_white_24dp);
            case 1:
                return res.getDrawable(R.drawable.ic_view_list_white_24dp);

        }
        return null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater Inflater = getMenuInflater();
        Inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        builder.setTitle("Logout")
                .setMessage("Are you sure to Log out ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("logged");
                        editor.commit();
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();// create and show the alert dialog

        return super.onKeyDown(keyCode, event);
    }
}
