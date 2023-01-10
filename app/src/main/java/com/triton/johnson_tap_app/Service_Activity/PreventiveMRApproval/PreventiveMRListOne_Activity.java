package com.triton.johnson_tap_app.Service_Activity.PreventiveMRApproval;

import static com.triton.johnson_tap_app.Db.DbUtil.db;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.triton.johnson_tap_app.Db.CommonUtil;
import com.triton.johnson_tap_app.Db.DbHelper;
import com.triton.johnson_tap_app.Db.DbUtil;
import com.triton.johnson_tap_app.Service_Activity.ServicesActivity;
import com.triton.johnson_tap_app.Service_Adapter.PreventiveMRListOne_Adapter;
import com.triton.johnson_tap_app.R;
import com.triton.johnson_tap_app.api.APIInterface;
import com.triton.johnson_tap_app.api.RetrofitClient;
import com.triton.johnson_tap_app.interfaces.QuantityListener;
import com.triton.johnson_tap_app.materialeditext.MaterialEditText;
import com.triton.johnson_tap_app.requestpojo.Job_status_updateRequest;
import com.triton.johnson_tap_app.requestpojo.ServiceUserdetailsRequestResponse;
import com.triton.johnson_tap_app.responsepojo.Fetch_MrList_Response;
import com.triton.johnson_tap_app.responsepojo.Job_status_updateResponse;
import com.triton.johnson_tap_app.responsepojo.Retrive_LocalValueResponse;
import com.triton.johnson_tap_app.responsepojo.SuccessResponse;
import com.triton.johnson_tap_app.utils.RestUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreventiveMRListOne_Activity extends AppCompatActivity implements QuantityListener {

    private String TAG ="MRListONE";

    MaterialEditText partnameMaterialEdittext, partnoMaterialEdittext, quantityMaterialEdittext;
    ImageButton imgbtnSearch;
    RecyclerView recyclerView;
    ImageView iv_back,img_clearsearch,iv_pause;
    Button submitButton, prevButton;
    FloatingActionButton addButton;
    String se_user_mobile_no, se_user_name, se_id,check_id, service_title,job_id,status,str_job_status;
    String strPartname, strPartno,strPartid, strQuantity,compno,sertype,str_Partid,str_Partno,str_Quantity =" ",str_Partname,str_mr1 ="",str_mr2="",str_mr3="",str_mr4="",str_mr5="",str_mr6="",str_mr7="",str_mr8="",str_mr9="",str_mr10="",message;
    private String PetBreedType = "";
    Context context;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    TextView txt_Jobid,txt_Starttime;
    String str_StartTime,Quantity;
    double Latitude ,Logitude;
    String address = "";

    ArrayList<String> arli_Partname;
    ArrayList<String>  arli_Partno;
    ArrayList<String> arli_Partid;
    ArrayList<String> arli_Quantity;
    int index = 0;

    String Partno = "",PartName, PartQty,PartID;
    String[] strLi_PartNo, strLi_PartName,strLi_PartQty,strLi_PartID;

    List<Retrive_LocalValueResponse.Data.MrDatum> databean;
    private List<Fetch_MrList_Response.Datum> breedTypedataBeanList;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_preventive_mrlist_one);

        context = this;
        CommonUtil.dbUtil = new DbUtil(context);
        CommonUtil.dbUtil.open();
        CommonUtil.dbHelper = new DbHelper(context);


        partnameMaterialEdittext = findViewById(R.id.part_name);
        partnoMaterialEdittext = findViewById(R.id.part_no);
        quantityMaterialEdittext = findViewById(R.id.no);
        imgbtnSearch = findViewById(R.id.imgbtn_search);
        iv_back = findViewById(R.id.iv_back);
        addButton = findViewById(R.id.btn_add);
        submitButton = findViewById(R.id.btn_sumbit);
        prevButton = findViewById(R.id.btn_show);
        recyclerView = findViewById(R.id.recyclerView);
        iv_pause = findViewById(R.id.ic_paused);
        txt_Starttime = findViewById(R.id.txt_starttime);
        txt_Jobid = findViewById(R.id.txt_jobid);

