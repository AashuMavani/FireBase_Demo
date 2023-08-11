package com.example.firebase_demo.Activity;

import static com.example.firebase_demo.Activity.MainActivity.editor;
import static com.example.firebase_demo.Activity.MainActivity.preferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase_demo.Fragment.Add_Product_Fragment;
import com.example.firebase_demo.Fragment.ShowAll_Product_Fragment;
import com.example.firebase_demo.Fragment.View_Product_Fragment;
import com.example.firebase_demo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout drawer_Layout;
    Toolbar toolbar;
    NavigationView navigation_View;
    ImageView header_img;
    TextView header_name,header_email;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        drawer_Layout=findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        navigation_View=findViewById(R.id.navigation_view);
        header_img=findViewById(R.id.header_img);
        header_name=findViewById(R.id.header_name);

        View view=navigation_View.getHeaderView(0);
        header_email=view.findViewById(R.id.header_email);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer_Layout,toolbar,R.string.Open_Drawer,R.string.Close_Drawer);
        drawer_Layout.addDrawerListener(toggle);
        toggle.syncState();

        header_email.setText(""+preferences.getString("email",null));

//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");

        navigation_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id==R.id.addproduct)
                {
                   ReplaceFragment(new Add_Product_Fragment());
                   drawer_Layout.closeDrawer(Gravity.LEFT);
                } else if (id==R.id.viewproduct) {
                    ReplaceFragment(new View_Product_Fragment());
                    drawer_Layout.closeDrawer(Gravity.LEFT);
                } else if (id==R.id.showallproduct) {
                    ReplaceFragment(new ShowAll_Product_Fragment());
                    drawer_Layout.closeDrawer(Gravity.LEFT);
                } else if (id==R.id.logout) {
                    editor.putBoolean("loginstatus",false);
                    editor.commit();
                    Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                    startActivity(intent);

                }
                return true;
            }
        });

    }

    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        transaction.replace(R.id.contentview,fragment);
        transaction.commit();
    }
}