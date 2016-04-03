package com.gcme.deeplife.SyncService;

import android.content.ContentValues;
import android.util.Log;

import com.gcme.deeplife.DeepLife;
import com.gcme.deeplife.Models.Disciples;
import com.gcme.deeplife.Models.Logs;
import com.gcme.deeplife.Models.Schedule;
import com.gcme.deeplife.Models.User;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by BENGEOS on 3/27/16.
 */
public class SyncService extends JobService {
    private static final String TAG = "SyncService";
    public static final String[] Sync_Tasks = {"Send_Log", "Send_Disciples","Remove_Disciple","Update_Disciple","Send_Schedule"};
    private List<Object> Param;
    private Gson myParser;
    private List<kotlin.Pair<String,String>> Send_Param;
    private User user;
    public SyncService(){
        Param = new ArrayList<Object>();
        myParser = new Gson();
    }
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("JobService", "The Job scheduler started");
        user = DeepLife.myDatabase.getUser();
        Param.add(DeepLife.myDatabase.getUser());
        Send_Param = new ArrayList<kotlin.Pair<String,String>>();
        if(user != null ){
            Send_Param.add(new kotlin.Pair<String, String>("User_Name",user.getUser_Name()));
            Send_Param.add(new kotlin.Pair<String, String>("User_Pass",user.getUser_Pass()));
            Send_Param.add(new kotlin.Pair<String, String>("Service",getService()));
            Send_Param.add(new kotlin.Pair<String, String>("Param",myParser.toJson(getParam())));
        }
        Fuel.post(DeepLife.API_URL, Send_Param).responseString(new Handler<String>() {
            @Override
            public void success(Request request, Response response, String s) {
                Log.i(TAG, "Request: \n" + request);
                Log.i(TAG, "Response: \n" + s);
                Gson myGson = new Gson();
                try {
                    JSONObject myObject = (JSONObject) new JSONTokener(s).nextValue();
                    Log.i(TAG,"Server Response -> \n"+myObject.toString());
                    if(!myObject.isNull("Response")){
                        JSONObject json_response = myObject.getJSONObject("Response");
                        Log.i(TAG,"Server  Response JSON OBJECT -> \n"+json_response.toString()+"---->"+!json_response.isNull("Confirmed_Logs"));
                        if(!json_response.isNull("Disciples")){
                            JSONArray json_Disciples = json_response.getJSONArray("Disciples");
                            Add_Disciple(json_Disciples);
                        }
                        if(!json_response.isNull("Schedules")){
                            JSONArray json_schedules = json_response.getJSONArray("Schedules");
                            Add_Schedule(json_schedules);
                        }
                        if(!json_response.isNull("Questions")){
                            JSONArray json_questions = json_response.getJSONArray("Questions");
                            Add_Qustions(json_questions);
                        }
                        if(!json_response.isNull("Reports")){
                            JSONArray json_questions = json_response.getJSONArray("Reports");
                            Add_Report_Forms(json_questions);
                        }
                        if(!json_response.isNull("Log_Response")){
                            JSONArray json_logs = json_response.getJSONArray("Log_Response");
                            Delete_Logs(json_logs);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(Request request, Response response, FuelError fuelError) {
                Log.i(TAG, "Error: \n" + fuelError);
            }
        });
        jobFinished(params, false);
        return false;
    }

    private static void Delete_Logs(JSONArray json_logs) {
        try{
            Log.i(TAG,"Deleting Confirmed Logs -> \n");
            Log.i(TAG,"Found  -> "+json_logs.length()+"   ->"+json_logs.toString());
            if(json_logs.length()>0){
                for(int i=0;i<json_logs.length();i++){
                    JSONObject obj = json_logs.getJSONObject(i);
                    Log.i(TAG, "Deleting  -> Logs: " + obj.toString());
                    int id = Integer.valueOf(obj.getString("Log_ID"));
                    Log.i(TAG, "Deleting -> LogID: " + id);
                    if(id>0){
                        long val = DeepLife.myDatabase.remove(com.gcme.deeplife.Database.DeepLife.Table_LOGS, id);
                        Log.i(TAG, "Deleting -> LogID: " + id+" :-> "+val);
                    }
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public String getService(){
        Log.i(TAG,"Found SendLogs -> "+DeepLife.myDatabase.getSendLogs().size());
        Log.i(TAG,"Found SendDisciple -> "+DeepLife.myDatabase.getSendDisciples().size());
        Log.i(TAG,"Found UpdateDisciples -> "+DeepLife.myDatabase.getUpdateDisciples().size());
        Log.i(TAG,"Found SendSchedule -> "+DeepLife.myDatabase.getSendSchedules().size());

        if(DeepLife.myDatabase.getSendLogs().size()>0){
            Log.i(TAG,"Found SendLogs Service -> "+DeepLife.myDatabase.getSendLogs().size());
            return "Send_Log";
        }else if(DeepLife.myDatabase.getSendDisciples().size()>0){
            Log.i(TAG,"Found SendDisciple Service -> "+DeepLife.myDatabase.getSendDisciples().size());
            return "AddNew_Disciples";
        }else if(DeepLife.myDatabase.getUpdateDisciples().size()>0){
            Log.i(TAG,"Found UpdateDisciples Service -> "+DeepLife.myDatabase.getUpdateDisciples().size());
            return "Update_Disciples";
        }else if(DeepLife.myDatabase.getSendSchedules().size()>0){
            Log.i(TAG,"Found SendSchedule Service -> "+DeepLife.myDatabase.getSendSchedules().size());
            return "AddNew_Schedules";
        }else{
            return "Update";
        }
    }
    public ArrayList<Object> getParam(){
        ArrayList<Object> Found = new ArrayList<Object>();
        if(DeepLife.myDatabase.getSendLogs().size()>0){
            Log.i(TAG,"GET LOG TO SEND -> \n");
            ArrayList<Logs> foundData = DeepLife.myDatabase.getSendLogs();
            for(int i=0;i<foundData.size();i++){
                Found.add(foundData.get(i));
            }
        }else if(DeepLife.myDatabase.getSendDisciples().size()>0){
            Log.i(TAG,"GET DISCIPLES TO SEND -> \n");
            ArrayList<Disciples> foundData = DeepLife.myDatabase.getSendDisciples();
            for(int i=0;i<foundData.size();i++){
                Found.add(foundData.get(i));
            }
        }else if(DeepLife.myDatabase.getUpdateDisciples().size()>0){
            Log.i(TAG,"GET DISCIPLES TO UPDATE -> \n");
            ArrayList<Disciples> foundData = DeepLife.myDatabase.getUpdateDisciples();
            for(int i=0;i<foundData.size();i++){
                Found.add(foundData.get(i));
            }
        }else if(DeepLife.myDatabase.getSendSchedules().size()>0){
            Log.i(TAG,"GET Schedules TO Send -> \n");
            ArrayList<Schedule> foundData = DeepLife.myDatabase.getSendSchedules();
            for(int i=0;i<foundData.size();i++){
                Found.add(foundData.get(i));
            }
        }
        return Found;
    }

    public static void Add_Disciple(JSONArray json_Disciples){
        try{
            if(json_Disciples.length()>0){
                Log.i(TAG,"Adding New Disciples -> \n"+json_Disciples.toString());
                for(int i=0;i<json_Disciples.length();i++){
                    JSONObject obj = json_Disciples.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[0], obj.getString("displayName"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[1], obj.getString("email"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[2], obj.getString("phone_no"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[3], obj.getString("country"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[4], obj.getString("stage"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.DISCIPLES_FIELDS[5], obj.getString("gender"));
                    long x = DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_DISCIPLES,cv);
                    if(x>0){
                        Log.i(TAG,"Adding Disciple Log -> \n");
                        ContentValues log = new ContentValues();
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[0],"Disciple");
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[1],Sync_Tasks[0]);
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[2],obj.getString("id"));
                        DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_LOGS,log);
                    }
                }
            }
        }catch (Exception e){

        }

    }
    public static void Add_Schedule(JSONArray json_schedules){
        try{
            if(json_schedules.length()>0){
                Log.i(TAG,"Adding New Schedules -> \n"+json_schedules.toString());
                for(int i=0;i<json_schedules.length();i++){
                    JSONObject obj = json_schedules.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(com.gcme.deeplife.Database.DeepLife.SCHEDULES_FIELDS[0], obj.getString("disciple_phone"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.SCHEDULES_FIELDS[1], obj.getString("name"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.SCHEDULES_FIELDS[2], obj.getString("time"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.SCHEDULES_FIELDS[3], obj.getString("type"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.SCHEDULES_FIELDS[4], obj.getString("description"));

                    long x = DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_SCHEDULES,cv);
                    if(x>0){
                        Log.i(TAG,"Adding Schedule Log -> \n");
                        ContentValues log = new ContentValues();
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[0],"Schedule");
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[1],Sync_Tasks[0]);
                        log.put(com.gcme.deeplife.Database.DeepLife.LOGS_FIELDS[2],obj.getString("id"));
                        DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_LOGS, log);
                    }
                }
            }
        }catch (Exception e){

        }
    }
    public static void Add_Qustions(JSONArray json_questions){
        try{
            if(json_questions.length()>0){
                Log.i(TAG,"Adding New Qustions -> \n"+json_questions.toString());
                DeepLife.myDatabase.Delete_All(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST);
                for(int i=0;i<json_questions.length();i++){
                    JSONObject obj = json_questions.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_FIELDS[0], obj.getString("category"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_FIELDS[1], obj.getString("question"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_FIELDS[2], obj.getString("description"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.QUESTION_LIST_FIELDS[3], obj.getString("mandatory"));
                    long x = DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_QUESTION_LIST,cv);
                    Log.i(TAG,"Adding Qustions -> "+obj.getString("id")+" : "+x);
                }
            }
        }catch (Exception e){

        }
    }
    public static void Add_Report_Forms(JSONArray json_questions){
        try{
            if(json_questions.length()>0){
                Log.i(TAG,"Adding New Reports -> \n"+json_questions.toString());
                DeepLife.myDatabase.Delete_All(com.gcme.deeplife.Database.DeepLife.Table_Report_Forms);
                for(int i=0;i<json_questions.length();i++){
                    JSONObject obj = json_questions.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(com.gcme.deeplife.Database.DeepLife.REPORT_FORM_FIELDS[0], obj.getString("id"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.REPORT_FORM_FIELDS[1], obj.getString("category"));
                    cv.put(com.gcme.deeplife.Database.DeepLife.REPORT_FORM_FIELDS[2], obj.getString("question"));
                    long x = DeepLife.myDatabase.insert(com.gcme.deeplife.Database.DeepLife.Table_Report_Forms,cv);
                    Log.i(TAG,"Adding Report -> "+obj.getString("id")+" : "+x);
                }
            }
        }catch (Exception e){

        }
    }
}
