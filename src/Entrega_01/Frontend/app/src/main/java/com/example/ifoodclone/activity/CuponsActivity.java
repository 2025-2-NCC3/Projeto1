package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.CuponsAdapter;
import com.example.ifoodclone.model.CouponDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuponsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CuponsAdapter adapter;
    private List<CouponDto> listaCupons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupons);

        recyclerView = findViewById(R.id.recyclerCupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CuponsAdapter(listaCupons);
        recyclerView.setAdapter(adapter);

        carregarCupons();
    }

    private void carregarCupons() {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getCoupons().enqueue(new Callback<List<CouponDto>>() {
            @Override
            public void onResponse(Call<List<CouponDto>> call, Response<List<CouponDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCupons.clear();
                    listaCupons.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CuponsActivity.this, "Nenhum cupom encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CouponDto>> call, Throwable t) {
                Toast.makeText(CuponsActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
