package com.example.yourstudy.user;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import com.example.yourstudy.pdf.RetrievePDFEnglish;
import com.example.yourstudy.pdf.RetrievePDFGraphics;

import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.R;
import com.example.yourstudy.pdf.RetrievePDFManagement;
import com.example.yourstudy.pdf.RetrievePDFModern_Languages;
import com.example.yourstudy.pdf.RetrievePDFOOP;
import com.example.yourstudy.pdf.RetrievePDFProbability_Theory;
import com.example.yourstudy.pdf.RetrievePDFProgramming_Technology;
import com.example.yourstudy.pdf.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;


public class MaterialsFragment extends Fragment {

    ImageButton selectFileButton;
    String selectedSubject;
    StorageReference storageReference;

    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materials, container, false);
        storageReference = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com").getReference().child("Materials");
        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("uploadPDF");
        ImageButton English = view.findViewById(R.id.english);
        ImageButton Graphics = view.findViewById(R.id.graphics);
        ImageButton Management = view.findViewById(R.id.management);
        ImageButton OOP = view.findViewById(R.id.OOP);
        ImageButton Modern_Languages = view.findViewById(R.id.modern_languages);
        ImageButton Probability_Theory = view.findViewById(R.id.probability_theory);
        ImageButton Programming_Technology = view.findViewById(R.id.programming_technology);

        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFEnglish.class);
                startActivity(intent);
            }
        });
        Graphics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFGraphics.class);
                startActivity(intent);
            }
        });
        Management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFManagement.class);
                startActivity(intent);
            }
        });
        OOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFOOP.class);
                startActivity(intent);
            }
        });
        Modern_Languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFModern_Languages.class);
                startActivity(intent);
            }
        });
        Probability_Theory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFProbability_Theory.class);
                startActivity(intent);
            }
        });
        Programming_Technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFProgramming_Technology.class);
                startActivity(intent);
            }
        });

        return view;
    }
}