//        partnameMaterialEdittext.setOnTouchListener((view, motionEvent) -> {
//
//            partnameMaterialEdittext.setFocusableInTouchMode(true);
//            partnoMaterialEdittext.setFocusableInTouchMode(true);
//            quantityMaterialEdittext.setFocusableInTouchMode(true);
//            return false;
//        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        se_id = sharedPreferences.getString("_id", "default value");
        se_user_mobile_no = sharedPreferences.getString("user_mobile_no", "default value");
        se_user_name = sharedPreferences.getString("user_name", "default value");
        compno = sharedPreferences.getString("compno","123");
        sertype = sharedPreferences.getString("sertype","123");
        service_title = sharedPreferences.getString("service_title", "default value");
        job_id = sharedPreferences.getString("job_id", "default value");
        Log.e("Name",""+ service_title);
        Log.e("Job ID",""+ job_id);

        str_StartTime = sharedPreferences.getString("starttime","");
        str_StartTime = str_StartTime.replaceAll("[^0-9-:]", " ");
        Log.e("Start Time",str_StartTime);
        txt_Jobid.setText("Job ID : " + job_id);
        txt_Starttime.setText("Start Time : " + str_StartTime);

        Latitude = Double.parseDouble(sharedPreferences.getString("lati","0.00000"));
        Logitude = Double.parseDouble(sharedPreferences.getString("long","0.00000"));
        address =sharedPreferences.getString("add","Chennai");
        Log.e("Location",""+Latitude+""+Logitude+""+address);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           // service_title = extras.getString("service_title");
            status = extras.getString("status");
            Log.e("Status", "" + status);
            // Log.d("title",service_title);
        }
        if (extras != null) {
           // job_id = extras.getString("job_id");
//            strPartname = extras.getString("partname");
//            strPartno = extras.getString("partno");
            str_mr1 = extras.getString("mr1");
            str_mr2 = extras.getString("mr2");
            str_mr3 = extras.getString("mr3");
            str_mr4 = extras.getString("mr4");
            str_mr5 = extras.getString("mr5");
            str_mr6 = extras.getString("mr6");
            str_mr7 = extras.getString("mr7");
            str_mr8 = extras.getString("mr8");
            str_mr9 = extras.getString("mr9");
            str_mr10 = extras.getString("mr10");
            Log.e("JobId","" + job_id );

//            Partno = extras.getString("partno");
//            PartName = extras.getString("partname");
//            Log.e("Part No","" + Partno);
//            if (Partno !=null){
//                strLi_PartNo = Partno.split(",");
//                arli_Partno = new ArrayList<String>(Arrays.asList(strLi_PartNo));
//                Log.e("Part No List",""+ arli_Partno);
//                strLi_PartName = PartName.split(",");
//                arli_Partname = new ArrayList<String>(Arrays.asList(strLi_PartName));
//                Log.e("Part Name List",""+ arli_Partname);
//            }

//            partnameMaterialEdittext.setText(strPartname);
//            partnoMaterialEdittext.setText(strPartno);
//            quantityMaterialEdittext.setFocusableInTouchMode(true);

        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("service_title", service_title);
        editor.apply();

//        Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
//
//        Log.e("MR List Count",""+ cursor.getCount());

        if (status.equals("pause")){
            Log.e("Inside", "Paused Job");

            arli_Partid = new ArrayList<>();
            arli_Partname = new ArrayList<>();
            arli_Partno = new ArrayList<>();
            arli_Quantity = new ArrayList<>();

            retrive_LocalValue();
           // addMrData(job_id);

            iv_pause.setVisibility(View.VISIBLE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    createLocaldata();
                    Intent send = new Intent(PreventiveMRListOne_Activity.this, PreventiveMRListtwo_Activity.class);
                    send.putExtra("service_title",service_title);
                    send.putExtra("job_id",job_id);
                    send.putExtra("mr1", str_mr1);
                    send.putExtra("mr2", str_mr2);
                    send.putExtra("mr3", str_mr3);
                    send.putExtra("mr4", str_mr4);
                    send.putExtra("mr5", str_mr5);
                    send.putExtra("mr6", str_mr6);
                    send.putExtra("mr7", str_mr7);
                    send.putExtra("mr8", str_mr8);
                    send.putExtra("mr9", str_mr9);
                    send.putExtra("mr10", str_mr10);
                    send.putExtra("status", status);
                    startActivity(send);
//                    Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
//
//                    Log.e("MR Count",""+ cursor.getCount());
//
//                    String strQuantity = quantityMaterialEdittext.getText().toString();
//                    String strPartname = partnameMaterialEdittext.getText().toString();
//                    String strPartno = partnoMaterialEdittext.getText().toString();
//
//
//                    Log.e("MR List Data","" + strQuantity + " " + strPartname + " " + strPartno );
//
//                    if (strQuantity.equals("")){
//                        quantityMaterialEdittext.setError("Please Enter the Quantity");
//                    } else if(strPartname.equals("")){
//                        partnameMaterialEdittext.setError("Please Select the Part Name");
//                    }else if(strPartno.equals("")){
//                        partnoMaterialEdittext.setError("Please Select the Part Name");
//                    }
//                    else {
//
//                        Cursor cur = CommonUtil.dbUtil.getPMR(job_id);
//
//                        Log.e("Count", ""+ cur.getCount());
//
//                        if (cur.getCount()>9){
//                            Toast.makeText(PreventiveMRListOne_Activity.this,"Only 10 Items to be Addes", Toast.LENGTH_SHORT).show();
//
//                        }
//                        else {
//                            CommonUtil.dbUtil.addPMR(strPartname, strPartno, strQuantity,job_id);
//                            Toast.makeText(PreventiveMRListOne_Activity.this,"Successfully Added", Toast.LENGTH_SHORT).show();
//                            addMrData(job_id);
//                            partnameMaterialEdittext.setText("");
//                            partnoMaterialEdittext.setText("");
//                            quantityMaterialEdittext.setText("");
//                        }

//                    Intent send = new Intent(BreakdownMRListOne_Activity.this, BreakdownMRListOne_Activity.class);
//                    startActivity(send);

                //    }

                }
            });

            imgbtnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Hi Nish","pause Search");
                    createLocaldata();
                    Intent send = new Intent(PreventiveMRListOne_Activity.this, PreventiveMRListtwo_Activity.class);
                    send.putExtra("service_title",service_title);
                    send.putExtra("job_id",job_id);
                    send.putExtra("mr1", str_mr1);
                    send.putExtra("mr2", str_mr2);
                    send.putExtra("mr3", str_mr3);
                    send.putExtra("mr4", str_mr4);
                    send.putExtra("mr5", str_mr5);
                    send.putExtra("mr6", str_mr6);
                    send.putExtra("mr7", str_mr7);
                    send.putExtra("mr8", str_mr8);
                    send.putExtra("mr9", str_mr9);
                    send.putExtra("mr10", str_mr10);
                    send.putExtra("status", status);

                    startActivity(send);

                }
            });

        }

        else{
            Log.e("Inside", "New Job ");

            iv_pause.setVisibility(View.VISIBLE);

            arli_Partid = new ArrayList<>();
            arli_Partname = new ArrayList<>();
            arli_Partno = new ArrayList<>();
            arli_Quantity = new ArrayList<>();

//            Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
//            Log.e("List Count",""+cur.getCount());

                getMRList();
           // addMrData(job_id);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
                    Log.e("List Count",""+cur.getCount());
//
//                    if (cur.getCount()>9){
//                        Toast.makeText(context,"Only 10 Items to be Addes", Toast.LENGTH_SHORT).show();
//
//                    }
               //     else{
                        Intent send = new Intent(PreventiveMRListOne_Activity.this, PreventiveMRListtwo_Activity.class);
                        send.putExtra("service_title",service_title);
                        send.putExtra("job_id",job_id);
                        send.putExtra("mr1", str_mr1);
                        send.putExtra("mr2", str_mr2);
                        send.putExtra("mr3", str_mr3);
                        send.putExtra("mr4", str_mr4);
                        send.putExtra("mr5", str_mr5);
                        send.putExtra("mr6", str_mr6);
                        send.putExtra("mr7", str_mr7);
                        send.putExtra("mr8", str_mr8);
                        send.putExtra("mr9", str_mr9);
                        send.putExtra("mr10", str_mr10);
                        send.putExtra("status", status);
                        startActivity(send);
               //     }



////                    Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
////
////                    Log.e("MR Count",""+ cursor.getCount());
////
////                    String strQuantity = quantityMaterialEdittext.getText().toString();
////
////
////                    Log.e("MR List Data","" + strQuantity + " " + strPartname + " " + strPartno );
////
////                    if (strQuantity.equals("")){
////                        quantityMaterialEdittext.setError("Please Enter the Quantity");
////                    } else if(strPartname.equals("")){
////                        partnameMaterialEdittext.setError("Please Select the Part Name");
////                    }else if(strPartno.equals("")){
////                        partnoMaterialEdittext.setError("Please Select the Part Name");
////                    }
////                    else {
////
////                        Cursor cur = CommonUtil.dbUtil.getPMR(job_id);
////
////                        Log.e("Count", ""+ cur.getCount());
////
////                        if (cur.getCount()>9){
////                            Toast.makeText(PreventiveMRListOne_Activity.this,"Only 10 Items to be Addes", Toast.LENGTH_SHORT).show();
////
////                        }
////                        else {
////                            CommonUtil.dbUtil.addPMR(strPartname, strPartno, strQuantity,job_id);
////                            Toast.makeText(PreventiveMRListOne_Activity.this,"Successfully Added", Toast.LENGTH_SHORT).show();
////                            addMrData(job_id);
////                            partnameMaterialEdittext.setText("");
////                            partnoMaterialEdittext.setText("");
////                            quantityMaterialEdittext.setText("");
////                        }
//
////                    Intent send = new Intent(BreakdownMRListOne_Activity.this, BreakdownMRListOne_Activity.class);
////                    startActivity(send);
//
//                //    }
//
                }
            });
