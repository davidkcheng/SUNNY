package com.example.davidcheng.si606demo.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.estimote.sdk.internal.utils.L;
import com.example.davidcheng.si606demo.R;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.estimote.sdk.MacAddress;
import com.example.davidcheng.si606demo.adapters.ActionListAdapter;

import java.util.Timer;
import java.util.TimerTask;




public class MainActivity extends Activity {

    private static final Region[] BEACONS = new Region[] {
                new Region("home", null, 1681, 55777), //uuid without "-"
                new Region("work", null, 46280, 6350),
    };

//    List<Beacon> beacons_all;

    Beacon home, work;
    private int limit = 10000;
    private int tsec_home = 0;
    private int tsec_work = 0;

    private int currenttime_home = 0;
    private int currenttime_work = 0;

    double current_energy = 100.0;

    private BeaconManager beaconManager;
    protected TextView numberofbeacons;

    Timer timer_home;
    Timer timer_work;

    TextView hometime;
    TextView worktime;
    TextView location;

    boolean isSchedule_home = false;
    boolean isSchedule_work = false;

    private AlertDialog.Builder myAltDlg;

    SimpleAdapter simpleAdapter;
    Boolean hasDialog = false;
    int numBeacons = 0;
    boolean isGetEnergy = false;

    Boolean isOffice = true, isKitchen = true;

    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hometime = (TextView) findViewById(R.id.hometime);
        worktime = (TextView) findViewById(R.id.worktime);

        location = (TextView) findViewById(R.id.name);

        numberofbeacons = (TextView) findViewById(R.id.number);

//        timer_home = new Timer();
//        timer_work = new Timer();

//        numberofbeacons.setText(""+10);

//        timer_home.schedule(task_home, tsec_home,100);


//        dialogCreate();

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
//                        toolbar.setSubtitle("Found beacons: " + beacons.size());
                        Log.v("number", "" + beacons.size());
                        numberofbeacons.setText(""+beacons.size());
//                        Log.v("mac", "" + beacons.get(0).getMacAddress());
//                        Log.v("mac", "" + beacons.get(1).getMacAddress());
//                        adapter.replaceWith(beacons);

                        if (beacons != null && !beacons.isEmpty() && beacons.size() >= 2) {

                            for (Beacon beacon:beacons) {
//                                Log.v("beacon", beacon.toString());
//                                if (beacons.get(0).getMajor() == 1681) {
//                                    home = beacons.get(0);
//                                    work = beacons.get(1);
//                                }
//                                else {
//                                    home = beacons.get(1);
//                                    work = beacons.get(0);
//                                }
                                if (beacon.getMajor() == 1681) {
                                    home = beacon;
                                    numBeacons++;
                                }
                                if (beacon.getMajor() == 46280) {
                                    work = beacon;
                                    numBeacons++;
                                }
                            }


                            Utils.Proximity home_proximity = Utils.computeProximity(home);
                            Utils.Proximity work_proximity = Utils.computeProximity(work);

                            if (home_proximity == Utils.Proximity.IMMEDIATE) {
                                Log.v("test", "test");
                                location.setText("Kitchen room");
                                if (!isGetEnergy) {
                                    current_energy+=15;
                                    currenttime_work-=15*1000*2;
                                    tsec_work-=15*1000*2;
                                    if (current_energy <100) {
                                        worktime.setText(current_energy+"%");
                                    } else {
                                        worktime.setText("100%");
                                    }

                                    isGetEnergy = true;
                                }
                                isKitchen = true;


//                                Log.v("MSG", "At home");
//                                if (!isSchedule_home) {
//                                    Log.v("MAS", "not scheduled");
//                                    timer_home = new Timer();
//                                    home_Task task_home = new home_Task();
//                                    timer_home.schedule(task_home, 0, 100);
//                                    isSchedule_home = true;
//                                    Log.v("MAS", "now its scheduled");
//
//                                }
                            } else {
                                isKitchen = false;
//                                 location.setText("Unknown");
//                                Log.v("MSG", "leave home");
//                                if(timer_home != null) {
//                                    timer_home.cancel();
//                                }
//                                isSchedule_home = false;
                            }

                            if (work_proximity == Utils.Proximity.IMMEDIATE) {


                                Log.v("MSG", "At work");
                                location.setText("Office");

                                if (!isSchedule_work) {
                                    Log.v("MAS", "not scheduled");
                                    timer_work = new Timer();
                                    work_Task task_work = new work_Task();
                                    timer_work.schedule(task_work, 0, 100);
                                    isSchedule_work = true;
                                    isGetEnergy = false;
                                    hasDialog = false;
                                }
                                isOffice = true;

                            }
                            else {
                                Log.v("MSG", "leave work");
                                isOffice = false;
//                                location.setText("Unknown");
                                if (timer_work != null) {
                                    timer_work.cancel();
                                }
                                isSchedule_work = false;
                            }

                            if (!isKitchen && !isOffice) {
                                location.setText("Unknown");
                            }
                        }
                    }
                });
            }
        });

