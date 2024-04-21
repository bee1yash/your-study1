package com.example.yourstudy.user;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.yourstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;


public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private int userGroup = 0;
    private TextView userEmailTextView;
    private TextView userInfoTextView;
    private TextView userGroupTextView;
    private DatabaseReference userRef;
    private ImageView profileImageView;

    private Uri profileImageUri;

    private StorageReference storageRef;

    public static class User {
        private String email;
        private String lastName;
        private String firstName;
        private int group;
        private String photoURL;

        public User() {
        }

        public User(String email, String lastName, String firstName, int group, String photoURL) {
            this.email = email;
            this.lastName = lastName;
            this.firstName = firstName;
            this.group = group;
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


        public int getGroup() {
            return group;
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userEmailTextView = view.findViewById(R.id.user_email);
        userInfoTextView = view.findViewById(R.id.user_info);
        userGroupTextView = view.findViewById(R.id.user_group);
        TextView logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users").child(currentUser.getUid());
            storageRef = FirebaseStorage.getInstance().getReference().child("images/" + currentUser.getUid());

            loadUserData();
        }

        profileImageView = view.findViewById(R.id.profile_image);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(), ProfileFragment.this);
            }
        });
        return view;
    }

    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userEmailTextView.setText(user.getEmail());
                        userInfoTextView.setText(user.getFirstName() + " " + user.getLastName());
                        userGroupTextView.setText("Group: " + user.getGroup());

                        String photoUrl = user.getPhotoURL();
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(photoUrl)
                                    .placeholder(R.drawable.baseline_person_24)
                                    .into(profileImageView);
                        }
                    }
                } else {
                    showGroupDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter your group");
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupInput = input.getText().toString();
                if (!groupInput.isEmpty()) {
                    userGroup = Integer.parseInt(groupInput);
                    addUserToDatabase();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void addUserToDatabase() {
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String[] parts = userEmail.split("@");
            String[] userNameParts = parts[0].split("\\.");
            String userFirstName = capitalizeFirstLetter(userNameParts[1]);
            String userLastName = capitalizeFirstLetter(userNameParts[0]);
            String photoUrl = "";
            User user = new User(userEmail, userLastName, userFirstName, userGroup, photoUrl);
            userRef.setValue(user);
            userEmailTextView.setText(userEmail);
            userInfoTextView.setText(userFirstName + " " + userLastName);
            userGroupTextView.setText("Group: " + userGroup);
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
                profileImageView.setImageURI(profileImageUri);
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
                        userRef.child("photoURL").setValue(photoUrl)
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