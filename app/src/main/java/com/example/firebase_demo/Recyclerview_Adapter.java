package com.example.firebase_demo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Recyclerview_Adapter extends FirebaseRecyclerAdapter<Product_Data,Recyclerview_Adapter.Holder> {


    public Recyclerview_Adapter(@NonNull FirebaseRecyclerOptions<Product_Data> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Product_Data model) {
        holder.name.setText(""+model.pName);
        holder.des.setText(""+model.pDes);
        holder.price.setText(""+model.pPrice);

        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu=new PopupMenu(v.getContext(), holder.popupMenu);
                menu.getMenuInflater().inflate(R.menu.edit_menu,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("Product").orderByChild("pId").equalTo(model.pId);
                        if(item.getItemId()==R.id.deleteProduct)
                        {
                              applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("YYY", "onCancelled", databaseError.toException());

                                }
                            });

                        }
                        if(item.getItemId()==R.id.updateProducr)
                        {
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        //appleSnapshot.getRef().removeValue();
                                        String id=ref.getKey();
                                        Product_Data model=new Product_Data(id,"Monitor","Output","4580","url");
                                        appleSnapshot.getRef().setValue(model);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("YYY", "onCancelled", databaseError.toException());

                                }
                            });
                        }


                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_file,parent,false);
        Holder holder=new Holder(view);


        return holder;
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imageView,popupMenu;
        TextView name,des,price;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.item_img);
            name=itemView.findViewById(R.id.item_name);
            des=itemView.findViewById(R.id.item_des);
            price=itemView.findViewById(R.id.item_price);
            popupMenu=itemView.findViewById(R.id.popmenu);



        }
    }
}




