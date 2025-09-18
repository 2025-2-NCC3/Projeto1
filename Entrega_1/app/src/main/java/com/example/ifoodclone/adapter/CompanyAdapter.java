package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Company;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.MyViewHolder> {
    private List<Company> companiesList;
    private Context context;

    public CompanyAdapter(List<Company> companiesList, Context context) {
        this.companiesList = companiesList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.companies_item_adapter, parent, false);
        return new CompanyAdapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.MyViewHolder holder, int position) {
        Company company = companiesList.get(position);
        holder.name.setText(company.getName());
        holder.category.setText(company.getCategory());
        holder.deliveryTime.setText(company.getDeliveryTime());

        String taxPrice = String.valueOf(company.getTaxPrice());
        holder.deliveryTax.setText("R$ " + taxPrice);

        Glide.with(context).load(company.getUrlImage()).into(holder.profilePhoto);
    }

    @Override
    public int getItemCount() {
        return companiesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, category, deliveryTime, deliveryTax;
        CircleImageView profilePhoto;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adapterTextCompanyName);
            category = itemView.findViewById(R.id.adapterTextCompanyCategory);
            deliveryTime = itemView.findViewById(R.id.adapterTextCompanyDeliveryTime);
            deliveryTax = itemView.findViewById(R.id.adapterTextCompanyDeliveryTax);
            profilePhoto = itemView.findViewById(R.id.adapterImageCompanyProfile);
        }
    }
}
