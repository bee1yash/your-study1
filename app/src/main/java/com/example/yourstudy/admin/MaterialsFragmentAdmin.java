package com.example.yourstudy.admin;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import com.example.yourstudy.pdf.RetrievePDFEnglishAdmin;

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
import com.example.yourstudy.pdf.RetrievePDFGraphicsAdmin;
import com.example.yourstudy.pdf.RetrievePDFManagementAdmin;
import com.example.yourstudy.pdf.RetrievePDFModern_LanguagesAdmin;
import com.example.yourstudy.pdf.RetrievePDFOOPAdmin;
import com.example.yourstudy.pdf.RetrievePDFProbability_TheoryAdmin;
import com.example.yourstudy.pdf.RetrievePDFProgramming_TechnologyAdmin;
import com.example.yourstudy.pdf.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;


public class MaterialsFragmentAdmin extends Fragment {

    ImageButton selectFileButton;
    String selectedSubject;
    StorageReference storageReference;

    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materials_admin, container, false);
        selectFileButton = view.findViewById(R.id.select_file_btn);
        storageReference = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com").getReference().child("Materials");
        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("uploadPDF");

        ImageButton English = view.findViewById(R.id.english_admin);
        ImageButton Graphics = view.findViewById(R.id.graphics_admin);
        ImageButton Management = view.findViewById(R.id.management_admin);
        ImageButton OOP = view.findViewById(R.id.OOP_admin);
        ImageButton Modern_Languages = view.findViewById(R.id.modern_languages_admin);
        ImageButton Probability_Theory = view.findViewById(R.id.probability_theory_admin);
        ImageButton Programming_Technology = view.findViewById(R.id.programming_technology_admin);

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubjectDialog();
            }
        });
        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFEnglishAdmin.class);
                startActivity(intent);
            }
        });
        Graphics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFGraphicsAdmin.class);
                startActivity(intent);
            }
        });
        Management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFManagementAdmin.class);
                startActivity(intent);
            }
        });
        OOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFOOPAdmin.class);
                startActivity(intent);
            }
        });
        Modern_Languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFModern_LanguagesAdmin.class);
                startActivity(intent);
            }
        });
        Probability_Theory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFProbability_TheoryAdmin.class);
                startActivity(intent);
            }
        });
        Programming_Technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RetrievePDFProgramming_TechnologyAdmin.class);
                startActivity(intent);
            }
        });



        return view;
    }

    private void showSubjectDialog() {
        final String[] subjects = {"Англійська мова", "Графічне та геометричне програмування", "Менеджмент", "Об'єктно-орієнтоване програмування", "Сучасні мови програмування", "Теорія ймовірностей", "Технологія програмування"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Оберіть предмет");
        builder.setItems(subjects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSubject = subjects[which];
                selectFile();
            }
        });
        builder.show();
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Оберіть файл"), 12);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==12 && resultCode== RESULT_OK && data!= null && data.getData()!= null)
        {
            showRenameDialog(data.getData());
        }
    }


    private void showRenameDialog(final Uri data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Перейменуйте файл(або залишіть як є)");
        final String fileName = getFileNameFromUri(data);
        final String extension = getFileExtension(data);
        final String formattedFileName = fileName.replaceAll("\\s+\\.", ".");
        final EditText input = new EditText(getActivity());
        input.setText(formattedFileName);
        builder.setView(input);

        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFileName = input.getText().toString();
                uploadPDFFileFirebase(data, newFileName, extension);
            }
        });
        builder.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                if (fileName != null && fileName.endsWith(".")) {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                int firstDotIndex = fileName.indexOf(".");
                if (firstDotIndex > 0) {
                    String beforeDot = fileName.substring(0, firstDotIndex);
                    String afterDot = fileName.substring(firstDotIndex);
                    fileName = beforeDot + " " + afterDot;
                }
            }
        }
        return fileName;
    }
    private String getFileExtension(Uri uri) {
        String extension = "";
        if (uri != null) {
            String fileName = uri.getLastPathSegment();
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
                extension = fileName.substring(lastDotIndex + 1);
            }
        }
        return extension;
    }
    private void uploadPDFFileFirebase(Uri data, String newFileName, String extension) {
        if (selectedSubject != null) {
            String filenameWithExtension = newFileName;
            if (!newFileName.endsWith("." + extension)) {
                filenameWithExtension += "." + extension;
            }
            StorageReference reference = storageReference.child(selectedSubject + "/" + filenameWithExtension);

            reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            putPDF putPDF = new putPDF(newFileName, uri.toString(), extension);

                            databaseReference.child(selectedSubject).child(Objects.requireNonNull(databaseReference.push().getKey())).setValue(putPDF);

                            Toast.makeText(getActivity(), "Файл завантажено", Toast.LENGTH_SHORT).show();

                            selectedSubject = null;
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getActivity(), "Виберіть предмет", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPDFListDialog(List<putPDF> pdfList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Список файлів");
        String[] fileNames = new String[pdfList.size()];
        for (int i = 0; i < pdfList.size(); i++) {
            fileNames[i] = pdfList.get(i).getName();
        }

        builder.setItems(fileNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selectedFileName = fileNames[which];

            }
        });

        builder.setNegativeButton("Скасувати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}