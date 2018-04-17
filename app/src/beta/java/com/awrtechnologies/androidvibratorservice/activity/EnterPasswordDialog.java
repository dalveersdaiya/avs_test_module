package com.awrtechnologies.androidvibratorservice.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.awrtechnologies.androidvibratorservice.R;

/**
 * Created by dalveersinghdaiya on 01/06/17.
 */

public class EnterPasswordDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;

    EditText etPassword;
    TextInputLayout tilPassword;
    Button buttonOk;

    public EnterPasswordDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_enter_password);
        findViews ();
        applyClickListeners ();
    }

    public void findViews(){
        etPassword = (EditText)findViewById (R.id.et_enter_password);
        tilPassword = (TextInputLayout)findViewById (R.id.til_enter_previous_password);
        buttonOk = (Button)findViewById (R.id.button_ok);

    }

    public void applyClickListeners(){
        buttonOk.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                break;
            default:
                break;
        }
        dismiss();
    }
}