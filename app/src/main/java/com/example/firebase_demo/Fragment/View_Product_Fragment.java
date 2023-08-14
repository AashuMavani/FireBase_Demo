package com.example.firebase_demo.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebase_demo.Product_Data;
import com.example.firebase_demo.R;
import com.example.firebase_demo.Recyclerview_Adapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class View_Product_Fragment extends Fragment {

    RecyclerView recyclerView;
    Recyclerview_Adapter adapter;
    private FirebaseDatabase mbase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mbase=FirebaseDatabase.getInstance();
        View view=inflater.inflate(R.layout.fragment_view__product_, container, false);
        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(View_Product_Fragment.this.getContext()));


        Query query = FirebaseDatabase.getInstance().getReference().child("Product");

        FirebaseRecyclerOptions<Product_Data> options
                = new FirebaseRecyclerOptions.Builder<Product_Data>()
                .setQuery(query, Product_Data.class)
                .build();

        adapter=new Recyclerview_Adapter(options, new Fragment_Interface() {
            @Override
            public void onFragmentCall(String id, String pName, String pPrice, String pDes, String pImg) {
                Add_Product_Fragment fragment=new Add_Product_Fragment();
                Bundle bundle=new Bundle();
                bundle.putString("id",id);
                bundle.putString("name",pName);
                bundle.putString("price",pPrice);
                bundle.putString("des",pDes);
                bundle.putString("img",pImg);


                fragment.setArguments(bundle);
                FragmentManager manager

            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}