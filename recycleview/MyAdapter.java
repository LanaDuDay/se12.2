package com.example.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public interface OnMyItemClickListener {
        void doSomeThing(int position);
    }

    private final List<PriceCoin> PriceCoinList;
    private OnMyItemClickListener itemClickListener;

    public MyAdapter(List<PriceCoin> priceCoinList) {
        PriceCoinList = priceCoinList;
    }

    public void setItemClickListener(OnMyItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PriceCoin priceCoin = PriceCoinList.get(position);
        holder.imageView.setImageResource(priceCoin.getInmageId());
        holder.textView.setText("Coin: " + priceCoin.getCoinName());
        holder.textViewright.setText("Price: $" + String.valueOf(priceCoin.getPrice()));
        holder.textView2.setText("Amount: " + String.valueOf(priceCoin.getQuantity()));
        holder.textView3.setText("Total: $" + String.valueOf(priceCoin.getPriceB()));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.doSomeThing(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return PriceCoinList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final TextView textViewright;
        private final TextView textView2;
        private final TextView textView3;
        private final CardView cardView;
        private final Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.new_cardview);
            imageView = itemView.findViewById(R.id.new_item_image_view);
            textView = itemView.findViewById(R.id.new_item_text);
            textViewright = itemView.findViewById(R.id.new_right_text_view);
            textView2 = itemView.findViewById(R.id.new_item_text2);
            textView3 = itemView.findViewById(R.id.new_item_text3);
            button = itemView.findViewById(R.id.new_button);
        }

    }
}
