package com.example.yourstudy.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.yourstudy.R;
import com.example.yourstudy.admin.LoginAdmin;
import com.example.yourstudy.user.ProfileFragment;
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
    Uri profileImageUri;
    StorageReference storageRef;
    DatabaseReference adminRef;
    TextView AdminEmailTextView;
    TextView AdminInfoTextView;
    TextView logoutButton;
    ImageView AdminprofileImageView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_admin, container, false);
        TextView logoutButton = view.findViewById(R.id.logout_btn_admin);
        AdminEmailTextView = view.findViewById(R.id.admin_email);
        AdminInfoTextView = view.findViewById(R.id.admin_info);
        AdminprofileImageView = view.findViewById(R.id.profile_image_admin);
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

        return view;
    }
    private void loadAdminData() {
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ProfileFragmentAdmin.Admin admin = dataSnapshot.getValue(ProfileFragmentAdmin.Admin.class);
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
    @SuppressLint("SetTextI18n")
    private void addUserToDatabase() {
        if (currentUser != null) {
            String adminEmail = currentUser.getEmail();
            String[] parts = adminEmail.split("@");
            String[] adminNameParts = parts[0].split("\\.");
            String adminFirstName = capitalizeFirstLetter(adminNameParts[1]);
            String adminLastName = capitalizeFirstLetter(adminNameParts[0]);
            String photoUrl = "";
            ProfileFragmentAdmin.Admin admin = new ProfileFragmentAdmin.Admin(adminEmail, adminLastName, adminFirstName,photoUrl);
            adminRef.setValue(admin);
            AdminEmailTextView.setText(adminEmail);
            AdminInfoTextView.setText(adminFirstName + " " + adminLastName);

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
                profileImageUri = result.getUri();
                AdminprofileImageView.setImageURI(profileImageUri);
                uploadFile();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadFile() {
        if (profileImageUri != null) {
            storageRef.putFile(profileImageUri)
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
}
