package com.example.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyHistoryTradeAdapter extends RecyclerView.Adapter<MyHistoryTradeAdapter.MyViewHolder> {

    private List<HistoryCardView> historyTradeList; // Thay thế HistoryTradeItem bằng kiểu dữ liệu thực tế của bạn



    public MyHistoryTradeAdapter(List<HistoryCardView> historyTradeList) {
        this.historyTradeList = historyTradeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_itemshistory, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HistoryCardView historyCardView = historyTradeList.get(position);

        // Set dữ liệu vào các thành phần UI
        holder.textViewItemText1.setText(historyCardView.getSymbol());
        holder.ImageView.setImageResource(historyCardView.getImageId());
        holder.textViewItemText.setText(String.valueOf(historyCardView.isBuyer())); // Chuyển đổi boolean thành chuỗi
        holder.textViewItemText2.setText(String.valueOf(historyCardView.getQuantity())); // Chuyển đổi số thành chuỗi
        holder.textViewItemText3.setText(String.valueOf(historyCardView.getTime()));
        holder.textViewItemText4.setText(String.valueOf(historyCardView.getPrice())); // Chuyển đổi số thành chuỗi
        holder.textViewRight.setText(String.valueOf(historyCardView.getOrderID()));

    }

    @Override
    public int getItemCount() {
        return historyTradeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemText1, textViewItemText, textViewItemText2, textViewItemText3, textViewItemText4, textViewRight;
        ImageView ImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView = itemView.findViewById(R.id.item_image_view);
            textViewItemText1 = itemView.findViewById(R.id.item_text1);
            textViewItemText = itemView.findViewById(R.id.item_text);
            textViewItemText2 = itemView.findViewById(R.id.item_text2);
            textViewItemText3 = itemView.findViewById(R.id.item_text3);
            textViewItemText4 = itemView.findViewById(R.id.item_text4);
            textViewRight = itemView.findViewById(R.id.right_text_view);
        }
    }
}
