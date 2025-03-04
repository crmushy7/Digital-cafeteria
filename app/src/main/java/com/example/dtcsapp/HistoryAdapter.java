package com.example.dtcsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistorySetGet> coupons;
    private HistoryAdapter.OnItemClickListener mListener;
    private boolean clickable = true;

    public HistoryAdapter(List<HistorySetGet> coupons) {
        this.coupons = coupons;
    }
    public void setOnItemClickListener(HistoryAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }


    public void updateData(List<HistorySetGet> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistorySetGet coupon = coupons.get(position);
        holder.bind(coupon);

        // Set the background drawable for receiptStatus TextView based on the status
//        if (item.getStatus() != null && item.getStatus().equals("Debt")) {
//            holder.receiptStatus.setBackgroundResource(R.drawable.roundedred);
//        } else {
//            holder.receiptStatus.setBackgroundResource(R.drawable.roundedgreen);
//        }

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position, coupon);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView food_name;
        private TextView food_price;
        private TextView coupon_status;
        private TextView coupon_date;
        private TextView coupon_refNo;
        private TextView coupon_No;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_name=itemView.findViewById(R.id.hc_foodname);
            food_price = itemView.findViewById(R.id.hc_foodprice);
            coupon_status = itemView.findViewById(R.id.hc_couponStatus);
            coupon_date=itemView.findViewById(R.id.hc_couponDate);
            coupon_refNo=itemView.findViewById(R.id.hc_referenceNumber);
            coupon_No=itemView.findViewById(R.id.coupon_number);
        }

        public void bind(HistorySetGet historySetGet) {
            food_name.setText(historySetGet.getFood_name());
            food_price.setText(historySetGet.getFood_price());
            coupon_status.setText(historySetGet.getCoupon_status());
            coupon_refNo.setText(historySetGet.getCoupon_reference_Number());
            coupon_date.setText(historySetGet.getCoupon_date());
            coupon_No.setText(historySetGet.getCoupon_No());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position,HistorySetGet historySetGet);
    }
}
