package com.example.yourstudy.admin;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.VectorEnabledTintResources;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.R;
import com.example.yourstudy.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MaterialsFragmentAdmin extends Fragment {

    EditText selectFile;
    Button selectFileButton;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materials_admin, container, false);
        selectFile = view.findViewById(R.id.select_file);
        selectFileButton = view.findViewById(R.id.select_file_btn);
        storageReference = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com").getReference().child("Materials");
        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("uploadPDF");
        selectFileButton.setEnabled(false);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });
        return view;
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF File Select"),12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==12 && resultCode== RESULT_OK && data != null && data.getData() != null)
        {
            selectFileButton.setEnabled(true);
            selectFile.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));

            selectFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFileFirebase(data.getData());
                }
            });
        }

    }
    private void uploadPDFFileFirebase(Uri data) {
        StorageReference reference = storageReference.child("upload" + System.currentTimeMillis() + ".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                putPDF putPDF = new putPDF(selectFile.getText().toString(), uri.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);



            }
        });
    }


}
