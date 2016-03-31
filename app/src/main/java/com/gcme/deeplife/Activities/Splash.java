package com.gcme.deeplife.Activities;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.gcme.deeplife.Database.Database;
import com.gcme.deeplife.Database.DeepLife;
import com.gcme.deeplife.R;


public class Splash extends Activity {

	Database myDatabase;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
		myDatabase = new Database(this);

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

        int dbcount = myDatabase.count(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST);
		if(dbcount<1){
			ContentValues questions1 = new ContentValues();
			questions1.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "WIN");
			questions1.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions1.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions1.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "1");

			ContentValues questions2 = new ContentValues();
			questions2.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "WIN");
			questions2.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions2.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions2.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "0");

			ContentValues questions3 = new ContentValues();
			questions3.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "BUILD");
			questions3.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions3.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions3.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "1");

			ContentValues questions4 = new ContentValues();
			questions4.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "BUILD");
			questions4.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions4.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions4.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "0");


			ContentValues questions5 = new ContentValues();
			questions5.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "SEND");
			questions5.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions5.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions5.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "1");

			ContentValues questions6 = new ContentValues();
			questions6.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[1], "SEND");
			questions6.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[2], "Have you stated that the question is the best int Have you stated that the question is the best int he world where the story ends?");
			questions6.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[3], "Have you stated that the dgsdgasdgas asdg");
			questions6.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_COLUMN[4], "0");

			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST, questions1);
			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST, questions2);
			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST, questions3);
			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST, questions4);
			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST, questions5);
			myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST,questions6);

            Log.i(DeepLife.TAG, "Questions added");
            myDatabase.dispose();
		}
        else{
            Log.i(DeepLife.TAG, "Questions not added");
            myDatabase.dispose();
        }

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
