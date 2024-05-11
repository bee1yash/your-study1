package com.example.yourstudy.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yourstudy.R;
import com.example.yourstudy.admin.LoginAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragmentAdmin extends Fragment {
    public static class Admin {
        private String email;
        private String lastName;
        private String firstName;
        private String photoURL;

        public Admin() {
        }

        public Admin(String email, String lastName, String firstName, String photoURL) {
            this.email = email;
            this.lastName = lastName;
            this.firstName = firstName;
            this.photoURL = photoURL;
        }

        public String getPhotoURL() {
            return photoURL;
        }

        public String getEmail() {
            return email;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }
    }

    FirebaseAuth auth;
    FirebaseUser currentUser;
    Uri AdminprofileImageUri;
    StorageReference storageRef;
    DatabaseReference adminRef;
    TextView AdminEmailTextView;
    TextView AdminInfoTextView;
    TextView logoutButton;
    ImageView AdminprofileImageView;
    DatabaseReference studentsRef;
    Spinner studentSpinner;
    List<String> studentNames;
    ArrayAdapter<String> spinnerAdapter;
    ImageButton deleteStudentBtn;
    ImageButton editGroupBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_admin, container, false);
        deleteStudentBtn = view.findViewById(R.id.delete_student_btn);
        editGroupBtn = view.findViewById(R.id.edit_group_btn);
        studentsRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");
        studentNames = new ArrayList<>();
        logoutButton = view.findViewById(R.id.logout_btn_admin);
        AdminEmailTextView = view.findViewById(R.id.admin_email);
        AdminInfoTextView = view.findViewById(R.id.admin_info);
        AdminprofileImageView = view.findViewById(R.id.profile_image_admin);
        AdminprofileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(), ProfileFragmentAdmin.this);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginAdmin.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            adminRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Admins").child(currentUser.getUid());
            storageRef = FirebaseStorage.getInstance().getReference().child("imagesAdmin/" + currentUser.getUid());

            loadAdminData();
            addUserToDatabase();
        }
        deleteStudentBtn.setOnClickListener(v -> showDeleteStudentDialog());
        editGroupBtn.setOnClickListener(v -> showEditGroupDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAdminData();
    }

    private void loadAdminData() {
        if (isAdded()) {
            adminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isAdded() && dataSnapshot.exists()) {
                        Admin admin = dataSnapshot.getValue(Admin.class);
                        if (admin != null) {
                            AdminEmailTextView.setText(admin.getEmail());
                            AdminInfoTextView.setText(admin.getFirstName() + " " + admin.getLastName());
                            String photoUrl = admin.getPhotoURL();
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(photoUrl)
                                        .placeholder(R.drawable.baseline_person_24)
                                        .into(AdminprofileImageView);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void addUserToDatabase() {
        if (currentUser != null) {
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild("photoURL")) {
                        String adminEmail = currentUser.getEmail();
                        String[] parts = adminEmail.split("@");
                        String[] adminNameParts = parts[0].split("\\.");
                        String adminFirstName = capitalizeFirstLetter(adminNameParts[1]);
                        String adminLastName = capitalizeFirstLetter(adminNameParts[0]);
                        String photoUrl = "";
                        Admin admin = new Admin(adminEmail, adminLastName, adminFirstName, photoUrl);
                        adminRef.setValue(admin);
                        AdminEmailTextView.setText(adminEmail);
                        AdminInfoTextView.setText(adminFirstName + " " + adminLastName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == requireActivity().RESULT_OK) {
                AdminprofileImageUri = result.getUri();
                AdminprofileImageView.setImageURI(AdminprofileImageUri);
                uploadFile();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadFile() {
        if (AdminprofileImageUri != null) {
            storageRef.putFile(AdminprofileImageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String photoUrl = uri.toString();
                        adminRef.child("photoURL").setValue(photoUrl)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    }))
                    .addOnFailureListener(e -> {
                    });
        }
    }

    private void showDeleteStudentDialog() {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String studentName = snapshot.child("firstName").getValue(String.class) + " " +
                            snapshot.child("lastName").getValue(String.class);
                    studentNames.add(studentName);
                }
                showDeleteStudentAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showDeleteStudentAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Виберіть студента, якого хочете видалити");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, studentNames);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedStudent = studentNames.get(which);
                deleteStudent(selectedStudent);
            }
        });
        builder.setNegativeButton("Відміна", null);
        builder.create().show();
    }

    private void deleteStudent(String selectedStudent) {
        studentsRef.orderByChild("firstName").equalTo(selectedStudent.split(" ")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                Toast.makeText(requireContext(), "Студент " + selectedStudent + " був успішно видалений", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showEditGroupDialog() {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String studentName = snapshot.child("firstName").getValue(String.class) + " " +
                            snapshot.child("lastName").getValue(String.class);
                    studentNames.add(studentName);
                }
                showEditGroupAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showEditGroupAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Виберіть студента, чию групу ви хочете змінити");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, studentNames);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedStudent = studentNames.get(which);
                showEditGroupInputDialog(selectedStudent);
            }
        });
        builder.setNegativeButton("Відміна", null);
        builder.create().show();
    }

    private void showEditGroupInputDialog(String selectedStudent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Введіть новий номер групи для студента: " + selectedStudent);
        final EditText input = new EditText(requireContext());
        builder.setView(input);
        builder.setPositiveButton("Зберегти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newGroup = input.getText().toString().trim();
                editStudentGroup(selectedStudent, newGroup);
            }
        });
        builder.setNegativeButton("Відміна", null);
        builder.show();
    }

    private void editStudentGroup(String selectedStudent, String newGroup) {
        studentsRef.orderByChild("firstName").equalTo(selectedStudent.split(" ")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("group").setValue(newGroup);
                }
                Toast.makeText(requireContext(), "Групу студента " + selectedStudent + " успішно змінено на " + newGroup, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
