package com.example.yourstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.databinding.FragmentScheduleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;

public class ScheduleFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
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


        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }
    }
    ImageView photoSchedule;
    FragmentScheduleBinding binding;
    StorageReference stoRef;
    File localfile;
    DatabaseReference userRef;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        photoSchedule = view.findViewById(R.id.photo_schedule);
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        stoRef = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com").getReference().child("Schedule/3Group.jpg");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users").child(currentUser.getUid());
        loadUserData();

        photoSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localfile != null) {
                    Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                    intent.putExtra("image_path", localfile.getAbsolutePath());
                    startActivity(intent);
                } else {
                }
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
                        int group = user.getGroup();
                        switch (group) {
                            case 3:
                                stoRef = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com")
                                        .getReference().child("Schedule/3Group/3Schedule.jpg");
                                loadScheduleImage();
                                break;
                            case 4:
                                stoRef = FirebaseStorage.getInstance("gs://your-study-a761b.appspot.com")
                                        .getReference().child("Schedule/4Group/4Schedule.jpg");
                                loadScheduleImage();
                                break;
                            default:
                                break;

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обробка помилок бази даних
            }
        });
    }

    private void loadScheduleImage() {
        try {
            localfile = File.createTempFile("tempfile", ".jpg");
            stoRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    photoSchedule.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

