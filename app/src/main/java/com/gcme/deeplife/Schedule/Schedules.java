package com.gcme.deeplife.Schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcme.deeplife.Database.DeepLife;
import com.gcme.deeplife.MainActivity;
import com.gcme.deeplife.Models.Disciples;
import com.gcme.deeplife.Models.Schedule;
import com.gcme.deeplife.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class Schedules extends Fragment {

	
	public ListView lv_schedule;
	Button addSchedule;

    //
    // Date Format
    //
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.schedule_list, container,
                false);
        lv_schedule = (ListView) view.findViewById(R.id.ls_schedule);
        populateList(getActivity());

        addSchedule = (Button) view.findViewById(R.id.bt_add_schedule);
        addSchedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), AddSchedule.class);
                startActivity(intent);
            }
		});

		setHasOptionsMenu(true);
    	return 	view;
		
	}

    public void populateList(Context context){
        ArrayList<Schedule> schedules = com.gcme.deeplife.DeepLife.myDatabase.get_All_Schedule();
        lv_schedule.setAdapter(new MyDiscipleListAdapter(context,schedules));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
	public void onPause() {
		// TODO Auto-generated method stub
	//	unregisterForContextMenu(newsView);
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
	//	registerForContextMenu(newsView);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);


	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
	       AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	        switch (item.getItemId()){
	            default:
	                return super.onContextItemSelected(item);

	        }

	}


    public void delete_Dialog(final int id) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        long deleted = com.gcme.deeplife.DeepLife.myDatabase.remove(DeepLife.Table_SCHEDULES,id);
                        if(deleted!=-1){
                            Toast.makeText(getActivity(),"Successfully Deleted",Toast.LENGTH_SHORT).show();
                            reload();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove Schedule ").setMessage("Are You sure you want to REMOVE this schedule?" )
                .setPositiveButton("Yes ", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }


    public void reload(){
        Intent intent = new Intent(this.getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    public class MyDiscipleListAdapter extends BaseAdapter
    {
        Context context;
        ArrayList<Schedule> schedule;
        public MyDiscipleListAdapter(Context context,ArrayList<Schedule> schedule)
        {
            this.context = context;
            this.schedule = schedule;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return schedule.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.schedule_item,null);


            TextView tv_name=(TextView)convertView.findViewById(R.id.schedule_with_name);
            TextView tv_disc=(TextView)convertView.findViewById(R.id.scheduledisciption);
            TextView tv_time=(TextView)convertView.findViewById(R.id.scheduletime);
            TextView tv_title=(TextView)convertView.findViewById(R.id.schedule_title);


            //final String name = schedule.get(position).ge;
            final String user_phone = schedule.get(position).getDisciple_Phone();
            final String time = schedule.get(position).getAlarm_Time();
            final String title = schedule.get(position).getTitle();
            final String discription = schedule.get(position).getDescription();
            final int id = Integer.parseInt(schedule.get(position).getID());

            Log.i(DeepLife.TAG,"Disciple phone " + user_phone);
            Disciples disciple = com.gcme.deeplife.DeepLife.myDatabase.getDiscipleProfileFromPhone(user_phone);

            //set the values
            tv_name.setText(disciple.getFull_Name());
            tv_time.setText(time);
            tv_disc.setText(discription);
            tv_title.setText(title);

            convertView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    delete_Dialog(id);
                    return true;
                }
            });


            return convertView;
        }
    }

	
}