//
            imgbtnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent send = new Intent(PreventiveMRListOne_Activity.this, PreventiveMRListtwo_Activity.class);
                    send.putExtra("service_title",service_title);
                    send.putExtra("job_id",job_id);
                    send.putExtra("mr1", str_mr1);
                    send.putExtra("mr2", str_mr2);
                    send.putExtra("mr3", str_mr3);
                    send.putExtra("mr4", str_mr4);
                    send.putExtra("mr5", str_mr5);
                    send.putExtra("mr6", str_mr6);
                    send.putExtra("mr7", str_mr7);
                    send.putExtra("mr8", str_mr8);
                    send.putExtra("mr9", str_mr9);
                    send.putExtra("mr10", str_mr10);
                    send.putExtra("status", status);

                    startActivity(send);

                }
            });


        }

        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());

                alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Are you sure to pause this job ?")
                        .setMessage(date)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context,"Lat : " + Latitude + "Long : " + Logitude + "Add : " + address,Toast.LENGTH_LONG).show();
                                str_job_status = "Job Paused";
                                Job_status_update();
                                createLocalValueCall();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals("pause")){
                    Log.e("Hi Nish","Pause JOB");
                    createLocaldata();
                }
                Intent send = new Intent(PreventiveMRListOne_Activity.this, MRForms_PreventiveMRActivity.class);
                send.putExtra("service_title",service_title);
                send.putExtra("job_id",job_id);
                send.putExtra("mr1", str_mr1);
                send.putExtra("mr2", str_mr2);
                send.putExtra("mr3", str_mr3);
                send.putExtra("mr4", str_mr4);
                send.putExtra("mr5", str_mr5);
                send.putExtra("mr6", str_mr6);
                send.putExtra("mr7", str_mr7);
                send.putExtra("mr8", str_mr8);
                send.putExtra("mr9", str_mr9);
                send.putExtra("mr10", str_mr10);
                send.putExtra("status", status);
                startActivity(send);

            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (status.equals("pause")){
                    Log.e("Hi Nish","Pause JOB");
                    createLocaldata();
                }

                Intent send = new Intent(PreventiveMRListOne_Activity.this, MRForms_PreventiveMRActivity.class);
                send.putExtra("service_title",service_title);
                send.putExtra("job_id",job_id);
                send.putExtra("mr1", str_mr1);
                send.putExtra("mr2", str_mr2);
                send.putExtra("mr3", str_mr3);
                send.putExtra("mr4", str_mr4);
                send.putExtra("mr5", str_mr5);
                send.putExtra("mr6", str_mr6);
                send.putExtra("mr7", str_mr7);
                send.putExtra("mr8", str_mr8);
                send.putExtra("mr9", str_mr9);
                send.putExtra("mr10", str_mr10);
                send.putExtra("status", status);
                startActivity(send);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
