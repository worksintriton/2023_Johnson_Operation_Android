package com.triton.johnson_tap_app.Service_Activity.LR_Service;

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
import com.triton.johnson_tap_app.Service_Activity.BreakdownMRApprovel.MRForms_BreakdownMRActivity;
import com.triton.johnson_tap_app.Service_Activity.BreakdownMRApprovel.StartJob_BreakdownMR_Activity;
import com.triton.johnson_tap_app.Service_Activity.PartsReplacementACK.StartJob_ACK_Activity;
import com.triton.johnson_tap_app.Service_Activity.PreventiveMRApproval.MRForms_PreventiveMRActivity;
import com.triton.johnson_tap_app.Service_Activity.PreventiveMRApproval.StartJob_PreventiveMR_Activity;
import com.triton.johnson_tap_app.Service_Activity.ServicesActivity;
import com.triton.johnson_tap_app.api.APIInterface;
import com.triton.johnson_tap_app.api.RetrofitClient;
import com.triton.johnson_tap_app.requestpojo.Job_statusRequest;
import com.triton.johnson_tap_app.requestpojo.Job_status_updateRequest;
import com.triton.johnson_tap_app.requestpojo.LRService_SubmitRequest;
import com.triton.johnson_tap_app.requestpojo.ServiceUserdetailsRequestResponse;
import com.triton.johnson_tap_app.responsepojo.Job_statusResponse;
import com.triton.johnson_tap_app.responsepojo.Job_status_updateResponse;
import com.triton.johnson_tap_app.responsepojo.SuccessResponse;
import com.triton.johnson_tap_app.utils.ConnectionDetector;
import com.triton.johnson_tap_app.utils.RestUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartJob_LRService_Activity extends AppCompatActivity {

    FloatingActionButton send;
    TextView text;
    ImageView iv_back;
    String se_user_mobile_no, se_user_name, se_id,check_id, service_title,job_id,message,str_job_status;
    String compno, sertype,status;
    String str_Custname, str_Custno, str_Custremarks,str_Techsign,str_CustAck,str_Quoteno,service_type,str_StartTime,currentDateandTime;
    Context context;
    SharedPreferences sharedPreferences;
    TextView txt_DateandTime;
    GpsTracker gpsTracker;
    Geocoder geocoder;
    double Latitude ,Logitude;
    String address = "",networkStatus ="";
    List<Address> myAddress =  new ArrayList<>();
    AlertDialog mDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_startjob_lrservice);
        context = this;

