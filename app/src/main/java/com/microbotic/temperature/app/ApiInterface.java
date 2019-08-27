package com.microbotic.temperature.app;

import com.microbotic.temperature.model.Temperature;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiInterface {

    /** login */
    @GET("api/login.php")
    Call<ResponseBody> login(@Query("username") String username,@Query("password") String password);


    /** class */
    @GET("api/class.php")
    Call<ResponseBody> getClassList(@Query("req") String req, @Query("id") String id);

    @GET("api/class.php")
    Call<ResponseBody> addClass(@Query("req") String req, @Query("id") String id,@Query("data") String data);

    @GET("api/class.php")
    Call<ResponseBody> updateClass(@Query("req") String req, @Query("id") String id,@Query("data") String data);


    /** student */
    @GET("api/student.php")
    Call<ResponseBody> getStudentsBySchool(@Query("req") String req, @Query("id") String id);

    @GET("api/student.php")
    Call<ResponseBody> getStudentsByClass(@Query("req") String req, @Query("id") String id, @Query("class_id") String classId);

    @GET("api/student.php")
    Call<ResponseBody> updateStudent(@Query("req") String req, @Query("id") String id,@Query("data") String data);


    /** temperature */
    @GET("api/temperature.php")
    Call<ResponseBody> getTemperatureListBySchool(@Query("req") String req, @Query("id") String id);

    @GET("api/temperature.php")
    Call<ResponseBody> getTemperatureListByCLass(@Query("req") String req, @Query("id") String id, @Query("class_id") String classId);

    @GET("api/temperature.php")
    Call<ResponseBody> addTemperature(@Query("req") String req, @Query("id") String id,@Query("data") String data);

    @GET("api/temperature.php")
    Call<ResponseBody> deleteTemperature(@Query("req") String req, @Query("id") String id,@Query("temperature_id") String temperatureId);

    /** student */
    @GET("api/robot.php")
    Call<ResponseBody> getRobotList(@Query("req") String req, @Query("id") String id);

    @GET("api/robot.php")
    Call<ResponseBody> addRobot(@Query("req") String req, @Query("id") String id, @Query("data") String data);

    @GET("api/robot.php")
    Call<ResponseBody> deleteRobot(@Query("req") String req, @Query("id") String id,@Query("robot_id") String data);

}
