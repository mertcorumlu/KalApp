package com.kalom.kalapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.kalom.kalapp.fragments.*;
import android.view.MenuItem;

public class MainPage extends AppCompatActivity {

    private Fragment frag1;
    private Fragment frag2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage_layout);


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {

                            case R.id.action_item1:

                                if(frag1!=null){
                                    selectedFragment=frag1;
                                }else{
                                    frag1=ItemOneFragment.newInstance();
                                    selectedFragment = frag1;
                                }

                                break;

                            case R.id.action_item2:

                                if(frag2!=null){
                                    selectedFragment=frag2;
                                }else{
                                    frag2=ItemOneFragment.newInstance();
                                    selectedFragment = frag2;
                                }

                                break;
                            case R.id.action_item3:
                                selectedFragment = ItemOneFragment.newInstance();
                                break;

                            case R.id.action_item4:
                                selectedFragment = ItemOneFragment.newInstance();
                                break;
                        }



                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        frag1=ItemOneFragment.newInstance();
        transaction.replace(R.id.frame_layout,frag1);
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
}