//
//                Log.e("MR Count",""+ cursor.getCount());

                if (status.equals("pause")){
                    Log.e("Hi Nish","Pause JOB");
                    createLocaldata();
                }
//
//                Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
//                Log.e("List Count",""+cur.getCount());

//                if (cur.getCount()>0){
//
//                    createLocaldata();
//                    Intent send = new Intent(PreventiveMRListOne_Activity.this, TechnicianSignature_PreventiveMRActivity.class);
//                    send.putExtra("service_title",service_title);
//                    send.putExtra("job_id",job_id);
//                    send.putExtra("mr1", str_mr1);
//                    send.putExtra("mr2", str_mr2);
//                    send.putExtra("mr3", str_mr3);
//                    send.putExtra("mr4", str_mr4);
//                    send.putExtra("mr5", str_mr5);
//                    send.putExtra("mr6", str_mr6);
//                    send.putExtra("mr7", str_mr7);
//                    send.putExtra("mr8", str_mr8);
//                    send.putExtra("mr9", str_mr9);
//                    send.putExtra("mr10", str_mr10);
//                    send.putExtra("status", status);
//                    startActivity(send);
//                }
//                else{
//
//                    alertDialog = new AlertDialog.Builder(context)
//                            //.setTitle("Please Login to Continue!")
//                            .setMessage("Please add MR")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    alertDialog.dismiss();
//                                }
//                            })
//                            .show();
//
//                }


