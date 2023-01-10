package com.triton.johnson_tap_app.Service_Activity.SiteAudit;

import static android.view.View.GONE;
import static com.android.volley.VolleyLog.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyLog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.triton.johnson_tap_app.Location.GpsTracker;
import com.triton.johnson_tap_app.R;
import com.triton.johnson_tap_app.Service_Activity.Breakdown_Services.Feedback_DetailsActivity;
import com.triton.johnson_tap_app.Service_Activity.Breakdown_Services.Start_Job_TextActivity;
import com.triton.johnson_tap_app.Service_Activity.PreventiveMRApproval.StartJob_PreventiveMR_Activity;
import com.triton.johnson_tap_app.api.APIInterface;
import com.triton.johnson_tap_app.api.RetrofitClient;
import com.triton.johnson_tap_app.requestpojo.Job_statusRequest;
import com.triton.johnson_tap_app.requestpojo.Job_status_updateRequest;
import com.triton.johnson_tap_app.responsepojo.Job_statusResponse;
import com.triton.johnson_tap_app.responsepojo.Job_status_updateResponse;
import com.triton.johnson_tap_app.responsepojo.RetriveResponseAudit;
import com.triton.johnson_tap_app.utils.ConnectionDetector;
import com.triton.johnson_tap_app.utils.RestUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartJob_AuditActivity extends AppCompatActivity {

    Context context;
    ImageView img_Back;
    TextView text;
    SharedPreferences sharedPreferences;
    FloatingActionButton send;
    String se_user_mobile_no, se_user_name, se_id,check_id, service_title,jobid,message,str_job_status,osacompno;
    String compno, sertype,status,str_StartTime ="",networkStatus="";
    TextView txt_DateandTime;
    String currentDateandTime;
    GpsTracker gpsTracker;
    TextView tvLatitude,tvLongitude,tvAddress;
    Geocoder geocoder;
    List<Address> myAddress =  new ArrayList<>();
    double Latitude ,Logitude;
    String address = "";
    AlertDialog mDialog;
    int PageNumber =0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_startjob_siteaudit);
        context = this;

