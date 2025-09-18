package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProductAdapter;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.listener.RecyclerItemClickListener;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.model.Company;
import com.example.ifoodclone.model.Order;
import com.example.ifoodclone.model.OrderItem;
import com.example.ifoodclone.model.Product;
import com.example.ifoodclone.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyMenuActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textCompanyName, textCartQuantity, textCartTotalPrice;
    private CircleImageView profileImage;
    private RecyclerView recyclerMenu;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<OrderItem> cartItems;
    private Order order;
    private Company selectedCompany;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;
    private User user;
    private int cartItensQuantity, paymentMethod;
    private Double cartItensTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_menu);
        findViewsById();
        toolbarConfig();
        databaseReference = FirebaseConfiguration.getDatabaseReference();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            selectedCompany = (Company) bundle.getSerializable("company");
            /* Configuração da interface */
            toolbar.setTitle(selectedCompany.getName() + " - Menu");
            textCompanyName.setText(selectedCompany.getName());
            Glide.with(CompanyMenuActivity.this).load(selectedCompany.getUrlImage()).into(profileImage);
            recoverProducts();
            recoverUserData();
        }

        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerMenu.setHasFixedSize(true);
        productAdapter = new ProductAdapter(productList, this);
        recyclerMenu.setAdapter(productAdapter);

        recyclerMenu.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerMenu,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmQuantity(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

    }
    private void confirmQuantity(int itemSelected){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantity");
        builder.setMessage("Enter the quantity");

        EditText editQuantity = new EditText(this);
        editQuantity.setText("1");
        editQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editQuantity);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String quantity = editQuantity.getText().toString();
                Product product = productList.get(itemSelected);

                OrderItem orderItem = new OrderItem();
                orderItem.setIdProduct(product.getProductId());
                orderItem.setQuantity(Integer.parseInt(quantity));
                orderItem.setProductName(product.getName());
                orderItem.setPrice(product.getPrice());
                cartItems.add(orderItem);

                if(order == null) {
                    String userId = user.getId();
                    String companyId = selectedCompany.getId();
                    order = new Order(userId, companyId);
                }

                order.setName(user.getName());
                order.setAddress(user.getAddress());
                order.setItems(cartItems);
                order.save();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void findViewsById() {
        toolbar = findViewById(R.id.toolbar);
        textCompanyName = findViewById(R.id.textCompanyNameMenu);
        textCartQuantity = findViewById(R.id.textViewCartQuantity);
        textCartTotalPrice = findViewById(R.id.textViewCartPrice);
        profileImage = findViewById(R.id.imageProfileMenu);
        recyclerMenu = findViewById(R.id.recyclerMenu);
        productList = new ArrayList<>();
        cartItems = new ArrayList<>();
    }
    private void toolbarConfig(){
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadingDialog(String title){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setView(R.layout.loading);
        alertDialog = alert.create();
        alertDialog.show();
    }

    private void recoverUserData(){
        loadingDialog("Loading...");
        DatabaseReference userReference = databaseReference.child("user").child(FirebaseUserConfiguration.getUserId());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                  user = snapshot.getValue(User.class);
                }
                recoverOrder();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void recoverOrder(){
        String userId = user.getId();
        String companyId = selectedCompany.getId();

        DatabaseReference orderReference = databaseReference.child("user_order")
                        .child(companyId)
                                .child(userId);
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItensQuantity = 0;
                cartItensTotalPrice = 0.0;
                cartItems = new ArrayList<>();
                if(snapshot.getValue() != null){
                    order = snapshot.getValue(Order.class);
                    cartItems = order.getItems();

                    for(OrderItem orderItem : cartItems){
                        int quantity = orderItem.getQuantity();
                        Double price = orderItem.getPrice();
                        cartItensQuantity += quantity;
                        cartItensTotalPrice += quantity * price;
                    }
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                textCartQuantity.setText("Qt: " + cartItensQuantity);
                textCartTotalPrice.setText("R$: " + decimalFormat.format(cartItensTotalPrice));

                alertDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.dismiss();
    }
    private void recoverProducts(){
        DatabaseReference productReference = databaseReference.child("products").child(selectedCompany.getId());
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    productList.add(ds.getValue(Product.class));
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void confirmOrder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the payment method");

        CharSequence[] items = new CharSequence[]{
                "Money", "Credit card"
        };
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                paymentMethod = i;
            }
        });
        EditText editObs = new EditText(this);
        editObs.setHint("Observation");
        builder.setView(editObs);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String observation = editObs.getText().toString();
                order.setObs(observation);
                order.setPayMethod(paymentMethod);
                order.setStatus("confirmed");
                order.confirm();
                order.remove();
                order = null;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuConfirm){
            confirmOrder();
        }
        return super.onOptionsItemSelected(item);
    }
}