package com.example.swimmingpoolmanager.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swimmingpoolmanager.R;
import com.example.swimmingpoolmanager.entities.Student;
import com.example.swimmingpoolmanager.network.ApiClient;
import com.example.swimmingpoolmanager.network.PoolManagerService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {

    private static final String URL = "http://10.0.2.2:3000/api/";
    private EditText etFirstName, etLastName;
    private Spinner spinnerPreference;
    private CheckBox cbFreestyle, cbBackstroke, cbBreaststroke, cbButterfly;
    private Button btnCancel, btnSubmit;

    private boolean isEditMode = false;
    private String editingStudentId;
    private final String[] lessonPreferences = {"Private", "Group", "Private+Group"};
    private PoolManagerService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        apiService = ApiClient.getClient(URL).create(PoolManagerService.class);
        findViews();
        setupSpinner();
        btnCancel.setOnClickListener(v -> finish());
        // Check for edit mode
        editingStudentId = getIntent().getStringExtra("studentId");
        if (editingStudentId != null) {
            isEditMode = true;
            setTitle("Edit Student");
            loadStudent(editingStudentId);
        }

        btnSubmit.setOnClickListener(v -> submitStudent());

    }

    private void findViews() {
        btnCancel = findViewById(R.id.btnCancel);

        etFirstName       = findViewById(R.id.etFirstName);
        etLastName        = findViewById(R.id.etLastName);
        spinnerPreference = findViewById(R.id.spinnerPreference);
        cbFreestyle       = findViewById(R.id.cbFreestyle);
        cbBackstroke      = findViewById(R.id.cbBackstroke);
        cbBreaststroke    = findViewById(R.id.cbBreaststroke);
        cbButterfly       = findViewById(R.id.cbButterfly);
        btnSubmit         = findViewById(R.id.btnSubmit);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                lessonPreferences
        );
        spinnerPreference.setAdapter(adapter);
    }

    private void loadStudent(String id) {
        apiService.getStudent(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Student> call, @NonNull Response<Student> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(AddStudentActivity.this,
                            "Failed to load student", Toast.LENGTH_SHORT).show();
                    return;
                }
                Student s = resp.body();
                // Populate text fields
                etFirstName.setText(s.getFirstName());
                etLastName.setText(s.getLastName());

                // Populate spinner
                for (int i = 0; i < lessonPreferences.length; i++) {
                    if (lessonPreferences[i].equals(s.getLessonPreference())) {
                        spinnerPreference.setSelection(i);
                        break;
                    }
                }

                // Populate checkboxes
                List<String> types = s.getSwimmingTypes();
                cbFreestyle.setChecked(types.contains("Freestyle"));
                cbBackstroke.setChecked(types.contains("Backstroke"));
                cbBreaststroke.setChecked(types.contains("Breaststroke"));
                cbButterfly.setChecked(types.contains("Butterfly"));

                // Change button text
                btnSubmit.setText(R.string.update);
            }

            @Override
            public void onFailure(@NonNull Call<Student> call, @NonNull Throwable t) {
                Toast.makeText(AddStudentActivity.this,
                        "Error loading student", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitStudent() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName  = etLastName.getText().toString().trim();
        String preference = (String) spinnerPreference.getSelectedItem();

        List<String> types = new ArrayList<>();
        if (cbFreestyle.isChecked())    types.add("Freestyle");
        if (cbBackstroke.isChecked())   types.add("Backstroke");
        if (cbBreaststroke.isChecked()) types.add("Breaststroke");
        if (cbButterfly.isChecked())    types.add("Butterfly");

        if (firstName.isEmpty() || lastName.isEmpty() || types.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Student payload = new Student(firstName, lastName, types, preference);

        if (isEditMode) {
            apiService.updateStudent(editingStudentId, payload)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<Student> c, @NonNull Response<Student> r) {
                            if (r.isSuccessful()) {
                                Toast.makeText(AddStudentActivity.this,
                                        "Student updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddStudentActivity.this,
                                        "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Student> c, @NonNull Throwable t) {
                            Toast.makeText(AddStudentActivity.this,
                                    "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            apiService.addStudent(payload)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                            if (r.isSuccessful()) {
                                Toast.makeText(AddStudentActivity.this,
                                        "Student added", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddStudentActivity.this,
                                        "Add failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                            Toast.makeText(AddStudentActivity.this,
                                    "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}