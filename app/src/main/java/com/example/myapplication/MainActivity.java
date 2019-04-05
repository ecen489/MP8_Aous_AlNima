package com.example.t18_firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase fbdb;
    DatabaseReference dbrf;
    int radioID = R.id.rad_ralph;
    int dbID = 404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbdb = FirebaseDatabase.getInstance();
        dbrf = fbdb.getReference();
    }

    public void radioClick(View view) {
        radioID = view.getId();
        if(radioID==R.id.rad_bart){dbID = 123;}
        if(radioID==R.id.rad_ralph){dbID = 404;}
        if(radioID==R.id.rad_milhouse){dbID = 456;}
        if(radioID==R.id.rad_lisa){dbID = 888;}
    }


    public void buttonClick(View view) {

        EditText edTxt= findViewById(R.id.edtxt);
        String pass = edTxt.getText().toString();
        edTxt.getText().clear();

        DatabaseReference passID = dbrf.child("simpsons/students/" + Integer.toString(dbID) + "/password");
        passID.setValue(pass);

        // DatabaseReference stud = dbrf.child("simpsons/students/999");
        // stud.child("email").setValue("abc@def.com");
    }

    public void buttonClick2(View view) {

        final TextView myTxt = findViewById(R.id.myTxt);

        DatabaseReference passID = dbrf.child("simpsons/students/"+ Integer.toString(dbID) + "/password");
        passID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                String pass = data.getValue().toString();
                myTxt.setText(pass);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //log error
            }
        });

    }
}