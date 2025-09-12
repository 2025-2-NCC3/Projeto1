package com.example.ifoodclone.model;

import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

public class Order {
    private String idUser;
    private String idCompany;
    private String idOrder;
    private String name;
    private String address;
    private List<OrderItem> items;
    private Double total;
    private String status = "pending";
    private int payMethod;
    private String obs;

    public Order() {

    }
    public Order(String idU, String idC){
        this.idUser = idU;
        this.idCompany = idC;

        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference orderReference = databaseReference.child("user_order")
                .child(getIdCompany())
                .child(getIdUser());
        setIdOrder(orderReference.push().getKey());

    }

    public void save(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference orderReference = databaseReference.child("user_order")
                .child(getIdCompany())
                .child(getIdUser());

        orderReference.setValue(this);
    }

    public void confirm(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference orderReference = databaseReference.child("order")
                .child(getIdCompany())
                .child(getIdOrder() );

        orderReference.setValue(this);
    }

    public void remove(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference orderReference = databaseReference.child("user_order")
                .child(getIdCompany())
                .child(getIdUser());
        orderReference.removeValue();
    }

    public void updateStatus(){
        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference orderReference = databaseReference.child("user_order")
                .child(getIdCompany())
                .child(getIdOrder());
        orderReference.updateChildren(status);
    }
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(int payMethod) {
        this.payMethod = payMethod;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
