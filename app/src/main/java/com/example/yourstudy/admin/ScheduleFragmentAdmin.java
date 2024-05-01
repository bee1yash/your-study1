package com.example.yourstudy.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.R;
import com.example.yourstudy.databinding.FragmentScheduleAdminBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ScheduleFragmentAdmin extends Fragment {

    private static final int REQUEST_CODE = 1;
    private int selectedGroup = 0;


    FragmentScheduleAdminBinding binding_admin;
    StorageReference stoRef;
    Bitmap bitmapSchedule = null;
    Bitmap bitmapModule = null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_admin, container, false);
        binding_admin = FragmentScheduleAdminBinding.inflate(inflater, container, false);
        ImageButton selectGroupButton = view.findViewById(R.id.select_group_button);
        selectGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Оберіть групу");
                String[] groups = {"Група 1", "Група 2", "Група 3", "Група 4"};
                builder.setItems(groups, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGroup = which + 1;
                        Toast.makeText(getActivity(), "Обрана група: " + selectedGroup, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        ImageButton uploadScheduleButton = view.findViewById(R.id.upload_schedule_button);
        uploadScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(selectedGroup, "Schedule");
            }
        });

        ImageButton uploadModuleButton = view.findViewById(R.id.upload_module_button);
        uploadModuleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(selectedGroup, "Module");
            }
        });

        return view;
    }

    private void uploadImage(int group, String type) {
        String folderName = group + "Group";
        String fileName = group + type + ".jpg";
        stoRef = FirebaseStorage.getInstance().getReference().child("Schedule").child(folderName).child(fileName);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                stoRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                            stoRef = null;
                        })
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());


                } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
