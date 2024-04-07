package com.example.yourstudy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private int userGroup = 0;
    private TextView userEmailTextView;
    private TextView userInfoTextView;
    private TextView userGroupTextView;

    public static class User {
        private String email;
        private String lastName;
        private String firstName;
        private int group;

        public User() {
        }

        public User(String email, String lastName, String firstName, int group) {
            this.email = email;
            this.lastName = lastName;
            this.firstName = firstName;
            this.group = group;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("Users");

    private void addUser(String email, String lastName, String firstName, int group) {
        User user = new User(email, lastName, firstName, group);
        String userId = myRef.push().getKey();

        myRef.child(userId).setValue(user);
    }

    @Nullable
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
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            myRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                String firstName = user.getFirstName();
                                String lastName = user.getLastName();
                                int group = user.getGroup();
                                userEmailTextView.setText(userEmail);
                                userInfoTextView.setText(firstName + " " + lastName);
                                userGroupTextView.setText("Group: " + group);
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
    }

    private void showGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Your Group");

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

    private void addUserToDatabase() {
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String[] parts = userEmail.split("@");
            String[] userNameParts = parts[0].split("\\.");
            String userFirstName = capitalizeFirstLetter(userNameParts[1]);
            String userLastName = capitalizeFirstLetter(userNameParts[0]);

            addUser(userEmail, userLastName, userFirstName, userGroup);

            userEmailTextView.setText(userEmail);
            userInfoTextView.setText(userFirstName + " " + userLastName);
            userGroupTextView.setText("Group: " + userGroup);
        }
    }

    private String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
