package com.example.yourstudy.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProgressFragment extends Fragment {

    private DatabaseReference gradesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);


        gradesRef = FirebaseDatabase.getInstance("https://your-study-a761b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Grades");

        ImageButton englishGradeButton = view.findViewById(R.id.english_grade);
        ImageButton graphicsGradeButton = view.findViewById(R.id.graphics_grade);
        ImageButton managementGradeButton = view.findViewById(R.id.management_grades);
        ImageButton OOPGradeButton = view.findViewById(R.id.oop_grades);
        ImageButton ModernGradeButton = view.findViewById(R.id.modern_languages_grades);
        ImageButton ProbabilityGradeButton = view.findViewById(R.id.probability_theory_grades);
        ImageButton TechnologyGradeButton = view.findViewById(R.id.programming_technology_grades);
        englishGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEnglishGrades();
            }
        });
        graphicsGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGraphicsGrades();
            }
        });
        managementGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayManagementGrades();
            }
        });
        OOPGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOOPGrades();
            }
        });
        ModernGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayModernGradeButtonGrades();
            }
        });
        ProbabilityGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProbabilityButtonGrades();
            }
        });
        TechnologyGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTechnologyGrades();
            }
        });

        return view;
    }

    private void displayEnglishGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Англійська мова");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Англійська моваFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Англійська моваSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Англійська моваAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null && averageGrade == null) {
                                    builder.setMessage("Оцінки не виставлені");
                                } else {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayGraphicsGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Графічне та геометричне програмування");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Графічне та геометричне програмуванняFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Графічне та геометричне програмуванняSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Графічне та геометричне програмуванняAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null)
                                {
                                    builder.setMessage("Оцінки не виставлені");
                                }
                                else
                                {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayManagementGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Менеджмент");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("МенеджментFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("МенеджментSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("МенеджментAverageGrade").getValue(String.class);
                                if (firstModuleGrade == null && secondModuleGrade == null && averageGrade == null )
                                {
                                    builder.setMessage("Оцінки не виставлені");
                                }
                            else
                            {
                                String message = "Перший модуль: " + firstModuleGrade +
                                        "\nДругий модуль: " + secondModuleGrade +
                                        "\nПідсумковий бал: " + averageGrade;

                                builder.setMessage(message);
                            }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayOOPGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Об'єктно-орієнтоване програмування");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Об'єктно-орієнтоване програмуванняFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Об'єктно-орієнтоване програмуванняSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Об'єктно-орієнтоване програмуванняAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null)
                                {
                                    builder.setMessage("Оцінки не виставлені");
                                }
                                else
                                {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayModernGradeButtonGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Сучасні мови програмування");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Сучасні мови програмуванняFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Сучасні мови програмуванняSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Сучасні мови програмуванняAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null && averageGrade == null) {
                                    builder.setMessage("Оцінки не виставлені");
                                } else {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayProbabilityButtonGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Теорія ймовірностей");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Теорія ймовірностейFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Теорія ймовірностейSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Теорія ймовірностейAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null && averageGrade == null) {
                                    builder.setMessage("Оцінки не виставлені");
                                } else {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayTechnologyGrades() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Технологія програмування");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();
        gradesRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstModuleGrade = snapshot.child("Технологія програмуванняFirstModuleGrade").getValue(String.class);
                                String secondModuleGrade = snapshot.child("Технологія програмуванняSecondModuleGrade").getValue(String.class);
                                String averageGrade = snapshot.child("Технологія програмуванняAverageGrade").getValue(String.class);

                                if (firstModuleGrade == null && secondModuleGrade == null && averageGrade == null) {
                                    builder.setMessage("Оцінки не виставлені");
                                } else {
                                    String message = "Перший модуль: " + firstModuleGrade +
                                            "\nДругий модуль: " + secondModuleGrade +
                                            "\nПідсумковий бал: " + averageGrade;

                                    builder.setMessage(message);
                                }
                            }
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
