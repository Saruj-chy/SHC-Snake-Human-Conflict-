package com.sd.spartan.shc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.shc.interfaces.OnClickInt;
import com.sd.spartan.shc.R;
import com.sd.spartan.shc.constants.Constraints;
import com.sd.spartan.shc.model.SnakeName;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SnakeNameViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mCtx;
    private List<SnakeName> mItemList;
    private final String mLanguage ;
    private final OnClickInt onClickInt ;

    public SnakeNameViewAdapter(Context mCtx, List<SnakeName> mItemList, String mLanguage, OnClickInt onClickInt) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mLanguage = mLanguage;
        this.onClickInt = onClickInt;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchFilterList(List<SnakeName> filteredList) {
        mItemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_snake_view, parent, false);
        return new SnakeNameViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        SnakeName volunteer = mItemList.get(position);
        ((SnakeNameViewHolder) holder).bind(volunteer);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class SnakeNameViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        CircleImageView mImageLogo;

        public SnakeNameViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_snake_name_layout);
            mImageLogo = itemView.findViewById(R.id.image_snake_layout);
        }

        public void bind(SnakeName snakeName) {
            if(mLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                mName.setText(snakeName.getName_ban());
            }else if(mLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                mName.setText(snakeName.getName_eng());
            }
            if(snakeName.getImage().equalsIgnoreCase("")){
                mImageLogo.setImageResource(R.drawable.stoke_border_both);
            }else{
                Picasso.get().load(Constraints.MAIN_URL+snakeName.getImage())
                        .placeholder(R.drawable.stoke_border_both).into(mImageLogo);
            }

            itemView.setOnClickListener(v -> {
                if(mLanguage.equalsIgnoreCase(Constraints.LAN_BAN)){
                    onClickInt.onClick(v,snakeName.getId(), snakeName.getName_ban(), false );
                }else if(mLanguage.equalsIgnoreCase(Constraints.LAN_ENG)){
                    onClickInt.onClick(v,snakeName.getId(), snakeName.getName_eng(), false );
                }
            });
            mImageLogo.setOnClickListener(v ->
                    onClickInt.onClick(v,
                    snakeName.getId(),
                            Constraints.MAIN_URL+snakeName.getImage(), true ));

        }

    }
}