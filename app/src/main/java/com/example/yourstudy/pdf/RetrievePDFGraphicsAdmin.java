package com.example.yourstudy.pdf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourstudy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RetrievePDFGraphicsAdmin extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseReference;
    private List<putPDF> pdfList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_pdf_admin);
        pdfList = new ArrayList<>();
        listView = findViewById(R.id.list_materials);
        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("uploadPDF");
        retrievePDFFiles();
        ImageButton deleteButton = findViewById(R.id.btn_delete_file);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileSelectionDialog();
            }
        });
    }
    private void showFileSelectionDialog() {
        if (pdfList.isEmpty()) {
            Toast.makeText(this, "Папка порожня", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] fileNames = new CharSequence[pdfList.size()];
        for (int i = 0; i < pdfList.size(); i++) {
            fileNames[i] = pdfList.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Виберіть файл, який хочете видалити");
        builder.setItems(fileNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedFileName = fileNames[which].toString();
                deleteSelectedFile(selectedFileName);
            }
        });
        builder.show();
    }


    private void deleteSelectedFile(String fileName) {
        DatabaseReference englishReference = databaseReference.child("Графічне та геометричне програмування");
        englishReference.orderByChild("name").equalTo(fileName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String fileUrl = snapshot.child("url").getValue(String.class);

                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RetrievePDFGraphicsAdmin.this, "Файл успішно видалено", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RetrievePDFGraphicsAdmin.this, "Невдалось видалити файл: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RetrievePDFGraphicsAdmin.this, "Невдалось видалити файл із Firebase Storage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(RetrievePDFGraphicsAdmin.this, "Файл не знайдено", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RetrievePDFGraphicsAdmin.this, "Помилка: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrievePDFFiles() {
        DatabaseReference englishReference = databaseReference.child("Графічне та геометричне програмування");

        englishReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pdfList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    putPDF pdf = snapshot.getValue(putPDF.class);
                    if (pdf != null) {
                        pdfList.add(pdf);
                    }
                }
                ArrayAdapter<putPDF> adapter = new ArrayAdapter<>(RetrievePDFGraphicsAdmin.this, android.R.layout.simple_list_item_1, pdfList);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        putPDF selectedPDF = pdfList.get(position);
                        String pdfUrl = selectedPDF.getUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(pdfUrl), "*/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RetrievePDFGraphicsAdmin.this, "Помилка: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
