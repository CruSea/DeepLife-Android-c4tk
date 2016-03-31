package com.gcme.deeplife.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.gcme.deeplife.R;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

				Thread splash = new Thread(){
        	@Override
        	public void run() {
        		try {
        			sleep(1000);       			
        		} catch(InterruptedException e){
        		} finally {
        			getNextActivity();
        		}
        		//super.run();
        	}
        };
        
        splash.start();
	}

	public synchronized void getNextActivity() {

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
