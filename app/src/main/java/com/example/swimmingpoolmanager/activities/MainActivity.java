package com.example.swimmingpoolmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swimmingpoolmanager.R;

public class MainActivity extends AppCompatActivity {

    private Button btnAddStudent, btnViewSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        btnAddStudent.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, StudentHubActivity.class)));

        btnViewSchedule.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ScheduleActivity.class)));
    }

    private void findViews() {
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnViewSchedule = findViewById(R.id.btnViewSchedule);
    }
}