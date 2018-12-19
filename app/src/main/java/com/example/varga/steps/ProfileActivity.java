package com.example.varga.steps;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View.OnClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.Calendar;


import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,SensorEventListener, StepListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail,TvSteps,CurrentTime;
    private Button buttonLogout;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;// ide kell vissza olvasni

    private Button BtnStart,BtnStop;
    private String mUserID;
    private Date currentDate;

    //private FirebaseDatabase database;
    DatabaseReference mDatabaseReference,mDatabaseReference2;
    FirebaseDatabase mFirebasedatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebasedatabase = FirebaseDatabase.getInstance();
        mUserID = FirebaseAuth.getInstance().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);


        mDatabaseReference2= FirebaseDatabase.getInstance().getReferenceFromUrl("https://stepcounter-74584.firebaseio.com/");
        DatabaseReference mChild = mDatabaseReference2.child("Users").child("UserID").child(
        "StepsNum");




        //initializing current date

        currentDate = Calendar.getInstance().getTime();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();

        simpleStepDetector.registerListener(this);
        //intizializing buttons

        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        TvSteps = (TextView) findViewById(R.id.tv_steps);
        CurrentTime = (TextView) findViewById(R.id.current_time);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);


        //displaying logged in user name
        textViewUserEmail.setText("Welcome " + user.getEmail());
        //displaying current time
        CurrentTime.setText(currentDate.toString());
        //adding listener to button
        buttonLogout.setOnClickListener(this);





        BtnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                sensorManager.registerListener(ProfileActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });





        BtnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(ProfileActivity.this);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot stepssnapshot : dataSnapshot.getChildren()) {
                    Steps oldsteps = stepssnapshot.getValue(Steps.class);
                    System.out.println("StepsNume" + oldsteps.getStepsNum());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){
    }

    @Override
    public void onSensorChanged (SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step ( long timeNs) {
        numSteps++;
        currentDate = Calendar.getInstance().getTime();
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        Steps step = new Steps(mUserID,numSteps,currentDate);
        mDatabaseReference.child(mUserID).setValue(step);
        mDatabaseReference.child(mUserID).push();
    }


    @Override
    public void onClick (View view){
        //if logout is pressed
        if (view == buttonLogout) {
            //logging out the user
            firebaseAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //closing activity
            finish();
            //starting login activity
          //  startActivity(new Intent(this, LoginActivity.class));
        }
    }

}