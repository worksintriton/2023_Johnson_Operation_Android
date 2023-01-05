package com.triton.johnsonapp.infopages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.triton.johnsonapp.R;
import com.triton.johnsonapp.api.APIInterface;
import com.triton.johnsonapp.api.RetrofitClient;
import com.triton.johnsonapp.requestpojo.JobFetchAddressRequest;
import com.triton.johnsonapp.responsepojo.LiftwellInfo_Response;
import com.triton.johnsonapp.session.SessionManager;
import com.triton.johnsonapp.utils.ConnectionDetector;
import com.triton.johnsonapp.utils.RestUtils;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoList_Activity  extends AppCompatActivity {

    private String TAG ="ViewInfoDetailsActivity";

    String userid,username;
    String networkStatus = "",message,job_id;

    Button btn_GoBack;

    ImageView img_Back;

    private Dialog dialog;

    LiftwellInfo_Response.DataBean dataBean;

    TextView txt_BrCode, txt_Jobno, txt_Cusname, txt_Ecno, txt_CustAd, txt_InsAd, txt_Nounits, txt_Dmodel, txt_Loadspeed,
                txt_Speed, txt_Floors, txt_Nocarent, txt_Liftwell, txt_Landent, txt_ClrOpen, txt_Framesize,txt_Jobid;

    String str_BrCode, str_Jobno, str_Cusname, str_Ecno, str_CustAd, str_InsAd, str_Nounits, str_Dmodel,str_Loadspeed,
            str_Speed, str_Floors, str_Nocarent, str_Liftwell, str_Landent, str_ClrOpen, str_Framesize,str_Jobid;

    Context context;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infolist);
        context =  this;

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_ID);
        String useremailid = user.get(SessionManager.KEY_EMAILID);

        username = user.get(SessionManager.KEY_USERNAME);

        txt_BrCode = findViewById(R.id.txt_brcode);
        txt_Jobno = findViewById(R.id.txt_jobno);
        txt_Cusname = findViewById(R.id.txt_cusno);
        txt_Ecno = findViewById(R.id.txt_ecno);
        txt_CustAd = findViewById(R.id.txt_custaddress);
        txt_InsAd = findViewById(R.id.txt_insaddress);
        txt_Nounits = findViewById(R.id.txt_nounits);
        txt_Dmodel = findViewById(R.id.txt_dmodel);
        txt_Loadspeed = findViewById(R.id.txt_loadspeed);
        txt_Speed = findViewById(R.id.txt_speed);
        txt_Floors = findViewById(R.id.txt_floors);
        txt_Nocarent = findViewById(R.id.txt_nocarenet);
        txt_Liftwell = findViewById(R.id.txt_liftwell);
        txt_Landent = findViewById(R.id.txt_landentrance);
        txt_ClrOpen = findViewById(R.id.txt_clropen);
        txt_Framesize = findViewById(R.id.txt_framesize);
        btn_GoBack = findViewById(R.id.btn_goback);
        txt_Jobid = findViewById(R.id.txt_job_id);
        img_Back = findViewById(R.id.img_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            job_id = extras.getString("job_id");
            Log.w(TAG,"job_id : "+job_id);
            txt_Jobid.setText("Job ID :"+job_id);

        }

        networkStatus = ConnectionDetector.getConnectivityStatusString(getApplicationContext());
        if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

            Toasty.warning(getApplicationContext(),"No Internet",Toasty.LENGTH_LONG).show();

        }
        else {
            ViewInfoRequestCall();
        }

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        btn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }





    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void ViewInfoRequestCall() {

        dialog = new Dialog(InfoList_Activity.this, R.style.NewProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progroess_popup);
        dialog.show();

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<LiftwellInfo_Response> call = apiInterface.LiftWellInfoCall(RestUtils.getContentType(), jobFetchAddressRequest());
        Log.w(TAG,"Liftwell Info url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<LiftwellInfo_Response>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<LiftwellInfo_Response> call, Response<LiftwellInfo_Response> response) {

                Log.w(TAG,"Liftwell Info Response" + new Gson().toJson(response.body()));

                if (response.body() != null) {
                    message = response.body().getMessage();
                    if (200 == response.body().getCode()) {

                        dataBean = response.body().getData();
                        dialog.dismiss();

                        if (dataBean.getBRCODE()!= null){

                            txt_BrCode.setText( dataBean.getBRCODE());
                            txt_BrCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getJOBNO()!= null){

                            txt_Jobno.setText( dataBean.getJOBNO());
                            txt_Jobno.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getCUSNAME()!= null){

                            txt_Cusname.setText( dataBean.getCUSNAME());
                            txt_Cusname.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getECNO()!= null){

                            txt_Ecno.setText( dataBean.getECNO());
                            txt_Ecno.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getBRCODE()!= null){

                            txt_BrCode.setText( dataBean.getBRCODE());
                            txt_BrCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getCUST_ADDRESS()!= null){


                            Log.e("Nish",""+dataBean.getCUST_ADDRESS());
                            txt_CustAd.setText( dataBean.getCUST_ADDRESS());
                            txt_CustAd.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getINS_ADDRESS()!= null){

                            txt_InsAd.setText(dataBean.getINS_ADDRESS());
                            txt_InsAd.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getNOUNITS()!= null){

                            txt_Nounits.setText(dataBean.getNOUNITS());
                            txt_Nounits.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getDMODEL()!= null){

                            txt_Dmodel.setText(dataBean.getDMODEL());
                            txt_Dmodel.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getLOAD_SPEED()!= null){

                            txt_Loadspeed.setText(dataBean.getLOAD_SPEED());
                            txt_Loadspeed.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getSPEED()!= null){

                            txt_Speed.setText(dataBean.getSPEED());
                            txt_Speed.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getBRCODE()!= null){

                            txt_BrCode.setText(dataBean.getBRCODE());
                            txt_BrCode.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getFLOORS()!= null){

                            txt_Floors.setText(dataBean.getFLOORS());
                            txt_Floors.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getNOCARENT()!= null){

                            txt_Nocarent.setText(dataBean.getNOCARENT());
                            txt_Nocarent.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getLIFTWELL()!= null){

                            txt_Liftwell.setText( dataBean.getLIFTWELL());
                            txt_Liftwell.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getLAND_ENTRANCE()!= null){

                            txt_Landent.setText(dataBean.getLAND_ENTRANCE());
                            txt_Landent.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getCLR_OPEN()!= null){

                            txt_ClrOpen.setText(dataBean.getCLR_OPEN());
                            txt_ClrOpen.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }

                        if (dataBean.getFRAME_SIZE()!= null){

                            txt_Framesize.setText(dataBean.getFRAME_SIZE());
                            txt_Framesize.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }


                    } else {
                        dialog.dismiss();

                    }
                }
            }

            @Override
            public void onFailure(Call<LiftwellInfo_Response> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private JobFetchAddressRequest jobFetchAddressRequest() {
        JobFetchAddressRequest jobFetchAddressRequest = new JobFetchAddressRequest();
        jobFetchAddressRequest.setJob_id(job_id);

        Log.w(TAG,"Liftwell info Request "+ new Gson().toJson(jobFetchAddressRequest));
        return jobFetchAddressRequest;
    }

}
