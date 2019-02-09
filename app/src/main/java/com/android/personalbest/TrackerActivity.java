package com.android.personalbest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

public class TrackerActivity extends AppCompatActivity {
    Dialog myDialog;
    TrackTime timer;
    public boolean stopTimer = false;
    public TextView total_time;
    public TextView real_time;

    public TextView display_velocity;
    public TextView display_avg_velocity;

    public String display_total_steps;
    public int curr_step = 0;
    private double sum_velocity = 0;
    private double curr_velocity;
    private int curr_time = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tracker);
        myDialog = new Dialog(this);
        real_time = findViewById(R.id.time_elapsed);
        display_velocity = findViewById(R.id.velocity);

        timer = new TrackTime();
        timer.execute("0");

        // temp value
        curr_step = 9;

        ((TextView)findViewById(R.id.steps)).setText(Integer.toString(curr_step));

        Button exit = findViewById(R.id.exit_btn);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_total_steps = ((TextView)findViewById(R.id.steps)).getText().toString();
                ShowPopup(view);
            }
        });
    }

    public void ShowPopup(View v) {
        Button btnClose;
        myDialog.setContentView(R.layout.activity_summary);

        total_time = myDialog.findViewById(R.id.total_time);
        display_avg_velocity = myDialog.findViewById(R.id.avg_velocity);
        stopTimer = true;

        TextView total_steps = myDialog.findViewById(R.id.total_steps);
        total_steps.setText(display_total_steps);

        btnClose = myDialog.findViewById(R.id.back_to_home);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                launchActivity();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void launchActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("intentional_steps", Integer.toString(curr_step));
        startActivity(intent);
    }

    // class to track time elapsed
    private class TrackTime extends AsyncTask<String, String, String> {
        int min = 0;
        int sec = 0;
        String time;
        Random r = new Random();
        private DecimalFormat df = new DecimalFormat("#.00");

        @Override
        protected String doInBackground(String... params) {
            while (true) {
                try {
                    publishProgress(Integer.toString(curr_time));

                    // temp value
                    curr_velocity = r.nextDouble()*2;

                    Thread.sleep(1000);
                    curr_time ++;

                    // get the timer
                    min = curr_time / 60;
                    sec = curr_time % 60;
                    time = String.format("%d:%02d", min, sec);

                    // get the sum velocity
                    sum_velocity += curr_velocity;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(stopTimer){
                    return(time);
                }
                if(isCancelled()){break;}
            }
            return (time);
        }

        @Override
        protected void onPostExecute(String result) {
            total_time.setText(time);
            double avg_velocity = sum_velocity / curr_time;
            display_avg_velocity.setText(df.format(avg_velocity));
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... count) {
            real_time.setText(time);
            display_velocity.setText(df.format(curr_velocity));
        }
    }


}