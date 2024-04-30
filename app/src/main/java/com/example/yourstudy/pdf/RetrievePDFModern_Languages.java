package com.example.yourstudy.pdf;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourstudy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RetrievePDFModern_Languages extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_pdf);

        listView = findViewById(R.id.list_materials);
        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("uploadPDF");
        retrievePDFFiles();
    }

    private void retrievePDFFiles() {
        DatabaseReference englishReference = databaseReference.child("Сучасні мови програмування");

        englishReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<putPDF> pdfList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    putPDF pdf = snapshot.getValue(putPDF.class);
                    if (pdf != null) {
                        pdfList.add(pdf);
                    }
                }
                PDFListAdapter adapter = new PDFListAdapter(RetrievePDFModern_Languages.this, pdfList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RetrievePDFModern_Languages.this, "Помилка: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
