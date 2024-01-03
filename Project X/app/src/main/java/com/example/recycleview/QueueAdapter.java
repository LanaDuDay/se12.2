package com.example.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> {
    private List<QueueItem> buyLimitQueueList;

    public QueueAdapter(List<QueueItem> buyLimitQueueList) {
        this.buyLimitQueueList = buyLimitQueueList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buy_limit_item, parent, false);

        // Return the ViewHolder
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the views in each item

        QueueItem queueItem = buyLimitQueueList.get(position);
        long timestamp = Long.parseLong(queueItem.getTime());
        Instant instant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.ofEpochMilli(timestamp);
        }
        // Định dạng cho ngày giờ
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Chuyển đổi Instant thành Date
        Date date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = Date.from(instant);
        }
        String formattedDate = dateFormat.format(date);
        holder.symbolTextView.setText("Symbol: " + queueItem.getSymbol());
        holder.priceTextView.setText("Price: " + String.valueOf(queueItem.getPrice()));
        holder.quantityTextView.setText("Amount: " + String.valueOf(queueItem.getQuantity()));
        holder.timeTextView.setText("Time: " + formattedDate);
    }

    @Override
    public int getItemCount() {
        return buyLimitQueueList.size();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView symbolTextView, priceTextView, quantityTextView, timeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
