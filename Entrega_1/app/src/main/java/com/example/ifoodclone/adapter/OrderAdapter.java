package com.example.ifoodclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Order;
import com.example.ifoodclone.model.OrderItem;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList){
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_adapter, parent, false);
        return new OrderAdapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.name.setText(order.getName());
        holder.address.setText(order.getAddress());
        holder.observation.setText(order.getObs());

        List<OrderItem> itens = order.getItems();
        String itemDesc = "";

        int itemNumber = 1;
        Double total = 0.0;
        for(OrderItem orderItem : itens){
            int qt = orderItem.getQuantity();
            Double price = orderItem.getPrice();
            total += (qt*price);

            String itemName = orderItem.getProductName();
            itemDesc += itemNumber + ") " + itemName + " / (" + qt + " x R$ " + price + ") \n";
            itemNumber++;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        itemDesc += "Total: R$ " + df.format(total);
        holder.items.setText(itemDesc);

        int payMethod = order.getPayMethod();
        String payment = payMethod == 0 ? "Money" : "Credit Card";
        holder.paymentMethod.setText("Pay method: " + payment);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, address, paymentMethod, observation, items;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textOrderName);
            address = itemView.findViewById(R.id.textOrderAddress);
            paymentMethod = itemView.findViewById(R.id.textOrderPayment);
            observation = itemView.findViewById(R.id.textOrderObservation);
            items = itemView.findViewById(R.id.textOrderItems);
        }
    }
}
