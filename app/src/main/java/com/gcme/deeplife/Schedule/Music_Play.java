package com.gcme.deeplife.Schedule;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gcme.deeplife.Database.DeepLife;
import com.gcme.deeplife.Models.Schedule;
import com.gcme.deeplife.R;

/**
 * Created by Roger on 4/3/2016.
 */

public class Music_Play extends Activity {

    Button btn_stop;
    TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        this.setFinishOnTouchOutside(false);

        Long rowId = this.getIntent().getExtras().getLong(DeepLife.SCHEDULES_COLUMN[0]);

        btn_stop = (Button) findViewById(R.id.music_play);
        tv_title = (TextView) findViewById(R.id.tv_music_play);

        Schedule schedule = com.gcme.deeplife.DeepLife.myDatabase.getScheduleWithId(rowId+"");
        tv_title.setText(schedule.getTitle());

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.jadhiel);
        mediaPlayer.start();

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });

    }
}
