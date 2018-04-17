package com.awrtechnologies.androidvibratorservice.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.api.callback.ApiCallBack;
import com.awrtechnologies.androidvibratorservice.api.caller.AuthCaller;
import com.awrtechnologies.androidvibratorservice.api.enums.ApiType;
import com.awrtechnologies.androidvibratorservice.api.params.AuthParam;
import com.awrtechnologies.androidvibratorservice.barcode.CameraSelectorDialogFragment;
import com.awrtechnologies.androidvibratorservice.barcode.FormatSelectorDialogFragment;
import com.awrtechnologies.androidvibratorservice.barcode.MessageDialogFragment;
import com.awrtechnologies.androidvibratorservice.utility.GeneralHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler, MessageDialogFragment.MessageDialogListener,
        FormatSelectorDialogFragment.FormatSelectorDialogListener, CameraSelectorDialogFragment.CameraSelectorDialogListener, ApiCallBack {


    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean (FLASH_STATE, false);
            mAutoFocus = savedInstanceState.getBoolean (AUTO_FOCUS_STATE, true);
            mSelectedIndices = savedInstanceState.getIntegerArrayList (SELECTED_FORMATS);
            mCameraId = savedInstanceState.getInt (CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }
//        hideNavigationBar ();
        setContentView (R.layout.activity_barcode_scanner);
        setupToolbar ();

        try {
            ViewGroup contentFrame = (ViewGroup) findViewById (R.id.content_frame);
            mScannerView = new ZXingScannerView (this);
            setupFormats ();
            contentFrame.addView (mScannerView);
        } catch (Exception e) {
            e.printStackTrace ();
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
        outState.putBoolean (FLASH_STATE, mFlash);
        outState.putBoolean (AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList (SELECTED_FORMATS, mSelectedIndices);
        outState.putInt (CAMERA_ID, mCameraId);
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        toolbar.setTitle ("");
        toolbar.setTitleTextColor (Color.parseColor ("#00000000"));
        final ActionBar ab = getSupportActionBar ();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled (true);
            ab.setDisplayShowHomeEnabled (true);
            ab.setDisplayUseLogoEnabled (true);
            ab.setDisplayShowTitleEnabled (false);
            toolbar.setNavigationOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    setExitDialog (BarcodeScanner.this);
                }
            });
        }
    }

    public void getDeviceInfo(final boolean showLogs) {
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run() {
                String deviceName = Build.MODEL;
                String deviceMan = Build.MANUFACTURER;
                String deviceBrand = Build.BRAND;
                String deviceBoard = Build.BOARD;

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (BarcodeScanner.this);
                String refreshedToken = preferences.getString ("RefreshedToken", null);

                if (showLogs) {
                    Log.d ("Daiya", "deviceName " + deviceName);//Model
                    Log.d ("Daiya", "deviceMan " + deviceMan);//Brand
                    Log.d ("Daiya", "deviceBrand " + deviceBrand);
                    Log.d ("Daiya", "deviceBoard " + deviceBoard);
                    Log.d ("Daiya", "Device Name " + getDeviceName ());
                    Log.d ("Daiya", "refreshedToken " + refreshedToken);
                }
            }
        }, 5000);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith (manufacturer)) {
            return capitalize (model);
        } else {
            return capitalize (manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length () == 0) {
            return "";
        }
        char first = s.charAt (0);
        if (Character.isUpperCase (first)) {
            return s;
        } else {
            return Character.toUpperCase (first) + s.substring (1);
        }
    }


