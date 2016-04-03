package com.gcme.deeplife.Activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gcme.deeplife.DeepLife;
import com.gcme.deeplife.MainActivity;
import com.gcme.deeplife.R;
import com.gcme.deeplife.SyncService.SyncService;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;


public class Login extends AppCompatActivity{

    private static final String TAG = "Log_In";
    private  AlertDialog.Builder builder;

	private ProgressDialog pDialog;
	private Context myContext;

	EditText ed_phoneNumber, ed_password;
	Button bt_login, btn_register;
	private TextInputLayout inputLayoutPhone, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		init();

	}

	public void init(){

		myContext = this;
		ed_phoneNumber = (EditText) findViewById(R.id.login_phone);
		ed_password = (EditText) findViewById(R.id.login_password);
		bt_login = (Button) findViewById(R.id.btnLogin);
		btn_register = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        inputLayoutPhone = (TextInputLayout) findViewById(R.id.login_inputtxt_phone);
		inputLayoutPassword = (TextInputLayout) findViewById(R.id.login_inputtxt_password);

		ed_phoneNumber.addTextChangedListener(new MyTextWatcher(ed_phoneNumber));
		ed_password.addTextChangedListener(new MyTextWatcher(ed_phoneNumber));

		setButtonActions();
	}

	public void setButtonActions(){

		/*
				Button login works
		*/

		bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }


        });

		/*
				Button login works
		*/

		btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), Register.class);
                startActivity(register);
            }
        });

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


    private void submitForm() {

        if (!validatePhone()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        List<Pair<String, String>> Send_Param;
        final ProgressDialog myDialog = new ProgressDialog(this);
        myDialog.setTitle(R.string.app_name);
        myDialog.setMessage("Authenticating the Account ....");
        myDialog.show();
        Send_Param = new ArrayList<Pair<String, String>>();
        Send_Param.add(new kotlin.Pair<String, String>("User_Name", ed_phoneNumber.getText().toString()));
        Send_Param.add(new kotlin.Pair<String, String>("User_Pass", ed_password.getText().toString()));
        Send_Param.add(new kotlin.Pair<String, String>("Service", "Log_In"));
        Send_Param.add(new kotlin.Pair<String, String>("Param", "[]"));
        Fuel.post("http://192.168.0.24/SyncSMS/public/deep_api", Send_Param).responseString(new Handler<String>() {
            @Override
            public void success(Request request, Response response, String s) {
                myDialog.cancel();
                try {
                    JSONObject myObject = (JSONObject) new JSONTokener(s).nextValue();
                    Log.i(TAG, "Server Response -> \n" + myObject.toString());
                    if (!myObject.isNull("Response")) {

                        DeepLife.myDatabase.Delete_All(com.gcme.deeplife.Database.DeepLife.Table_DISCIPLES);
                        DeepLife.myDatabase.Delete_All(com.gcme.deeplife.Database.DeepLife.Table_SCHEDULES);
                        DeepLife.myDatabase.Delete_All(com.gcme.deeplife.Database.DeepLife.Table_LOGS);

                        JSONObject json_response = myObject.getJSONObject("Response");
                        if(!json_response.isNull("Disciples")){
                            JSONArray json_Disciples = json_response.getJSONArray("Disciples");
                            SyncService.Add_Disciple(json_Disciples);
                        }
                        if(!json_response.isNull("Schedules")){
                            JSONArray json_schedules = json_response.getJSONArray("Schedules");
                            SyncService.Add_Schedule(json_schedules);
                        }

                        Intent register = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(register);
                    } else {
                        ShowDialog("Invalid user account! use a valid account please");
                    }
                } catch (Exception e) {
                    ShowDialog("Something went wrong! Please try again \n"+e.toString());
                    Log.i(TAG, "Error Occurred-> \n" + e.toString());
                }

            }

            @Override
            public void failure(Request request, Response response, FuelError fuelError) {
                Log.i(TAG, "Server Response -> \n" + response.toString());
                myDialog.cancel();
                ShowDialog("Authentication has failed! Please try again");
            }
        });
    }
    public void ShowDialog(String message) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        break;
                }
            }
        };
        builder = new AlertDialog.Builder(myContext);
        builder.setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton("Ok", dialogClickListener).show();
    }
    private boolean validatePhone() {
        if (ed_phoneNumber.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(ed_phoneNumber);

            return false;
        }
        else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePassword() {
        if (ed_password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(ed_password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.login_phone:
                    validatePhone();
                    break;
                case R.id.login_password:
                    validatePassword();
                    break;
            }
        }
    }
}
