package com.example.ifoodclone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseUserConfiguration {

    public static String getUserId(){
        FirebaseAuth auth = FirebaseConfiguration.getFirebaseAuth();
        return auth.getCurrentUser().getUid();
    }
    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = FirebaseConfiguration.getFirebaseAuth();
        return user.getCurrentUser();
    }
    public static boolean updateUserType(String type){
        try {
            FirebaseUser firebaseUser = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(type).build();
            firebaseUser.updateProfile(profile);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