//        Latitude = Double.parseDouble("0.0");
//        Logitude = Double.parseDouble("0.0");

        send = findViewById(R.id.add_fab);
        text = findViewById(R.id.text);
        img_Back = (ImageView) findViewById(R.id.img_back);
        txt_DateandTime = findViewById(R.id.txt_datetime);
        tvLatitude = (TextView)findViewById(R.id.latitude);
        tvLongitude = (TextView)findViewById(R.id.longitude);
        tvAddress = findViewById(R.id.address);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(StartJob_AuditActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        se_id = sharedPreferences.getString("_id", "default value");
        se_user_mobile_no = sharedPreferences.getString("user_mobile_no", "default value");
        se_user_name = sharedPreferences.getString("user_name", "default value");
        service_title = sharedPreferences.getString("service_title", "Services");
        jobid =sharedPreferences.getString("jobid","L-1234");
        osacompno = sharedPreferences.getString("osacompno","ADT2020202020");

        Log.e("Name", "" + service_title);
        Log.e("Mobile", ""+ se_user_mobile_no);
        Log.e("Jobid",""+ jobid);
        Log.e("osocompno",""+ osacompno);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("status");
            Log.e("Status", "" + status);
        }


        Spannable name_Upload = new SpannableString("Start Job ");
        name_Upload.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, name_Upload.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(name_Upload);
        Spannable name_Upload1 = new SpannableString("*");
        name_Upload1.setSpan(new ForegroundColorSpan(Color.RED), 0, name_Upload1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.append(name_Upload1);

        getMYLocation();
        Toast.makeText(context,"Lat : " + Latitude + "Long : " + Logitude + "Add : " + address,Toast.LENGTH_LONG).show();


        networkStatus = ConnectionDetector.getConnectivityStatusString(getApplicationContext());

        Log.e("Network",""+networkStatus);
        if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

            NoInternetDialog();

        }else {

            jobStatuscall();
        }

        if (status.equals("pause")){

            send.setImageResource(R.drawable.ic_resume);
            //  send.getImageTintList() = ColorStateList.valueOf(Color.rgb(255, 50, 50));

            txt_DateandTime.setVisibility(View.VISIBLE);

            Spannable name_Upload2 = new SpannableString("Resume Job ");
            name_Upload2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, name_Upload.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setText(name_Upload2);
            Spannable name_Upload3 = new SpannableString("*");
            name_Upload3.setSpan(new ForegroundColorSpan(Color.RED), 0, name_Upload3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.append(name_Upload3);

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            txt_DateandTime.setText(date);

        }

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
//                Intent send = new Intent(context, AD_DetailsSiteAudit_Activity.class);
//                //send.putExtra("service_title",service_title);
//                send.putExtra("status", status);
//                startActivity(send);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                getSampleLoc();

                if (status.equals("new")){

                    Log.e("Status",""+ message);

                    if (Objects.equals(message, "Not Started")){

                        Log.e("Hi","inside");
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        currentDateandTime = df.format(Calendar.getInstance().getTime());
                        Log.e ("Start Time",""+ currentDateandTime);
                        str_StartTime = currentDateandTime;

                        alert();

                    }
                    else{

                        Log.e("Hi","outside");
                        Intent send = new Intent(context, Checklist_AuditActivity.class);
                        send.putExtra("status", status);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("starttime", str_StartTime);
                        Log.e("Time",""+str_StartTime);
                        editor.putString("lati", String.valueOf(Latitude));
                        editor.putString("long", String.valueOf(Logitude));
                        editor.putString("add",address);
                        editor.apply();
                        startActivity(send);

                    }


                }
                else{

                    str_job_status = "Job Paused";
                    Job_status_update();
                }
            }
        });
    }

    private Job_status_updateRequest getSampleLoc() {

        Job_status_updateRequest custom = new Job_status_updateRequest();
        custom.setUser_mobile_no(se_user_mobile_no);
        custom.setService_name(service_title);
        custom.setJob_id(jobid);
        custom.setStatus(str_job_status);
        custom.setOM_OSA_COMPNO(osacompno);
        custom.setJOB_LOCATION(address);
        custom.setJOB_START_LAT(Latitude);
        custom.setJOB_START_LONG(Logitude);
        Log.w(VolleyLog.TAG,"Request "+ new Gson().toJson(custom));
        return custom;
    }

    public void NoInternetDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_nointernet, null);
        Button btn_Retry = mView.findViewById(R.id.btn_retry);


        mBuilder.setView(mView);
        final Dialog dialog= mBuilder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        btn_Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();
                startActivity(getIntent());

            }
        });
    }


    private void getMYLocation() {

        Log.e("Hi","Getting Your Location");
        gpsTracker = new GpsTracker(StartJob_AuditActivity.this);
        if(gpsTracker.canGetLocation()){
            Latitude = gpsTracker.getLatitude();
            Logitude = gpsTracker.getLongitude();
            tvLatitude.setText(String.valueOf(Latitude));
            tvLongitude.setText(String.valueOf(Logitude));
            Log.e("Lat ",Latitude + " Long: " + Logitude);

            geocoder = new Geocoder(context,Locale.getDefault());

            if (Latitude > 0.0 && Logitude > 0.0){

                try {
                    myAddress = geocoder.getFromLocation(gpsTracker.getLatitude(),gpsTracker.getLongitude(),1);
                    address = myAddress.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.e("Address",address);


                tvAddress.setText(address);
            }
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void alert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = getLayoutInflater().inflate(R.layout.startjob_popup_layout, null);

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        TextView txt_DateTime = mView.findViewById(R.id.txt_datetime);
        txt_DateTime.setText(date);

        TextView txt_jobstatus = mView.findViewById(R.id.txt_jobstatus);
        TextView txt_job_content = mView.findViewById(R.id.txt_job_content);
        LinearLayout ll_start = mView.findViewById(R.id.ll_start);
        LinearLayout ll_pause = mView.findViewById(R.id.ll_pause);
        LinearLayout ll_stop = mView.findViewById(R.id.ll_stop);
        LinearLayout ll_resume = mView.findViewById(R.id.ll_resume);
        ImageView img_close = mView.findViewById(R.id.img_close);
        Button btn_back = mView.findViewById(R.id.btn_back);
        btn_back.setVisibility(GONE);
        txt_jobstatus.setVisibility(GONE);
        ll_resume.setVisibility(GONE);
        ll_pause.setVisibility(GONE);
        ll_stop.setVisibility(GONE);

        mBuilder.setView(mView);
        mDialog= mBuilder.create();
        mDialog.show();

        ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_job_status = "Job Started";
                networkStatus = ConnectionDetector.getConnectivityStatusString(getApplicationContext());

                Log.e("Network",""+networkStatus);
                if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

                    NoInternetDialog();

                }else {

                    Toast.makeText(context,"Lat : " + Latitude + "Long : " + Logitude + "Add : " + address,Toast.LENGTH_LONG).show();


                    if (Latitude > 0.0 && Logitude > 0.0 && !Objects.equals(address, "")){

                        Job_status_update();

                    }
                    else{

                        mDialog.dismiss();

                        ErrorAlert();
                    }

                }

            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void ErrorAlert() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = getLayoutInflater().inflate(R.layout.popup_tryagain, null);

        TextView txt_Message = mView.findViewById(R.id.txt_message);
        Button btn_Ok = mView.findViewById(R.id.btn_ok);


        mBuilder.setView(mView);
        mDialog = mBuilder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void jobStatuscall() {

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<Job_statusResponse> call = apiInterface.CheckworkAuditStatusCall(RestUtils.getContentType(), job_statusRequest());
        Log.w(TAG,"SignupResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<Job_statusResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<Job_statusResponse> call, @NonNull retrofit2.Response<Job_statusResponse> response) {

                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));
                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (200 == response.body().getCode()) {
                        if(response.body().getData() != null){

                            Log.d("msg",message);

                            if (!Objects.equals(message, "Not Started")){

                                str_StartTime = response.body().getTime();

                                Log.e (" Start Time  API",""+str_StartTime);

                            }
                        }


                    } else {
                        Toasty.warning(getApplicationContext(),""+message,Toasty.LENGTH_LONG).show();

                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<Job_statusResponse> call, @NonNull Throwable t) {
                Log.e("OTP", "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Job_statusRequest job_statusRequest() {
        Job_statusRequest custom = new Job_statusRequest();
        custom.setUser_mobile_no(se_user_mobile_no);
        custom.setService_name(service_title);
        custom.setJob_id(jobid);
        custom.setOM_OSA_COMPNO(osacompno);
        Log.w(TAG,"loginRequest "+ new Gson().toJson(custom));
        return custom;
    }


    private void Job_status_update() {

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<Job_status_updateResponse> call = apiInterface.job_status_updateAuditResponseCall(com.triton.johnson_tap_app.utils.RestUtils.getContentType(), job_status_updateRequest());
        Log.w(VolleyLog.TAG,"Response url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<Job_status_updateResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<Job_status_updateResponse> call, @NonNull Response<Job_status_updateResponse> response) {

                Log.w(VolleyLog.TAG,"Response" + new Gson().toJson(response.body()));
                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (200 == response.body().getCode()) {
                        if(response.body().getData() != null){

                            Log.d("msg",message);

                            if (Objects.equals(status, "new")){
                                mDialog.dismiss();
                                Intent send = new Intent(context, Checklist_AuditActivity.class);
                                send.putExtra("status", status);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("starttime", str_StartTime);
                                Log.e("Time", "" + str_StartTime);
                                editor.putString("lati", String.valueOf(Latitude));
                                editor.putString("long", String.valueOf(Logitude));
                                editor.putString("add",address);
                                editor.apply();
                                startActivity(send);
                            }
                            else{

                                retrive_LocalValue();
                            }



                        }


                    } else {
                        Toasty.warning(getApplicationContext(),""+message,Toasty.LENGTH_LONG).show();

                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<Job_status_updateResponse> call, @NonNull Throwable t) {
                Log.e("OTP", "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Job_status_updateRequest job_status_updateRequest() {
        Job_status_updateRequest custom = new Job_status_updateRequest();
        custom.setUser_mobile_no(se_user_mobile_no);
        custom.setService_name(service_title);
        custom.setJob_id(jobid);
        custom.setStatus(str_job_status);
        custom.setOM_OSA_COMPNO(osacompno);
        custom.setJOB_LOCATION(address);
        custom.setJOB_START_LAT(Latitude);
        custom.setJOB_START_LONG(Logitude);
        Log.w(VolleyLog.TAG,"Request "+ new Gson().toJson(custom));
        return custom;
    }

    private void retrive_LocalValue() {

        APIInterface apiInterface =  RetrofitClient.getClient().create((APIInterface.class));
        Call<RetriveResponseAudit> call = apiInterface.retriveLocalValueCallAudit(com.triton.johnson_tap_app.utils.RestUtils.getContentType(),localRequest());
        Log.w(TAG,"Retrive Local Value url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<RetriveResponseAudit>() {
            @Override
            public void onResponse(Call<RetriveResponseAudit> call, Response<RetriveResponseAudit> response) {

                Log.w(TAG,"Retrive Response" + new Gson().toJson(response.body()));

                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (response.body().getCode() == 200) {

                        if(response.body().getData() != null) {

                            PageNumber = response.body().getData().getPageNumber();
                            Log.e("Pause Page Number",""+PageNumber);

                            if (PageNumber ==2){

                                Intent send = new Intent(context, AuditChecklist.class);
                                send.putExtra("status", status);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("starttime", str_StartTime);
                                Log.e("Time", "" + str_StartTime);
                                editor.putString("lati", String.valueOf(Latitude));
                                editor.putString("long", String.valueOf(Logitude));
                                editor.putString("add",address);
                                editor.apply();
                                startActivity(send);
                            }
                            else if(PageNumber ==4){

                                Intent send = new Intent(context, AuditMR_Activity.class);
                                send.putExtra("job_id",jobid);
                                send.putExtra("status", status);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("starttime", str_StartTime);
                                editor.putString("lati", String.valueOf(Latitude));
                                editor.putString("long", String.valueOf(Logitude));
                                editor.putString("add",address);
                                editor.apply();
                                startActivity(send);
                            }
                            else{

                                Intent send = new Intent(context, TechnicianSigantureAudit_Activity.class);
                                send.putExtra("job_id",jobid);
                                send.putExtra("status", status);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("starttime", str_StartTime);
                                editor.putString("lati", String.valueOf(Latitude));
                                editor.putString("long", String.valueOf(Logitude));
                                editor.putString("add",address);
                                editor.apply();
                                startActivity(send);
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RetriveResponseAudit> call, Throwable t) {
                Log.e("On Failure", "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private Job_status_updateRequest localRequest() {

        Job_status_updateRequest custom = new Job_status_updateRequest();
        custom.setUser_mobile_no(se_user_mobile_no);
        custom.setJobId(jobid);
        custom.setOM_OSA_COMPNO(osacompno);
        Log.w(VolleyLog.TAG,"Retrive Request "+ new Gson().toJson(custom));
        return custom;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
//        Intent send = new Intent(context, AD_DetailsSiteAudit_Activity.class);
//        //send.putExtra("service_title",service_title);
//        send.putExtra("status", status);
//        startActivity(send);
    }
}