//
//                Log.e("Size",""+arli_Partno.size());
//                for (int i = 0 ; i < arli_Partno.size(); i++){
//
//                    Partno = arli_Partno.get(i).toString();
//                    Log.e("Part No Final",""+Partno);
//
//                }

                getMRList();

                if (arli_Partno.size() == 0){

                    alertDialog = new AlertDialog.Builder(context)
                            //.setTitle("Please Login to Continue!")
                            .setMessage("Please add MR")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                }
                            })
                            .show();
                }
                else if(arli_Quantity.contains("0")){

                    alertDialog = new AlertDialog.Builder(context)
                            //.setTitle("Please Login to Continue!")
                            .setMessage("Please add MR Quantity")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
//                    createLocaldata();
                    Intent send = new Intent(PreventiveMRListOne_Activity.this, TechnicianSignature_PreventiveMRActivity.class);
                    send.putExtra("service_title",service_title);
                    send.putExtra("job_id",job_id);
                    send.putExtra("mr1", str_mr1);
                    send.putExtra("mr2", str_mr2);
                    send.putExtra("mr3", str_mr3);
                    send.putExtra("mr4", str_mr4);
                    send.putExtra("mr5", str_mr5);
                    send.putExtra("mr6", str_mr6);
                    send.putExtra("mr7", str_mr7);
                    send.putExtra("mr8", str_mr8);
                    send.putExtra("mr9", str_mr9);
                    send.putExtra("mr10", str_mr10);
                    send.putExtra("status", status);
                    startActivity(send);
                }
            }
        });
    }

    public void getMRList() {

        arli_Partid = new ArrayList<>();
        arli_Partname = new ArrayList<>();
        arli_Partno = new ArrayList<>();
        arli_Quantity = new ArrayList<>();

        Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
        Log.e("List Count",""+cur.getCount());


        if (cur.getCount()>0 &&cur.moveToFirst()){

            do {
               str_Partid = cur.getString(cur.getColumnIndexOrThrow(DbHelper.ID));
               str_Partname= cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NAME));
                str_Partname = str_Partname.replace("*","'");
               str_Partno= cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NO));
               str_Quantity = cur.getString(cur.getColumnIndexOrThrow(DbHelper.QUANTITY));
                Log.e("Quantity",""+str_Quantity);
                arli_Partid.add(str_Partid);
                arli_Partname.add(str_Partname);
                arli_Partno.add(str_Partno);
                arli_Quantity.add(str_Quantity);
            } while (cur.moveToNext());
        }
        cur.close();

        Log.e("Nish",""+arli_Partid.size());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        PreventiveMRListOne_Adapter adapter = new PreventiveMRListOne_Adapter(arli_Partid,arli_Partname,arli_Partno, arli_Quantity,context,this);
        recyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener(new PreventiveMRListOne_Adapter.onItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                arli_Partid.remove(position);
//                adapter.notifyItemRemoved(position);
//            }
//        });
    }

    private void createLocalValueCall() {

        APIInterface apiInterface =  RetrofitClient.getClient().create((APIInterface.class));
        Call<SuccessResponse> call = apiInterface.createLocalValueCallPMR(RestUtils.getContentType(),createLocalRequest());
        Log.w(TAG,"Create Local Value url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {

                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));

                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (response.body().getCode() == 200){

                        if(response.body().getData() != null){

                            Log.d("msg",message);

//                            CommonUtil.dbUtil.deleteMRTable(job_id,"1",service_title);
//
//                            Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"1",service_title);
//                            Log.e("List Count",""+cur.getCount());

                            Intent send = new Intent(context, ServicesActivity.class);
                            startActivity(send);
                        }

                    }else{


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

    private ServiceUserdetailsRequestResponse createLocalRequest() {
        ServiceUserdetailsRequestResponse local = new ServiceUserdetailsRequestResponse();
        local.setJobId(job_id);
        local.setUserMobileNo(se_user_mobile_no);
        local.setSMU_SCH_COMPNO(compno);
        local.setSMU_SCH_SERTYPE(sertype);
        local.setEngSignature("-");

//        List<ServiceUserdetailsRequestResponse.MrDatum> mrData = new ArrayList<>();
//
//        Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
//
//        Log.e("MR Count",""+ cursor.getCount());
//
//
//        int i =0;
//        if (cursor.getCount()>0 &&cursor.moveToFirst()) {
//
//            do {
//                str_Partid = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_MR_ID));
//                str_Partname = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NAME));
//                str_Partno = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NO));
//                str_Quantity = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_QUANTITY));
//
//                int mynum = i+1;
//
//                ServiceUserdetailsRequestResponse.MrDatum mrDatum = new ServiceUserdetailsRequestResponse.MrDatum();
//                mrDatum.setTitle("Mr"+mynum);
//                if(mynum==1) {
//                    //   mrDatum.setValue(mr1);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==2) {
//                    // mrDatum.setValue(mr2);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==3) {
//
//                    //   mrDatum.setValue(mr3);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==4) {
//
//                    //   mrDatum.setValue(mr4);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==5) {
//
//                    //   mrDatum.setValue(mr5);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==6) {
//
//                    //  mrDatum.setValue(mr6);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==7) {
//
//                    //  mrDatum.setValue(mr7);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==8) {
//
//                    // mrDatum.setValue(mr8);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==9) {
//
//                    //  mrDatum.setValue(mr9);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==10) {
//
//                    //  mrDatum.setValue(mr10);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                }
//
//                Log.e("Nish",""+mynum + mrDatum.getTitle() +" :"+ mrDatum.getValue());
//                mrData.add(mrDatum);
//
//                i++;
//
//            }while (cursor.moveToNext());
//        }
//
//        local.setMrData(mrData);

        List<ServiceUserdetailsRequestResponse.MrDatum> mrData = new ArrayList<>();

        Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
        Log.e("List Count",""+cur.getCount());

        arli_Partno.clear();
        arli_Partname.clear();
        arli_Quantity.clear();

        if (cur.getCount()>0 &&cur.moveToFirst()) {

            do {
                str_Partid = cur.getString(cur.getColumnIndexOrThrow(DbHelper.ID));
                str_Partname = cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NAME));
                str_Partno = cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NO));
                str_Quantity = cur.getString(cur.getColumnIndexOrThrow(DbHelper.QUANTITY));
                arli_Partname.add(str_Partname);
                arli_Partno.add(str_Partno);
                arli_Quantity.add(str_Quantity);
                Log.e("Part List No",""+arli_Partno);
                Log.e("Part List Name",""+arli_Partname);
                Log.e("Part List Qty",""+arli_Quantity);

            }while (cur.moveToNext());

            for (int i = 0; i<arli_Partno.size();i++){
                int mynum = i+1;

                ServiceUserdetailsRequestResponse.MrDatum mrDatum = new ServiceUserdetailsRequestResponse.MrDatum();

                mrDatum.setValue("MR "+mynum);
                mrDatum.setPartno(arli_Partno.get(i));
                mrDatum.setPartname(arli_Partname.get(i));
                mrDatum.setReq(arli_Quantity.get(i));

                mrData.add(mrDatum);

            }

        }

        Log.e("Nish",""+ mrData.size());
        Log.e(TAG,"Request field"+ new Gson().toJson(mrData));
        local.setMrData(mrData);

        Log.w(TAG,"Local Request "+ new Gson().toJson(local));
        return local;
    }

    private void Job_status_update() {

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<Job_status_updateResponse> call = apiInterface.PreventiveMrJobWorkStatusResponseCall(RestUtils.getContentType(), job_status_updateRequest());
        Log.w(TAG,"SignupResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<Job_status_updateResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<Job_status_updateResponse> call, @NonNull retrofit2.Response<Job_status_updateResponse> response) {

                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));
                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (200 == response.body().getCode()) {
                        if(response.body().getData() != null){

                            Log.d("msg",message);
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
        custom.setSMU_SCH_COMPNO(compno);
        custom.setSMU_SCH_SERTYPE(sertype);
        custom.setJOB_START_LONG(Logitude);
        custom.setJOB_START_LAT(Latitude);
        custom.setJOB_LOCATION(address);
        Log.e("CompNo",""+compno);
        Log.e("SertYpe", ""+sertype);
        Log.w(TAG,"loginRequest "+ new Gson().toJson(custom));
        return custom;
    }


    private void retrive_LocalValue() {

            APIInterface apiInterface =  RetrofitClient.getClient().create((APIInterface.class));
            Call<Retrive_LocalValueResponse> call = apiInterface.retriveLocalValueCallPMR(RestUtils.getContentType(),localRequest());
            Log.w(TAG,"Create Local Value url  :%s"+" "+ call.request().url().toString());

            call.enqueue(new Callback<Retrive_LocalValueResponse>() {
                @Override
                public void onResponse(Call<Retrive_LocalValueResponse> call, Response<Retrive_LocalValueResponse> response) {

                    Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));

                    if (response.body() != null) {
                        message = response.body().getMessage();

                        if (response.body().getCode() == 200) {

                            if(response.body().getData() != null){

                                Log.d("msg",message);

                                databean = response.body().getData().getMr_data();

                                Log.e("Nish",""+databean.toString());
                                Log.e("Nish",""+databean.size());

                       //         if (databean.size() > 0){

//                                    CommonUtil.dbUtil.Preportdelete(job_id);
                             //   }

                                for (int i =0 ; i < databean.size(); i++){
                                    //  datre =  response.body().getData();

                                    strPartno = databean.get(i).getPartno();
                                    strPartname = databean.get(i).getPartname();
                                    strPartname = strPartname.replace("'","*");
                                    strQuantity = databean.get(i).getReq();
                                    //commonutil

                                    Log.e("Part 1",""+strPartno);
                                    Log.e("Part 2",""+strPartname);
                                    Log.e("Part 3",""+strQuantity);

                                    Log.e("jobID",""+ job_id);


//                                    if(!CommonUtil.dbUtil.hasPMR(strPartname,job_id,strPartno,strQuantity)) {
//                                        Log.e("Nish","inside");
//
//                                        CommonUtil.dbUtil.addPMR(strPartname, strPartno, strQuantity, job_id);
//                                    } else{
//                                        Log.e("Nish","outside");
//
//                                    }
                                }

                                if (CommonUtil.dbUtil.hasMRList(strPartno,strPartname,"2",job_id,service_title)) {
                                    Log.e("Hi Nish", "Had Data");
                                    CommonUtil.dbUtil.deleteMRList(strPartno, strPartname, "2", job_id, service_title);
                                    Cursor cur = CommonUtil.dbUtil.getMRList(job_id, "2", service_title);
                                    Log.e("List Count", "" + cur.getCount());
                                    CommonUtil.dbUtil.addMRList(strPartno, strPartname, "2", job_id, service_title, strQuantity);
                                    Cursor curs = CommonUtil.dbUtil.getMRList(job_id, "2", service_title);
                                    Log.e("List Count", "" + curs.getCount());
                                    Log.e("Nish","outside");
                                }
                              //  addMrData(job_id);
                                getMRList();
                            }
                        }
                    }
                    else{
                        Toasty.warning(getApplicationContext(),""+message,Toasty.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Retrive_LocalValueResponse> call, Throwable t) {

                    Log.e("On Failure", "--->" + t.getMessage());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private Job_status_updateRequest localRequest() {
            Job_status_updateRequest custom = new Job_status_updateRequest();
            custom.setUser_mobile_no(se_user_mobile_no);
            custom.setJobId(job_id);
            custom.setSMU_SCH_COMPNO(compno);
            //  custom.setSMU_SCH_SERTYPE(sertype);
            Log.w(TAG,"Request Data "+ new Gson().toJson(custom));
            return custom;
        }

    @Override
    public void onBackPressed() {

        if (status.equals("pause")){
            Log.e("Hi Nish","Pause JOB");
            createLocaldata();
        }
        Intent send = new Intent(PreventiveMRListOne_Activity.this, MRForms_PreventiveMRActivity.class);
        send.putExtra("service_title",service_title);
        send.putExtra("job_id",job_id);
        send.putExtra("mr1", str_mr1);
        send.putExtra("mr2", str_mr2);
        send.putExtra("mr3", str_mr3);
        send.putExtra("mr4", str_mr4);
        send.putExtra("mr5", str_mr5);
        send.putExtra("mr6", str_mr6);
        send.putExtra("mr7", str_mr7);
        send.putExtra("mr8", str_mr8);
        send.putExtra("mr9", str_mr9);
        send.putExtra("mr10", str_mr10);
        send.putExtra("status", status);
        startActivity(send);
    }

    private void createLocaldata() {

        Log.e("Nish","Create Local Data");

        APIInterface apiInterface =  RetrofitClient.getClient().create((APIInterface.class));
        Call<SuccessResponse> call = apiInterface.createLocalValueCallPMR(RestUtils.getContentType(),createLocalDataRequest());
        Log.w(TAG,"Create Local Pause Value url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {

                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));

                if (response.body() != null) {
                    message = response.body().getMessage();

                    if (response.body().getCode() == 200){

                        if(response.body().getData() != null){

                            Log.d("msg",message);

//                            Intent send = new Intent(BreakdownMRListOne_Activity.this, ServicesActivity.class);
//                            startActivity(send);
                        }

                    }else{
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

    private ServiceUserdetailsRequestResponse createLocalDataRequest() {
        ServiceUserdetailsRequestResponse local = new ServiceUserdetailsRequestResponse();
        local.setJobId(job_id);
        local.setUserMobileNo(se_user_mobile_no);
        local.setSMU_SCH_COMPNO(compno);
        local.setSMU_SCH_SERTYPE(sertype);
        local.setEngSignature("-");

//        Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);
//
//        Log.e("MR Count",""+ cursor.getCount());
//
//
//        int i =0;
//        if (cursor.getCount()>0 &&cursor.moveToFirst()) {
//
//            do {
//                str_Partid = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_MR_ID));
//                str_Partname = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NAME));
//                str_Partno = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NO));
//                str_Quantity = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_QUANTITY));
//
//                int mynum = i+1;
//
//                ServiceUserdetailsRequestResponse.MrDatum mrDatum = new ServiceUserdetailsRequestResponse.MrDatum();
//                mrDatum.setTitle("Mr"+mynum);
//                if(mynum==1) {
//                    //   mrDatum.setValue(mr1);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==2) {
//                    // mrDatum.setValue(mr2);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==3) {
//
//                    //   mrDatum.setValue(mr3);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==4) {
//
//                    //   mrDatum.setValue(mr4);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==5) {
//
//                    //   mrDatum.setValue(mr5);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==6) {
//
//                    //  mrDatum.setValue(mr6);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==7) {
//
//                    //  mrDatum.setValue(mr7);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==8) {
//
//                    // mrDatum.setValue(mr8);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==9) {
//
//                    //  mrDatum.setValue(mr9);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                } else if (mynum==10) {
//
//                    //  mrDatum.setValue(mr10);
//                    mrDatum.setPartno(str_Partno);
//                    mrDatum.setPartname(str_Partname);
//                    mrDatum.setReq(str_Quantity);
//                }
//
//                Log.e("Nish",""+mynum + mrDatum.getTitle() +" :"+ mrDatum.getValue());
//                mrData.add(mrDatum);
//
//                i++;
//
//            }while (cursor.moveToNext());
//        }
//
//        local.setMrData(mrData);
        List<ServiceUserdetailsRequestResponse.MrDatum> mrData = new ArrayList<>();

        Cursor cur = CommonUtil.dbUtil.getMRList(job_id,"2",service_title);
        Log.e("List Count",""+cur.getCount());

        arli_Partno.clear();
        arli_Partname.clear();
        arli_Quantity.clear();

        if (cur.getCount()>0 &&cur.moveToFirst()) {

            do {
                str_Partid = cur.getString(cur.getColumnIndexOrThrow(DbHelper.ID));
                str_Partname = cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NAME));
                str_Partno = cur.getString(cur.getColumnIndexOrThrow(DbHelper.PART_NO));
                str_Quantity = cur.getString(cur.getColumnIndexOrThrow(DbHelper.QUANTITY));
                arli_Partname.add(str_Partname);
                arli_Partno.add(str_Partno);
                arli_Quantity.add(str_Quantity);
                Log.e("Part List No",""+arli_Partno);
                Log.e("Part List Name",""+arli_Partname);
                Log.e("Part List Qty",""+arli_Quantity);

            }while (cur.moveToNext());

            for (int i = 0; i<arli_Partno.size();i++){
                int mynum = i+1;

                ServiceUserdetailsRequestResponse.MrDatum mrDatum = new ServiceUserdetailsRequestResponse.MrDatum();

                mrDatum.setValue("MR "+mynum);
                mrDatum.setPartno(arli_Partno.get(i));
                mrDatum.setPartname(arli_Partname.get(i));
                mrDatum.setReq(arli_Quantity.get(i));

                mrData.add(mrDatum);

            }

        }


        Log.e("Nish",""+ mrData.size());
        Log.e(TAG,"Request field"+ new Gson().toJson(mrData));
        local.setMrData(mrData);


        Log.w(TAG,"Local Request "+ new Gson().toJson(local));
        return local;
    }

    private void addMrData(String job_id) {

//        ArrayList<String> arli_Partid = new ArrayList<>();
//        ArrayList<String> arli_Partname = new ArrayList<>();
//        ArrayList<String> arli_Partno =  new ArrayList<>();
//        ArrayList<String> arli_Quantity =  new ArrayList<>();

//        arli_Partid.clear();
//        arli_Partname.clear();
//        arli_Partno.clear();
//        arli_Quantity.clear();

        arli_Partid = new ArrayList<>();
        arli_Partname = new ArrayList<>();
        arli_Partno = new ArrayList<>();
        arli_Quantity = new ArrayList<>();


        Cursor cursor = CommonUtil.dbUtil.getPMR(job_id);

        Log.e("MR Count",""+ cursor.getCount());

        if (cursor.getCount()>0 &&cursor.moveToFirst()){

            do {
                String str_Partid = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_MR_ID));
                String str_Partname = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NAME));
                String str_Partno = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_PART_NO));
                String str_Quantity = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.P_QUANTITY));

                arli_Partid.add(str_Partid);
                arli_Partname.add(str_Partname);
                arli_Partno.add(str_Partno);
                arli_Quantity.add(str_Quantity);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.e("Nish",""+arli_Partid.size());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        PreventiveMRListOne_Adapter adapter = new PreventiveMRListOne_Adapter(arli_Partid,arli_Partname,arli_Partno, arli_Quantity,context, this);
        recyclerView.setAdapter(adapter);

    }

    public void petBreedTypeSelectListener(String petbreedtitle, String petbreedid) {
        PetBreedType = petbreedtitle;
    }

    @Override
    public void onQuantityChange(EditText edt_Qty, String s, int position) {

        Quantity = s;
        Log.e("Remarks 123",""+ Quantity +" "+position);


    }
}