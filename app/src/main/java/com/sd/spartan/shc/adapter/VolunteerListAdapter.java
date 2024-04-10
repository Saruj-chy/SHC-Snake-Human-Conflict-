package com.sd.spartan.shc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.shc.R;
import com.sd.spartan.shc.constants.Constraints;
import com.sd.spartan.shc.model.Volunteer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VolunteerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mCtx;
    private List<Volunteer> mItemList;
    private final String mLanguage ;

    public VolunteerListAdapter(Context mCtx, List<Volunteer> mItemList, String mLanguage) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mLanguage = mLanguage;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchFilterList(List<Volunteer> filteredList) {
        mItemList = filteredList;
        notifyDataSetChanged();
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_volunteer, parent, false);
        return new VolunteerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Volunteer volunteer = mItemList.get(position);
        ((VolunteerViewHolder) holder).bind(volunteer);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class VolunteerViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mAddress ;
        CircleImageView mImageLogo;
        ImageButton mPhnBtn ;


        public VolunteerViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_name_layout);
            mAddress = itemView.findViewById(R.id.text_address_layout);
            mPhnBtn = itemView.findViewById(R.id.imgbtn_call);
            mImageLogo = itemView.findViewById(R.id.image_contact_layout);


        }

        @SuppressLint("SetTextI18n")
        public void bind(Volunteer volunteer) {

            if(mLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                mName.setText(volunteer.getName_ban());
                mAddress.setText(" "+volunteer.getAddress_ban());
            }else if(mLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                mName.setText(volunteer.getName_eng());
                mAddress.setText(" "+volunteer.getAddress_eng());
            }

            mPhnBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+880"+ volunteer.getPhone()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                mCtx.startActivity(intent);
            });

        }
    }
}