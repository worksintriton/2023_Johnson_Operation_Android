package com.triton.johnson_tap_app.Service_Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.triton.johnson_tap_app.PetBreedTypeSelectListener;
import com.triton.johnson_tap_app.R;
import com.triton.johnson_tap_app.Service_Activity.Preventive_Services.Customer_Details_preActivity;
import com.triton.johnson_tap_app.responsepojo.JobListResponse;

import java.util.List;

public class JobListAdapter_Preventive extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private  String TAG = "PetBreedTypesListAdapter";
    private Context context;
    JobListResponse.DataBean currentItem;
    private List<JobListResponse.DataBean> breedTypedataBeanList;
    private PetBreedTypeSelectListener petBreedTypeSelectListener;
    String service_title,status;
    SharedPreferences sharedPreferences;


    public JobListAdapter_Preventive(Context mcontext, List<JobListResponse.DataBean> breedTypedataBeanList, PetBreedTypeSelectListener petBreedTypeSelectListener, String mstatus) {
        this.context = mcontext;
        this.breedTypedataBeanList = breedTypedataBeanList;
        this.petBreedTypeSelectListener = petBreedTypeSelectListener;
        this.status = mstatus;

        Log.e("Status",""+status);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        service_title = sharedPreferences.getString("service_title","Preventive Maintenance");
        Log.e("Name",service_title);

    }

    public void filterPreventiveList(List<JobListResponse.DataBean> filterllist)
    {
        breedTypedataBeanList = filterllist;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_item_pre, parent, false);
        return new JobListAdapter_Preventive.ViewHolderOne(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((JobListAdapter_Preventive.ViewHolderOne) holder, position);


    }

    @SuppressLint("LogNotTimber")
    private void initLayoutOne(JobListAdapter_Preventive.ViewHolderOne holder, final int position) {
        currentItem = breedTypedataBeanList.get(position);

//        Intent intent = new Intent("message_subject_intent");
//        intent.putExtra("cust_name" , currentItem.getCUSNAME());
//        intent.putExtra("cont_no" , currentItem.getCONTNO());
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        if(currentItem.getJob_id() != null){
            holder.job_id.setText(currentItem.getJob_id());
            holder.cust_name.setText(currentItem.getCustomer_name());
            holder.pm_date.setText(currentItem.getPm_date());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        service_title = sharedPreferences.getString("service_title", "default value");
        Log.e("Name",service_title);

        holder.ll_root.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                breedTypedataBeanList.get(position).getJob_id();

                String s = breedTypedataBeanList.get(position).getJob_id();
                String compno = breedTypedataBeanList.get(position).getSMU_SCH_COMPNO();
                String sertype = breedTypedataBeanList.get(position).getSMU_SCH_SERTYPE();

                Log.e("A",""+compno);
                Log.e("A",""+sertype);
                Log.e("A",""+s);


                //   Toast.makeText(context, "value" + service_title, Toast.LENGTH_LONG).show();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("compno",compno);
                editor.putString("sertype", sertype);
                editor.putString("job_id",s);
                editor.apply();


                Intent n_act = new Intent(context, Customer_Details_preActivity.class);
                n_act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // n_act.putExtra("job_id", s);
                n_act.putExtra("status",status);
                context.startActivity(n_act);
            }
        });


    }
    @Override
    public int getItemCount() {
        return breedTypedataBeanList.size();
    }

    public void filterList(List<JobListResponse.DataBean> breedTypedataBeanListFiltered) {
        breedTypedataBeanList = breedTypedataBeanListFiltered;
        Log.w(TAG,"breedTypedataBeanList : "+new Gson().toJson(breedTypedataBeanList));

        notifyDataSetChanged();
    }




    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {
        public TextView job_id,cust_name,pm_date;
        public LinearLayout ll_root;

        public ViewHolderOne(View itemView) {
            super(itemView);

            job_id = itemView.findViewById(R.id.text);
            cust_name = itemView.findViewById(R.id.text1);
            pm_date = itemView.findViewById(R.id.text2);
            ll_root = itemView.findViewById(R.id.lin_job_item);

        }

    }
}
