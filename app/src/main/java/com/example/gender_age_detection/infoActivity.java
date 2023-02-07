package com.example.gender_age_detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class infoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<userModel> list;
    Myadapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        recyclerView = findViewById(R.id.recyclerview);


        list = new ArrayList<userModel>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Myadapter(this,list);

        recyclerView.setAdapter(adapter);

        EventChangeListener();




    }

    private void EventChangeListener() {
        db.collection("records").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Log.e("Firestore Error",error.getMessage());
                    return;

                }
                for(DocumentChange dc: value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        System.out.println(dc.getDocument().toObject(userModel.class));
                        list.add(dc.getDocument().toObject(userModel.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}