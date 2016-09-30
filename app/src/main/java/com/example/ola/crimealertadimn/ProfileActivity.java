package com.example.ola.crimealertadimn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogOut;
    private Button buttonEvent;
    private Button viewEventsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView)findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome "+user.getEmail());
        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonEvent = (Button) findViewById(R.id.buttonEvent);
        viewEventsBtn = (Button) findViewById(R.id.view_events);

        buttonLogOut.setOnClickListener(this);
        buttonEvent.setOnClickListener(this);
        viewEventsBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view){
        if (view == buttonLogOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonEvent){
            //go to adding new event page
            // startActivity(new );



                //start the profile activity
                finish();
                startActivity(new Intent(this,eventInput.class));

        }
        if (view == viewEventsBtn){
            startActivity(new Intent(this,viewEventsOnMap.class));
            finish();
        }

    }

}