//        Latitude = Double.parseDouble("0.0");
//        Logitude = Double.parseDouble("0.0");
        send = findViewById(R.id.add_fab);
        text = findViewById(R.id.text);
        iv_back = (ImageView) findViewById(R.id.img_back);
        txt_DateandTime = findViewById(R.id.txt_datetime);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(StartJob_LRService_Activity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //  service_title = extras.getString("service_title");
            status = extras.getString("status");
            //   Log.e("Name",":" + service_title);
            Log.e("Status", "" + status);
            job_id = extras.getString("job_id");
            Log.e("JobID",""+job_id);
            str_Custname = extras.getString("C_name");
            str_Custno = extras.getString("C_no");
            str_Custremarks = extras.getString("C_remarks");
            str_Techsign = extras.getString("tech_signature");
            str_CustAck = extras.getString("cust_ack");
//            Log.e("A", "" + str_Custname);
//            Log.e("A", "" + str_Custno);
//            Log.e("A", "" + str_Custremarks);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        se_id = sharedPreferences.getString("_id", "default value");
        se_user_mobile_no = sharedPreferences.getString("user_mobile_no", "default value");
        se_user_name = sharedPreferences.getString("user_name", "default value");
        service_title = sharedPreferences.getString("service_title", "Services");
        str_Quoteno = sharedPreferences.getString("quoteno","123");
        service_type = sharedPreferences.getString("service_type","PSM");
        Log.e("Name", "" + service_title);
        Log.e("Mobile", ""+ se_user_mobile_no);
        Log.e("QuoteNO", ""+ str_Quoteno);

        Spannable name_Upload = new SpannableString("Start Job ");
        name_Upload.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, name_Upload.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(name_Upload);
        Spannable name_Upload1 = new SpannableString("*");
        name_Upload1.setSpan(new ForegroundColorSpan(Color.RED), 0, name_Upload1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.append(name_Upload1);

        networkStatus = ConnectionDetector.getConnectivityStatusString(getApplicationContext());

        Log.e("Network",""+networkStatus);
        if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

           NoInternetDialog();

        }
        else{
            Job_status();
        }

        getMYLocation();
        Toast.makeText(context,"Lat : " + Latitude + "Long : " + Logitude + "Add : " + address,Toast.LENGTH_LONG).show();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent send = new Intent(context, LR_Details_Activity.class);
                send.putExtra("job_id",job_id);
                send.putExtra("status" , status);
                send.putExtra("C_name" , str_Custname);
                send.putExtra("C_no" , str_Custno);
                send.putExtra("C_remarks" , str_Custremarks);
                send.putExtra("tech_signature", str_Techsign);
                send.putExtra("cust_ack",str_CustAck);;
                startActivity(send);
            }
        });

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                        Intent send = new Intent(StartJob_LRService_Activity.this, CustomerDetails_LRServiceActivity.class);
                        send.putExtra("job_id",job_id);
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
                else {
                    str_job_status = "Job Resume";

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

                            ErrorAlert();
                        }

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

    private void alert() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartJob_LRService_Activity.this);
        View mView = getLayoutInflater().inflate(R.layout.startjob_popup_layout, null);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        TextView txt_DateTime = mView.findViewById(R.id.txt_datetime);
        txt_DateandTime.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
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
        mDialog = mBuilder.create();
        mDialog.show();

        ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_job_status = "Job Started";

                networkStatus = ConnectionDetector.getConnectivityStatusString(getApplicationContext());

                Log.e("Network", "" + networkStatus);
                if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

                 NoInternetDialog();

                } else{

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

    private void Job_status() {

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<Job_statusResponse> call = apiInterface.CheckworkStatusLRCall(RestUtils.getContentType(), job_statusRequest());
        Log.w(TAG,"SignupResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<Job_statusResponse>() {

            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<Job_statusResponse> call, @NonNull retrofit2.Response<Job_statusResponse> response) {

                Log.e("Hi","OnResponse");

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
        custom.setJob_id(job_id);
        custom.setSMU_SCQH_QUOTENO(str_Quoteno);
        Log.w(TAG,"loginRequest "+ new Gson().toJson(custom));
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


    private void createLocalValueCall() {

        APIInterface apiInterface =  RetrofitClient.getClient().create((APIInterface.class));
        Call<SuccessResponse> call = apiInterface.createLocalValueCallLR(RestUtils.getContentType(),createLocalRequest());
        Log.w(TAG,"Create Local Value url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {

                Log.e("Hi","OnSucess");

                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));

                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (response.body().getCode() == 200){

                        if(response.body().getData() != null){

                            Log.d("msg",message);

                            Intent send = new Intent(context, ServicesActivity.class);
                            startActivity(send);
                        }

                    } else{

                        Toasty.warning(getApplicationContext(),""+message,Toasty.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {

                Log.e("On Failure", "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private LRService_SubmitRequest createLocalRequest() {
        LRService_SubmitRequest request = new LRService_SubmitRequest();
        request.setJobId(job_id);
        request.setUserId(se_user_mobile_no);
        request.setServiceType(service_type);
        request.setCustomerName("-");
        request.setCustomerNo("-");
        request.setRemarks("-");
        request.setTechSignature("-");
        request.setCustomerAcknowledgement("-");
        request.setSMU_SCQH_QUOTENO(str_Quoteno);
        request.set_id(se_id);
        // local.setMrData(null);
        Log.w(TAG,"Local Request "+ new Gson().toJson(request));
        return request;
    }


    private void Job_status_update() {

            APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
            Call<Job_status_updateResponse> call = apiInterface.job_status_updateLRResponseCall(com.triton.johnson_tap_app.utils.RestUtils.getContentType(), job_status_updateRequest());
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
                                }

                                Intent send = new Intent(context, CustomerDetails_LRServiceActivity.class);
                                send.putExtra("job_id", job_id);
                                send.putExtra("status", status);
                                send.putExtra("C_name", str_Custname);
                                send.putExtra("C_no", str_Custno);
                                send.putExtra("C_remarks", str_Custremarks);
                                send.putExtra("tech_signature", str_Techsign);
                                send.putExtra("cust_ack", str_CustAck);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("starttime", str_StartTime);
                                editor.putString("lati", String.valueOf(Latitude));
                                editor.putString("long", String.valueOf(Logitude));
                                editor.putString("add",address);
                                editor.apply();
                                startActivity(send);
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
        custom.setJob_id(job_id);
        custom.setStatus(str_job_status);
        custom.setSMU_SCQH_QUOTENO(str_Quoteno);
        custom.setJOB_LOCATION(address);
        custom.setJOB_START_LAT(Latitude);
        custom.setJOB_START_LONG(Logitude);
        Log.w(VolleyLog.TAG,"Request "+ new Gson().toJson(custom));
        return custom;
    }

    @Override
    public void onBackPressed() {
        Intent send = new Intent(context, LR_Details_Activity.class);
        send.putExtra("job_id",job_id);
        send.putExtra("status" , status);
        send.putExtra("C_name" , str_Custname);
        send.putExtra("C_no" , str_Custno);
        send.putExtra("C_remarks" , str_Custremarks);
        send.putExtra("tech_signature", str_Techsign);
        send.putExtra("cust_ack",str_CustAck);
        startActivity(send);
    }


    private void getMYLocation() {

        Log.e("Hi","Getting Your Location");
        gpsTracker = new GpsTracker(StartJob_LRService_Activity.this);
        if(gpsTracker.canGetLocation()){
            Latitude = gpsTracker.getLatitude();
            Logitude = gpsTracker.getLongitude();
            Log.e("Lat ",Latitude + " Long: " + Logitude);

            if (Latitude > 0.0 && Logitude > 0.0){
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    myAddress = geocoder.getFromLocation(gpsTracker.getLatitude(),gpsTracker.getLongitude(),1);
                    address = myAddress.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("Address",address);
            }
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
}
