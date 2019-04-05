package com.example.myapplication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class pullactivity extends AppCompatActivity {

    FirebaseDatabase firebase_db;
    DatabaseReference db_ref;
    FirebaseAuth firebase_auth;
    FirebaseUser user = null;

    EditText stdidEditText;

    RecyclerView recView;
    RecyclerView.Adapter adapter;

    List<Grade> gradeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull);

        firebase_db = FirebaseDatabase.getInstance();
        db_ref = firebase_db.getReference();

        stdidEditText = (EditText) findViewById(R.id.stdidEditText);
        recView = (RecyclerView) findViewById(R.id.recView);

        firebase_auth = FirebaseAuth.getInstance();

        gradeList = new ArrayList<>();

        user = firebase_auth.getCurrentUser();
        if (user == null) {
            Intent intent = getIntent();
            final String uname = intent.getStringExtra("email");
            final String pswd = intent.getStringExtra("pswd");

            try {
                firebase_auth.signInWithEmailAndPassword(uname, pswd)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //we good
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error. Unable to maintain user", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            } catch (Exception e) {
                Log.d("NINI", "Signin error");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebase_auth.getCurrentUser();
    }

    public void callPush(View view) {
        Intent intent = new Intent(getApplicationContext(), PushActivity.class);
        startActivity(intent);
    }

    public void callSignout(View view) {
        firebase_auth.signOut();
        user = null;
        Toast.makeText(getApplicationContext(), "Error. Unable to maintain user", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void callQuery2(View view) {
        int studentID = 0;

        try {
            studentID = Integer.parseInt(stdidEditText.getText().toString());
        } catch (Exception e) {
            Log.d("NINI", "Unable to parse integer");
            return;
        }

        DatabaseReference gradeRef = db_ref.child("simpsons/grades/");
        Query query = gradeRef.orderByChild("student_id").startAt(studentID);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    public void callQuery1(View view) {
        int studentID = 0;

        try {
            studentID = Integer.parseInt(stdidEditText.getText().toString());
        } catch (Exception e) {
            Log.d("NINI", "Unable to parse integer");
            return;
        }

        DatabaseReference gradeRef = db_ref.child("simpsons/grades/");
        Query query = gradeRef.orderByChild("student_id").equalTo(studentID);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                Toast.makeText(getApplicationContext(),"listening",Toast.LENGTH_SHORT).show();
                gradeList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grade grade = snapshot.getValue(Grade.class);
                    gradeList.add(grade);
                }

                recView.setHasFixedSize(true);
                recView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new RecAdapter(gradeList, populateStudentIDMap(), getApplicationContext());
                recView.setAdapter(adapter);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("NINI", "Database error");
        }
    };

    public Map<Integer, String> populateStudentIDMap() {
        return new HashMap<Integer, String>() {
            {
                put(123, "Bart");
                put(404, "Ralph");
                put(456, "Milhouse");
                put(888, "Lisa");
            }
        };
    }
}