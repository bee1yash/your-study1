package com.example.yourstudy.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.R;
import com.example.yourstudy.databinding.FragmentScheduleAdminBinding;


public class ScheduleFragmentAdmin extends Fragment {


    ImageView photoSchedule;
    ImageView photoModule;
    FragmentScheduleAdminBinding binding_admin;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_admin, container, false);
        binding_admin = FragmentScheduleAdminBinding.inflate(inflater, container, false);
        return view;
    }
}