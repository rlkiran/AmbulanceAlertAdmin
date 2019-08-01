package com.example.ambulanceadminversion20;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewRequests extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView address,userName;
    ImageView imageView;
    Button loc;
    GeoPoint destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.imageReceived);
        userName = findViewById(R.id.userName);
        loc = findViewById(R.id.info_bt);
    }

    @SuppressLint("SetTextI18n")
    public void getInfo(View view) {


        if(loc.getText().equals("get location")) {
            sendDataForMaps(destination);
        }
        DocumentReference docRef = db.collection("requests").document("requestone");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    setName(document.getString("name"));
                    setImageUrl(document.getString("imageUrl"));
                    setAddress(Objects.requireNonNull(document.getGeoPoint("geoLocation")));
                    destination = document.getGeoPoint("geoLocation");
                    loc.setText("get location");

                } else {
                   // Log.d("LOGGER", "No such document");
                }
            } else {
                Toast.makeText(this, "get failed with "+ task.getException(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendDataForMaps(GeoPoint geoLocation) {
        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
        i.putExtra("latitude",geoLocation.getLatitude());
        i.putExtra("longitude",geoLocation.getLongitude());
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    private void setName(String name) {
        userName.setText("Name : " + name);
    }

    @SuppressLint("SetTextI18n")
    private void setAddress(GeoPoint geoLocation) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        StringBuilder sb = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(geoLocation.getLatitude(), geoLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresses != null;
        sb.append(addresses.get(0).getAddressLine(0)).append("\n");

        address.setText("The Address is : \n" + sb);
    }

    private void setImageUrl(String imageUrl) {
        Picasso.get().load(imageUrl).into(imageView);
    }



}
