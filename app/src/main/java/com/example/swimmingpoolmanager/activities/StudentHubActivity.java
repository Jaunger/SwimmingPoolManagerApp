package com.example.swimmingpoolmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swimmingpoolmanager.R;
import com.example.swimmingpoolmanager.adapters.StudentAdapter;
import com.example.swimmingpoolmanager.entities.Student;
import com.example.swimmingpoolmanager.network.ApiClient;
import com.example.swimmingpoolmanager.network.PoolManagerService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentHubActivity extends AppCompatActivity {
    private RecyclerView rvStudents;
    private FloatingActionButton fabAdd;
    private StudentAdapter adapter;
    private PoolManagerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_hub);

        rvStudents = findViewById(R.id.rvStudents);
        fabAdd      = findViewById(R.id.fabAdd);

        service = ApiClient
                .getClient("http://10.0.2.2:3000/api/")
                .create(PoolManagerService.class);

        adapter = new StudentAdapter(
                new ArrayList<>(),
                this::onEdit,
                this::onDelete
        );
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddStudentActivity.class))
        );

        loadStudents();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadStudents() {
        service.getAllStudents().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    List<Student> list = resp.body();
                    Log.d("StudentHub", "Fetched " + list.size() + " students");
                    adapter.setItems(list);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                Log.e("StudentHub", "Network failure", t);
                Toast.makeText(StudentHubActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onEdit(Student s) {
        Intent i = new Intent(this, AddStudentActivity.class);
        i.putExtra("studentId", s.get_id());
        startActivity(i);
    }

    private void onDelete(Student student) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Remove " + student.getFirstName() +
                        " " + student.getLastName() + "?")
                .setPositiveButton("Yes", (d, w) -> service.deleteStudent(student.get_id())
                        .enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                                if (r.isSuccessful()) loadStudents();
                                else Toast.makeText(StudentHubActivity.this,
                                        "Delete failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                                Toast.makeText(StudentHubActivity.this,
                                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
}