//    //TODO : REMOVE THESE TWO METHODS IF NOT WORKING
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged (hasFocus);
//        if (hasFocus) {
//            new Handler ().postDelayed (new Runnable () {
//                @Override
//                public void run() {
//                    hideNavigationBar ();
//                }
//            }, 1500);
//        }
//
//    }

    public void hideNavigationBar() {
        final View decorView = this.getWindow ().getDecorView ();
        final int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        Timer timer = new Timer ();
        TimerTask task = new TimerTask () {
            @Override
            public void run() {
                BarcodeScanner.this.runOnUiThread (new Runnable () {
                    @Override
                    public void run() {
                        decorView.setSystemUiVisibility (uiOptions);

                    }
                });
            }
        };

        timer.scheduleAtFixedRate (task, 1, 2);

    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri (RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone (getApplicationContext (), notification);
            r.play ();
        } catch (Exception e) {
        }
        String result = rawResult.getText ();
        Log.d ("Daiya", "barcode result : " + result);
//        dialog (this, result);
        showMessageDialog ("Contents = " + rawResult.getText () + ", Format = " + rawResult.getBarcodeFormat ().toString ());

//        getPasscode(result);
    }


    //    To get the poasscode from the Encrypted String
    public String getPasscode(String currentString) {
        String[] separated = currentString.split (".");
        String myPasscode = separated[1];
        return myPasscode;
    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance ("Scan Results", message, this);
        fragment.show (getSupportFragmentManager (), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog ("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog ("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager ();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag (dialogName);
        if (fragment != null) {
            fragment.dismiss ();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview (this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats ();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera (mCameraId);
        mScannerView.setFlash (mFlash);
        mScannerView.setAutoFocus (mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat> ();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty ()) {
            mSelectedIndices = new ArrayList<Integer> ();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size (); i++) {
                mSelectedIndices.add (i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add (ZXingScannerView.ALL_FORMATS.get (index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats (formats);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (mFlash) {
            menuItem = menu.add (Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add (Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction (menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if (mAutoFocus) {
            menuItem = menu.add (Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add (Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction (menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add (Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction (menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add (Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction (menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu (menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId ()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle (R.string.flash_on);
                } else {
                    item.setTitle (R.string.flash_off);
                }
                mScannerView.setFlash (mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle (R.string.auto_focus_on);
                } else {
                    item.setTitle (R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus (mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance (this, mSelectedIndices);
                fragment.show (getSupportFragmentManager (), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera ();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance (this, mCameraId);
                cFragment.show (getSupportFragmentManager (), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }


    @Override
    public void onPause() {
        super.onPause ();
        try {
            mScannerView.stopCamera ();
            closeMessageDialog ();
            closeFormatsDialog ();
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume ();
        try {
//            hideNavigationBar ();
            mScannerView.setResultHandler (this);
            mScannerView.startCamera (mCameraId);
            mScannerView.setFlash (mFlash);
            mScannerView.setAutoFocus (mAutoFocus);
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }

    public void setExitDialog(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
        alertDialogBuilder.setMessage ("Exit App?");
        alertDialogBuilder.setPositiveButton ("yes",
                new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss ();
                        Intent intent = new Intent (Intent.ACTION_MAIN);
                        intent.addCategory (Intent.CATEGORY_HOME);
                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity (intent);
                    }
                });

        alertDialogBuilder.setNegativeButton ("No", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss ();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create ();
        alertDialog.show ();
    }

    public void getAuthFromApi(String access_code, String password) {
        AuthParam authParam = new AuthParam ();
        authParam.access_token_QR = access_code;
        authParam.password = password;
        AuthCaller.instance ().post (this, authParam, this, ApiType.AUTH);

    }


    @Override
    public void onResult(String result, ApiType apitype, int resultCode) {
        if (apitype == ApiType.AUTH) {
            Log.d ("Daiya", "Auth on Result : result code" + resultCode + " and result : " + result);
            if (resultCode == 200) {
                Intent intent = new Intent (BarcodeScanner.this, MyLocation.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (intent);
            } else {
                Toast.makeText (this, "Try again!!", Toast.LENGTH_LONG).show ();
                Intent i = new Intent (this, BarcodeScanner.class);
                i.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (i);
            }
        }
    }

    public void dialog(Context context, final String accessToken) {
        point = GeneralHelper.getInstance (this).getScreenSize ();
        final Dialog dialog = new Dialog (context);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setCancelable (false);
        dialog.setContentView (R.layout.dialog_enter_password);
        final EditText etPassWord = (EditText) dialog.findViewById (R.id.et_enter_password);
        etPassWord.requestFocus ();
        dialog.findViewById (R.id.button_ok).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                dialog.dismiss ();
                if (etPassWord.getText ().toString () != null || !etPassWord.getText ().toString ().isEmpty ()) {
//                    Intent intent = new Intent (BarcodeScanner.this, MyLocation.class);
//                    startActivity (intent);
                    dialog.dismiss ();
                    getAuthFromApi (accessToken, etPassWord.getText ().toString ());
                } else {
                    Toast.makeText (BarcodeScanner.this, "Enter password.", Toast.LENGTH_SHORT).show ();
                }

            }
        });

        dialog.show ();
        Window window = dialog.getWindow ();
        window.setBackgroundDrawableResource (R.color.transparent);
        window.setLayout (point.x / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
