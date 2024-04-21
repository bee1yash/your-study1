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
import android.widget.ImageView;
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

    ImageView photoSchedule;
    ImageView photoModule;
    FragmentScheduleAdminBinding binding_admin;
    StorageReference stoRef;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_admin, container, false);
        binding_admin = FragmentScheduleAdminBinding.inflate(inflater, container, false);
        photoSchedule = view.findViewById(R.id.photo_schedule);
        photoModule = view.findViewById(R.id.photo_module);

        Button selectGroupButton = view.findViewById(R.id.select_group_button);
        selectGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Group");
                String[] groups = {"Group 1", "Group 2", "Group 3", "Group 4"};
                builder.setItems(groups, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set the selected group based on the user's selection
                        selectedGroup = which + 1; // Groups are 1-indexed
                        Toast.makeText(getActivity(), "Selected Group: " + selectedGroup, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        Button uploadScheduleButton = view.findViewById(R.id.upload_schedule_button);
        uploadScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(selectedGroup, "Schedule");
            }
        });

        Button uploadModuleButton = view.findViewById(R.id.upload_module_button);
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

                if (photoSchedule != null && photoModule != null) {
                    if (selectedGroup != 0) {
                        if (imageUri.getPath().contains("Schedule")) {
                            photoSchedule.setImageBitmap(bitmap);
                        } if (imageUri.getPath().contains("module")) {
                            photoModule.setImageBitmap(bitmap);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please select a group first", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
