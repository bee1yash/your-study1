package com.example.yourstudy.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yourstudy.R;


public class MainActivityAdmin extends AppCompatActivity {
    com.example.yourstudy.databinding.ActivityMainAdminBinding binding_admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding_admin = com.example.yourstudy.databinding.ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding_admin.getRoot());
        replaceFragment(new ScheduleFragmentAdmin());
        binding_admin.bottomNavigationViewAdmin.setBackground(null);
        binding_admin.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.schedule_admin) {
                replaceFragment(new ScheduleFragmentAdmin());
            } else if (item.getItemId() == R.id.profile_admin) {
                replaceFragment(new ProfileFragmentAdmin());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, fragment);
        fragmentTransaction.commit();
    }
}