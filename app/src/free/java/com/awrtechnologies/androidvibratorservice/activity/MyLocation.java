package com.awrtechnologies.androidvibratorservice.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.service.BackgroundLocationService;
import com.awrtechnologies.androidvibratorservice.utility.CheckNetwork;
import com.awrtechnologies.androidvibratorservice.utility.GeneralHelper;
import com.awrtechnologies.androidvibratorservice.utility.Utils;

public class MyLocation extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    TextView tv_dual_sim;
    TextView tv_imei_main;
    TextView tv_imei_secondary;
    TextView tv_is_sim_one_ready;
    TextView tv_is_sim_two_ready;
    TextView tv_network_name;
    TextView tv_mobile_number;
    TextView tv_battery_percent;
    TextView tv_date_time;
    TextView tv_location;
    TextView tv_location_accuracy;
    TextView tv_location_bearing;
    TextView tv_location_altitude;

    boolean isInternetPresent = false;
    Point point;

    private Button mStartUpdatesButton;
    private Button mPauseUpdatesButton;
    private Button buttonCamouflage;
    private Button buttonSetPin;
    private Button buttonStopUpdates;

    public static void setHideApplication(Context c, boolean hide) {
        ComponentName cn = new ComponentName(c.getApplicationContext(),
                Splash.class);
        int setting = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        int current = c.getPackageManager().getComponentEnabledSetting(cn);
        if (current != setting) {
            c.getPackageManager().setComponentEnabledSetting(cn, setting,
                    PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        findViews();

        applyClickListeners();
        mStartUpdatesButton.setEnabled(false);
        updateUI();
        try {
            registerReceiver(UiUpdated, new IntentFilter("LOCATION_UPDATED"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkForPermission();
    }

    public void findViews() {
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mPauseUpdatesButton = (Button) findViewById(R.id.pause_updates_button);
        tv_dual_sim = (TextView) findViewById(R.id.tv_dual_sim);
        tv_imei_main = (TextView) findViewById(R.id.tv_imei_main);
        tv_imei_secondary = (TextView) findViewById(R.id.tv_imei_secondary);
        tv_is_sim_one_ready = (TextView) findViewById(R.id.tv_is_sim_one_ready);
        tv_is_sim_two_ready = (TextView) findViewById(R.id.tv_is_sim_two_ready);
        tv_network_name = (TextView) findViewById(R.id.tv_network_name);
        tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_battery_percent = (TextView) findViewById(R.id.tv_battery_percent);
        tv_date_time = (TextView) findViewById(R.id.tv_date_time);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_location_accuracy = (TextView) findViewById(R.id.tv_location_accuracy);
        tv_location_bearing = (TextView) findViewById(R.id.tv_location_bearing);
        tv_location_altitude = (TextView) findViewById(R.id.tv_location_altitude);
        buttonCamouflage = (Button) findViewById(R.id.button_camouflage);
        buttonSetPin = (Button) findViewById(R.id.button_set_pin);
        buttonStopUpdates = (Button) findViewById(R.id.button_stop_updates);
    }

    public void applyClickListeners() {
        buttonCamouflage.setOnClickListener(this);
        mStartUpdatesButton.setOnClickListener(this);
        mPauseUpdatesButton.setOnClickListener(this);
        buttonSetPin.setOnClickListener(this);
        buttonStopUpdates.setOnClickListener(this);
    }

    private void updateUI() {
        if (Utils.isMyServiceRunning(this, BackgroundLocationService.class)) {
            mStartUpdatesButton.setEnabled(false);
            mPauseUpdatesButton.setEnabled(true);
            buttonStopUpdates.setEnabled(true);
            buttonCamouflage.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mPauseUpdatesButton.setEnabled(false);
            buttonCamouflage.setEnabled(false);
            buttonStopUpdates.setEnabled(false);
        }
    }

    private BroadcastReceiver UiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setUpdatedData(intent);
        }
    };

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String status = CheckNetwork.getConnectivityStatusString(context);
            if (status.equals("WIFI") || status.equals("MOBILE")) {
                isInternetPresent = true;
            } else if (status.equals("No Connection")) {
                isInternetPresent = false;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_updates_button:
                Intent intent = new Intent(MyLocation.this, BackgroundLocationService.class);
                startService(intent);
                updateUI();
                break;
            case R.id.pause_updates_button:
                Intent intent1 = new Intent(MyLocation.this, BackgroundLocationService.class);
                stopService(intent1);
                updateUI();
                break;
            case R.id.button_camouflage:
                if (checkIfAppIsHiddenMode()) {
                    onBackPressed();
                    Toast.makeText(MyLocation.this, "Hiding the app.", Toast.LENGTH_SHORT).show();
                } else {
                    setHideApplication(MyLocation.this, true);
                    Toast.makeText(MyLocation.this, "Please wait..!! This will take a few seconds.", Toast.LENGTH_SHORT).show();
                    setAppHiddenStatus(true);
                }
                break;
            case R.id.button_set_pin:
                dialogSetPin(MyLocation.this);
                break;
            case R.id.button_stop_updates:
                dialogStopUpdate(MyLocation.this);
                break;
        }
    }

    public void setUpdatedData(Intent intent) {
        tv_location.setText(intent.getExtras().getString("tvLocation"));
        tv_dual_sim.setText(intent.getExtras().getString("tv_dual_sim"));
        tv_imei_main.setText(intent.getExtras().getString("tv_imei_main"));
        tv_imei_secondary.setText(intent.getExtras().getString("tv_imei_secondary"));
        tv_is_sim_one_ready.setText(intent.getExtras().getString("tv_is_sim_one_ready"));
        tv_is_sim_two_ready.setText(intent.getExtras().getString("tv_is_sim_two_ready"));
        tv_network_name.setText(intent.getExtras().getString("tv_network_name"));
        tv_mobile_number.setText(intent.getExtras().getString("tv_mobile_number"));
        tv_battery_percent.setText(intent.getExtras().getString("tv_battery_percent"));
        tv_date_time.setText(intent.getExtras().getString("tv_date_time"));
        tv_location.setText(intent.getExtras().getString("tv_location"));
        tv_location_accuracy.setText(intent.getExtras().getString("tv_location_accuracy"));
        tv_location_bearing.setText(intent.getExtras().getString("tv_location_bearing"));
        tv_location_altitude.setText(intent.getExtras().getString("tv_location_altitude"));
    }

    public void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mStartUpdatesButton.setEnabled(true);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStartUpdatesButton.setEnabled(true);
                } else {
                }
                break;

            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mStartUpdatesButton.setEnabled(true);
                } else {
                }
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
            unregisterReceiver(UiUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(networkReceiver);
            unregisterReceiver(UiUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            updateUI();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogSetPin(Context context) {
        point = GeneralHelper.getInstance(this).getScreenSize();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_enter_pin);
        Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        final TextView tvPin = (TextView) dialog.findViewById(R.id.tv_pin);
        if (chechIfPinChanged()) {
            tvPin.setText(getMyPin());
        } else {
            tvPin.setText(getMyPin());
        }
        buttonCancel.setVisibility(View.VISIBLE);
        final EditText etPassWord = (EditText) dialog.findViewById(R.id.et_enter_password);
        etPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tvPin.setText(getMyPin());
                }
                if (s.length() >= 1) {
                    tvPin.setText("##" + etPassWord.getText().toString());
                }
            }
        });

        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassWord.getText().toString() != null || !etPassWord.getText().toString().isEmpty()) {
                    if (etPassWord.getText().length() != 4) {
                        Toast.makeText(MyLocation.this, "Enter at least 4 digits.", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        setUpPin(etPassWord.getText().toString());
                    }
                } else {
                    Toast.makeText(MyLocation.this, "Enter a pin.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyLocation.this, "You may change the pin later.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void dialogStopUpdate(Context context) {
        point = GeneralHelper.getInstance(this).getScreenSize();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);
        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent1 = new Intent(MyLocation.this, BackgroundLocationService.class);
                stopService(intent1);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("accessToken", "");
                editor.putString("ps_employee_id", "");
                editor.putString("id", "");
                editor.apply();
                Toast.makeText(MyLocation.this, "Auth cleared. Let's start again.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyLocation.this, BarcodeScanner.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyLocation.this, "You may stop updates later.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setLayout(point.x / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void setAppHiddenStatus(boolean isHidden) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isHidden", isHidden);
        editor.apply();
    }

    public void setUpPin(String pin) {
        String myPin = "##" + pin;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("myPin", myPin);
        editor.putBoolean("pinChanged", true);
        editor.apply();
    }

    public boolean checkIfAppIsHiddenMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
        boolean isHidden = preferences.getBoolean("isHidden", false);
        return isHidden;
    }

    public boolean chechIfPinChanged() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
        boolean isPinChanged = preferences.getBoolean("pinChanged", false);
        return isPinChanged;
    }

    public String getMyPin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyLocation.this);
        String myPin = preferences.getString("myPin", "##1111");
        return myPin;
    }

}
