package com.example.swimmingpoolmanager.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.swimmingpoolmanager.R;
import com.example.swimmingpoolmanager.entities.Schedule;
import com.example.swimmingpoolmanager.network.ApiClient;
import com.example.swimmingpoolmanager.network.GenerateScheduleResponse;
import com.example.swimmingpoolmanager.network.IssuesResponse;
import com.example.swimmingpoolmanager.network.PoolManagerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {
    private final String URL = "http://10.0.2.2:3000/api/";
    private AppCompatButton btnBack, btnGenerate, btnClear;
    private ProgressBar pbLoading;
    private AppCompatImageButton btnIssue;
    StringBuilder issueMsg;
    private AppCompatSpinner daySelectorSpinner;
    private LinearLayoutCompat headerRow, dayGridContainer;
    private PoolManagerService service;
    private final String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday"};
    private final Map<String,List<Schedule>> dayMap = new LinkedHashMap<>();
    private int lessonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        findViews();

        for (String d : days) dayMap.put(d, new ArrayList<>());

        buildTopRow();

        initDayArrayAdapter();

        daySelectorSpinner.setSelection(1);

        service = ApiClient.getClient(URL).create(PoolManagerService.class);

        setupButtons();

        loadSchedule();
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        btnGenerate.setOnClickListener(v -> generateSchedule());
        btnClear.setOnClickListener(v -> service.clearSchedule().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                for (List<Schedule> list : dayMap.values()) {
                    list.clear();
                }
                int selectedPos = daySelectorSpinner.getSelectedItemPosition();
                if (selectedPos > 0) {
                    dayGridContainer.removeAllViews();
                    buildGanttGrid(dayGridContainer, 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                Toast.makeText(ScheduleActivity.this,
                        "Clear failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
        btnIssue.setOnClickListener(v -> issueDialog());
    }

    private void initDayArrayAdapter() {
         List<String> spinnerDays = new ArrayList<>(Arrays.asList(
                "⏹",
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday"
        ));

        ArrayAdapter<String> adapter = getStringArrayAdapter(spinnerDays);
        daySelectorSpinner.setAdapter(adapter);
        daySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) return;
                String selDay = days[pos-1];
                lessonCount = 0;
                List<Schedule> lessons = dayMap.get(selDay);

                int numCols = (lessons == null) ? 0 : lessons.size() * 4;

                dayGridContainer.removeAllViews();
                buildGanttGrid(dayGridContainer, numCols);

                if (lessons != null) {
                    for (Schedule s : lessons) {
                        placeLessonBlock(dayGridContainer, s);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter(List<String> spinnerDays) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerDays
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @NonNull
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) v;
                if (position == 0) {
                     tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return v;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void buildTopRow() {
        headerRow.removeAllViews();
        String[] quarters = {":00", ":15", ":30", ":45"};
        for (int h = 8; h <= 20; h++) {
            for (String q : quarters) {
                AppCompatTextView txt = new AppCompatTextView(this);
                txt.setText(q);
                txt.setGravity(Gravity.CENTER);
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setWidth(80);
                txt.setHeight(48);

                headerRow.addView(txt);
            }
        }
    }

    private void findViews() {
        pbLoading          = findViewById(R.id.pbLoading);
        btnIssue           = findViewById(R.id.btnIssues);
        btnBack            = findViewById(R.id.btnBack);
        btnGenerate        = findViewById(R.id.btnGenerate);
        btnClear           = findViewById(R.id.btnClear);
        daySelectorSpinner = findViewById(R.id.daySelectorSpinner);
        headerRow          = findViewById(R.id.headerRow);
        dayGridContainer   = findViewById(R.id.dayGridContainer);
    }

    /**
     * Initiates the schedule generation process.
     * <p>
     * This method displays a loading indicator and disables the "Generate" button
     * while making a network request to generate a new schedule.
     * </p>
     */
    private void generateSchedule() {
        pbLoading.setVisibility(View.VISIBLE);
        btnGenerate.setEnabled(false);
        service.generateSchedule().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenerateScheduleResponse> call,
                                   @NonNull Response<GenerateScheduleResponse> response) {
                pbLoading.setVisibility(View.GONE);
                btnGenerate.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ScheduleActivity.this,
                            "Generate failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                service.getScheduleIssues().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<IssuesResponse> call,
                                           @NonNull Response<IssuesResponse> response2) {
                        if (response2.isSuccessful() && response2.body() != null) {
                            IssuesResponse ir = response2.body();
                            StringBuilder report = getStringBuilder(ir);

                            if (report.length() > 0) {
                                issueMsg = report;
                                issueDialog();
                            }
                        } else {
                            Toast.makeText(ScheduleActivity.this,
                                    "Failed to fetch schedule report", Toast.LENGTH_SHORT).show();
                        }
                        loadSchedule();
                    }

                    @Override
                    public void onFailure(@NonNull Call<IssuesResponse> call, @NonNull Throwable t) {
                        btnGenerate.setEnabled(true);
                        Toast.makeText(ScheduleActivity.this,
                                "Error fetching report: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        loadSchedule();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GenerateScheduleResponse> call, @NonNull Throwable t) {
                btnGenerate.setEnabled(true);
                btnGenerate.setEnabled(true);
                Toast.makeText(ScheduleActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Builds a formatted string report of schedule issues.
     * <p>
     * This method processes the `IssuesResponse` object to generate a report
     * containing unscheduled students and other issues. The report is formatted
     * as a `StringBuilder` for easy display or further processing.
     * </p>
     *
     * @param ir The `IssuesResponse` object containing issue details.
     * @return A `StringBuilder` containing the formatted report of issues.
     */
    @NonNull
    private static StringBuilder getStringBuilder(IssuesResponse ir) {
        StringBuilder report = new StringBuilder();

        List<String> uns = ir.getUnscheduledStudents();
        if (uns != null && !uns.isEmpty()) {
            report.append("Unscheduled students:\n");
            for (String name : uns) {
                report.append("• ").append(name).append("\n");
            }
            report.append("\n");
        }

        List<String> oth = ir.getIssues();
        if (oth != null && !oth.isEmpty()) {
            report.append("Other issues:\n");
            for (String issue : oth) {
                report.append("• ").append(issue).append("\n");
            }
        }
        return report;
    }


    private void issueDialog() {
        if (issueMsg == null) {
            getLastIssueMsg();
        }


        new AlertDialog.Builder(ScheduleActivity.this)
                .setTitle("Issues Report")
                .setMessage(issueMsg.toString())
                .setPositiveButton("OK", null)
                .show();

    }

    private void loadSchedule() {
        dayMap.values().forEach(List::clear);
        service.getSchedule().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Schedule>> c,
                                   @NonNull Response<List<Schedule>> r) {
                if (!r.isSuccessful() || r.body() == null) {
                    Toast.makeText(ScheduleActivity.this,
                            "Load failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Schedule s : r.body()) {
                    List<Schedule> lst = dayMap.get(s.getDay());
                    if (lst != null) lst.add(s);
                }

                for (String day : days) {
                    List<Schedule> lst = dayMap.get(day);
                    if (lst != null) {
                        lst.sort(Comparator.comparing(Schedule::getStartTime));
                    }
                }

                getLastIssueMsg();

                int currentPos = daySelectorSpinner.getSelectedItemPosition();
                if (currentPos > 0) {
                    daySelectorSpinner.setSelection(0);
                    daySelectorSpinner.post(() -> daySelectorSpinner.setSelection(currentPos));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Schedule>> c, @NonNull Throwable t) {
                Toast.makeText(ScheduleActivity.this,
                        "Error load: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Fetches the latest schedule issues from the server asynchronously.
     * <p>
     * This method makes a network request to retrieve the schedule issues
     * using the `PoolManagerService`. The response is processed to update
     * the `issueMsg` field with a formatted report of issues.
     * </p>
     * If the request fails or the response is unsuccessful, an error is logged.
     */
    private void getLastIssueMsg() {
        service.getScheduleIssues().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<IssuesResponse> call,
                                   @NonNull Response<IssuesResponse> response) {
                Log.d("ScheduleActivity", "got issues response, code=" + response.code());
                if (!response.isSuccessful() || response.body() == null) {
                    Log.d("ScheduleActivity", "issues call failed: " + response.message());
                    return;
                }
                issueMsg = getStringBuilder(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<IssuesResponse> call, @NonNull Throwable t) {
                Log.e("ScheduleActivity", "Failed to load issues", t);
            }
        });
    }


    /**
     * Builds the Gantt-style grid for the schedule.
     * <p>
     * This method clears the container and dynamically creates rows and columns
     * to represent time slots for lessons. Each row corresponds to an hour, and
     * each column represents a 15-minute interval.
     * </p>
     *
     * @param container The `LinearLayoutCompat` container to populate with the grid.
     * @param numCols   The number of columns to display in the grid.
     */
    private void buildGanttGrid(LinearLayoutCompat container, int numCols) {
        container.removeAllViews();

        headerRow.removeAllViews();
        for (int q = 0; q < numCols; q++) {
            int minute = (q % 4) * 15;
            AppCompatTextView inc = new AppCompatTextView(this);
            inc.setText(String.format(Locale.getDefault(), ":%02d", minute));
            inc.setGravity(Gravity.CENTER);
            inc.setTypeface(Typeface.DEFAULT_BOLD);
            inc.setWidth(80);
            inc.setHeight(48);
            headerRow.addView(inc);
        }

        for (int hour = 8; hour <= 20; hour++) {
            LinearLayoutCompat row = new LinearLayoutCompat(this);
            row.setOrientation(LinearLayoutCompat.HORIZONTAL);
            row.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            row.setPadding(0,4,0,4);

            AppCompatTextView hourLbl = new AppCompatTextView(this);
            hourLbl.setText(String.format(Locale.getDefault(), "%02d:00", hour));
            hourLbl.setWidth(128);
            hourLbl.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(hourLbl);

            for (int i = 0; i < numCols; i++) {
                View slot = new View(this);
                slot.setLayoutParams(new LinearLayoutCompat.LayoutParams(80, 64));
                slot.setBackgroundColor(Color.parseColor("#EEEEEE"));
                row.addView(slot);
            }

            container.addView(row);
        }
    }



    /**
         * Places a lesson block in the Gantt grid for the given lesson.
         * <p>
         * This method calculates the appropriate row and column for the lesson based on its start time
         * and type, creates a visual block representing the lesson, and adds it to the grid.
         * The block is clickable to show lesson details.
         * </p>
         *
         * @param container The LinearLayoutCompat container representing the day's grid.
         * @param lesson    The Schedule object containing lesson details.
         */
    private void placeLessonBlock(LinearLayoutCompat container, Schedule lesson) {
        String[] p = lesson.getStartTime().split(":");
        int hour = Integer.parseInt(p[0]);

        int rowIndex = hour - 8;
        if (rowIndex < 0 || rowIndex >= container.getChildCount()) return;

        int startSlot = lessonCount * 4;
        int span      = lesson.getType().equalsIgnoreCase("Group") ? 4 : 3;

        LinearLayoutCompat row = (LinearLayoutCompat) container.getChildAt(rowIndex);
        int maxInsertPos = row.getChildCount() - 1;
        int insertPos   = Math.min(startSlot + 1, maxInsertPos);

        AppCompatTextView block = new AppCompatTextView(this);
        block.setText(lesson.getType());
        block.setWidth(80 * span);
        block.setHeight(64);
        block.setGravity(Gravity.CENTER);
        block.setBackgroundColor(
                lesson.getType().equalsIgnoreCase("Group")
                        ? Color.parseColor(getString(R.string.GroupColor))
                        : Color.parseColor(getString(R.string.PrivateColor))
        );
        block.setTextColor(Color.BLACK);

        row.addView(block, insertPos);

        for (int i = 0; i < span; i++) {
            int last = row.getChildCount() - 1;
            row.removeViewAt(last);
        }

        block.setOnClickListener(v -> {
            lessonDetails(lesson);
        });

        lessonCount++;
    }


    private void lessonDetails(Schedule lesson) {
        int duration = lesson.getType().equalsIgnoreCase("Group") ? 60 : 45;

        String msg = "Type: " + lesson.getType() +
                "\nSwimming: " + lesson.getSwimmingType() +
                "\nTime: " + lesson.getDay() + " " + lesson.getStartTime() +
                "\nDuration: " + duration + " min" +
                "\nInstructor: " + lesson.getInstructorName() +
                "\nStudents:\n- " + TextUtils.join("\n- ", lesson.getStudentNames());

        new AlertDialog.Builder(ScheduleActivity.this)
                .setTitle("Lesson Details")
                .setMessage(msg)
                .setPositiveButton("Close", null)
                .show();
    }

}