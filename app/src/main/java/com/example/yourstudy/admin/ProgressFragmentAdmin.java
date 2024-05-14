package com.example.yourstudy.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yourstudy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ProgressFragmentAdmin extends Fragment {
    public static class User {
        private String email;
        private String lastName;
        private String firstName;

        public User() {
        }

        public User(String email, String lastName, String firstName) {
            this.email = email;
            this.lastName = lastName;
            this.firstName = firstName;
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

    private static final String[] SUBJECTS = {"Англійська мова", "Графічне та геометричне програмування", "Менеджмент", "Об'єктно-орієнтоване програмування", "Сучасні мови програмування", "Теорія ймовірностей", "Технологія програмування"};

    private EditText searchInput;
    private ImageButton searchButton;
    private TextView studentInfoTextView;
    private EditText firstModuleGradeInput;
    private EditText secondModuleGradeInput;
    private ImageButton addGradesButton;
    private TextView averageGradeTextView;
    private TextView averageGradeTextView2;
    private Spinner subjectSpinner;
    private ArrayAdapter<String> subjectAdapter;
    private DatabaseReference databaseReference;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_admin, container, false);
        ArrayList<String> subjectsWithHint = new ArrayList<>(Arrays.asList(SUBJECTS));
        subjectsWithHint.add(0, "Оберіть предмет");
        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        studentInfoTextView = view.findViewById(R.id.student_info_text_view);
        firstModuleGradeInput = view.findViewById(R.id.first_module_grade_input);
        secondModuleGradeInput = view.findViewById(R.id.second_module_grade_input);
        addGradesButton = view.findViewById(R.id.add_grades_button);
        averageGradeTextView = view.findViewById(R.id.average_grade_text_view);
        averageGradeTextView2 = view.findViewById(R.id.average_grade_text_view2);
        subjectSpinner = view.findViewById(R.id.subject_spinner);

        subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectsWithHint);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim();

                if (query.isEmpty()) {
                    Toast.makeText(getContext(), "Поле порожнє", Toast.LENGTH_SHORT).show();
                    return;
                }

                Query searchQuery;
                if (query.contains("@")) {
                    searchQuery = databaseReference.orderByChild("email").equalTo(query);
                } else {
                    String[] parts = query.split(" ");
                    String firstNameQuery = capitalizeFirstLetter(parts[0]);
                    String lastNameQuery = parts.length > 1 ? capitalizeFirstLetter(parts[1]) : "";

                    searchQuery = databaseReference.orderByChild("firstName")
                            .startAt(firstNameQuery)
                            .endAt(firstNameQuery + "\uf8ff")
                            .limitToFirst(1);
                }

                searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    displayUserInfo(user);
                                    return;
                                }
                            }
                            Toast.makeText(getContext(), "Студента не знайдено", Toast.LENGTH_SHORT).show();
                            studentInfoTextView.setText("");
                        } else {
                            Toast.makeText(getContext(), "Студента не знайдено", Toast.LENGTH_SHORT).show();
                            studentInfoTextView.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        addGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstModuleGrade = firstModuleGradeInput.getText().toString().trim();
                String secondModuleGrade = secondModuleGradeInput.getText().toString().trim();
                String selectedSubject = subjectSpinner.getSelectedItem().toString();

                if (email == null || email.isEmpty()) {
                    Toast.makeText(getContext(), "Спочатку знайдіть студента", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!firstModuleGrade.isEmpty() && !secondModuleGrade.isEmpty()) {
                    int firstGrade = Integer.parseInt(firstModuleGrade);
                    int secondGrade = Integer.parseInt(secondModuleGrade);

                    if (firstGrade > 100 || secondGrade > 100) {
                        Toast.makeText(getContext(), "Бал не може бути більший за 100", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int averageGrade = (firstGrade + secondGrade) / 2;


                    String averageGradeString = String.valueOf(averageGrade);

                    averageGradeTextView2.setText(averageGradeString);

                    addGradesToDatabase(email, firstModuleGrade, secondModuleGrade, selectedSubject, averageGradeString);
                } else {
                    Toast.makeText(getContext(), "Уведіть бали за обидва модулі", Toast.LENGTH_SHORT).show();
                }
            }
        });
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHint));
                    ((TextView) view).setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        return view;
    }

    private void displayUserInfo(User user) {
        if (user != null) {
            email = user.getEmail();
            String studentInfo = user.getFirstName() + " " + user.getLastName() + "\n" + user.getEmail();
            studentInfoTextView.setText(studentInfo);
        } else {
            Toast.makeText(getContext(), "Дані студента порожні", Toast.LENGTH_SHORT).show();
        }
    }

    private void addGradesToDatabase(String studentEmail, String firstModuleGrade, String secondModuleGrade, String subject, String averageGrade) {
        DatabaseReference gradesRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Grades");

        Query query = gradesRef.orderByChild("email").equalTo(studentEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child(subject + "FirstModuleGrade").setValue(firstModuleGrade);
                        snapshot.getRef().child(subject + "SecondModuleGrade").setValue(secondModuleGrade);
                        snapshot.getRef().child(subject + "AverageGrade").setValue(averageGrade);
                    }
                    Toast.makeText(getContext(), "Бали успішно оновлені", Toast.LENGTH_SHORT).show();
                } else {
                    String userUid = UUID.randomUUID().toString();
                    DatabaseReference newGradeRef = gradesRef.child(userUid);
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", studentEmail);
                    userData.put(subject + "FirstModuleGrade", firstModuleGrade);
                    userData.put(subject + "SecondModuleGrade", secondModuleGrade);
                    userData.put(subject + "AverageGrade", averageGrade);


                    newGradeRef.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Бали успішно оновлені", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Невдалось поставити бали", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String capitalizeFirstLetter(String input) {
        if (input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
