package com.example.swimmingpoolmanager.network;

import com.example.swimmingpoolmanager.entities.Schedule;
import com.example.swimmingpoolmanager.entities.Student;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PoolManagerService {

    @GET("schedule")
    Call<List<Schedule>> getSchedule();

    @POST("schedule/generate")
    Call<GenerateScheduleResponse> generateSchedule();

    @DELETE("schedule/clear")
    Call<Void> clearSchedule();

    @POST("students")
    Call<Void> addStudent(@Body Student student);

    @GET("students/all")
    Call<List<Student>> getAllStudents();

    @GET("schedule/issues")
    Call<IssuesResponse> getScheduleIssues();

    @GET("students/{id}")
    Call<Student> getStudent(@Path("id") String id);


    @PUT("students/{id}")
    Call<Student> updateStudent(@Path("id") String id, @Body Student student);

    @DELETE("students/{id}")
    Call<Void> deleteStudent(@Path("id") String id);
}