//        startMonitoring();

    }

    public void dialogCreate() {
        final String [] items = new String[] {"Take a coffee break with your colleagues", "Step outside and make the phone call you've been putting off", "Go to kitchen room to have some cookies and water"};

        myAltDlg =  new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View actionview = inflater.inflate(R.layout.view_action, null);
        ListView listview = (ListView) actionview.findViewById(R.id.action_list);
//        listview.setAdapter(new ArrayAdapter<String>(this, R.layout.action, items));
//        final int[] icons = new int[] {R.drawable.facebook, R.drawable.twitter};
//        View share_list = inflater.inflate(R.layout.view_sharedialog, null);

//        ListView listview = (ListView) findViewById(R.id.action_list);
        listview.setAdapter(new ActionListAdapter(MainActivity.this, R.layout.action, items));
//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.v("click", items[position]);
//                myAltDlg.
//            }
//        });

        myAltDlg.setView(actionview);
        myAltDlg.setCancelable(true);
        myAltDlg.show();
    }

    class home_Task extends TimerTask {
        public void run() {
            tsec_home += 100;
            currenttime_home += 100;
            Log.v("second_home", "" + tsec_home);
            Message message = new Message();
            handler.sendMessage(message);
        }
    }
    class work_Task extends TimerTask {
        public void run() {
            tsec_work += 100;
            currenttime_work += 100;
            Log.v("second_home", "" + tsec_work);
            Message message = new Message();
            handler.sendMessage(message);

        }
    }

    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg) {
            super.handleMessage(msg);
            current_energy = 100 - (double) (currenttime_work / 1000) / 2;
            Log.v("energy", ""+current_energy);
//            worktime.setText("" + Math.round(currenttime_work / 1000));
            if (current_energy < 99.9999) {
                worktime.setText(Double.toString(current_energy) + "%");
            }
            if (current_energy < 0) {
                worktime.setText("You are Exhausted!");
            }

//            hometime.setText("" + Math.round(currenttime_home / 1000));
            if ((current_energy < 90)  && (!hasDialog)) {
                dialogCreate();
                hasDialog = true;
            }

        }
    };


//    public TimerTask task_work = new TimerTask() {
//        @Override
//        public void run() {
//
//
//        }
//    };

//    public TimerTask task_home = new TimerTask() {
//        @Override
//        public void run() {
//            tsec_home += 100;
//            Log.v("second", "" + tsec_home);
////            hometime.setText("" + Math.round(tsec_home / 1000));
////            Message message = new Message();
////            message.what = 1;
////            handler.sendMessage(message);
////            if(tsec_home > limit) {
//////                Log.v("END", ""+tsec_home);
//////                timer_home.cancel();
////            }
//        }
//    };



    private void startMonitoring() {

            // Configure verbose debug logging.
            L.enableDebugLogging(true);

            /**
             * Scanning
             */

            Log.v("TAG","enter");
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);

            beaconManager.setRangingListener(new BeaconManager.RangingListener() {

                @Override
                public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
                    if (paramList != null && !paramList.isEmpty()) {
                        Beacon beacon = paramList.get(0);
                        Utils.Proximity proximity = Utils.computeProximity(beacon);
                        if (proximity == Utils.Proximity.IMMEDIATE) {
                            Log.d("TAG", "entered in region " + paramRegion.getProximityUUID());
//                            postNotification(paramRegion);
                        } else if (proximity == Utils.Proximity.FAR) {
                            Log.d("TAG", "exiting in region " + paramRegion.getProximityUUID());
//                            removeNotification(paramRegion);
                        }
                    }
                }

            });

            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        Log.d("TAG", "connected");
                        for (Region region : BEACONS) {
                            beaconManager.startRanging(region);
                        }
                    } catch (Exception e) {
                        Log.d("TAG", "Error while starting monitoring");
                    }
                }
            });
    }

    @Override protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }
    }

    @Override protected void onStop() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);

        super.onStop();
    }

    private void startScanning() {
//        toolbar.setSubtitle("Scanning...");
//        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        startMonitoring();
//        return START_STICKY;
//    }
//
//
//    private void startMonitoring() {
//        if (beaconManager == null) {
//            beaconManager = new BeaconManager(this);
//
//            // Configure verbose debug logging.
//            L.enableDebugLogging(true);
//
//            /**
//             * Scanning
//             */
//            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);
//
//            beaconManager.setRangingListener(new RangingListener() {
//
//                @Override
//                public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
//                    if (paramList != null && !paramList.isEmpty()) {
//                        Beacon beacon = paramList.get(0);
//                        Proximity proximity = Utils.computeProximity(beacon);
//                        if (proximity == Proximity.IMMEDIATE) {
//                            Log.d(TAG, "entered in region " + paramRegion.getProximityUUID());
//                            postNotification(paramRegion);
//                        } else if (proximity == Proximity.FAR) {
//                            Log.d(TAG, "exiting in region " + paramRegion.getProximityUUID());
//                            removeNotification(paramRegion);
//                        }
//                    }
//                }
//
//            });
//
//            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//                @Override
//                public void onServiceReady() {
//                    try {
//                        Log.d(TAG, "connected");
//                        for (Region region : BEACONS) {
//                            beaconManager.startRanging(region);
//                        }
//                    } catch (RemoteException e) {
//                        Log.d("TAG", "Error while starting monitoring");
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
