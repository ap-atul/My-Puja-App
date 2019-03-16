package company.brandmore.com.mypooja;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.utils.validationInput;

public class registerActivity extends AppCompatActivity {

    private EditText email, password;
    private Button register;
    private Spinner spinner;
    private ProgressBar progressBar;

    public validationInput validator;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.passwordReg);
        register = findViewById(R.id.register);
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.items, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        validator = new validationInput();
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("users");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getSelectedItem().toString().equals("Yajman")){
                    findViewById(R.id.registerBG).setBackgroundColor(getResources().getColor(R.color.reg_theme_person));
                    register.setTextColor(getResources().getColor(R.color.reg_theme_person));
                }else{
                    findViewById(R.id.registerBG).setBackgroundColor(getResources().getColor(R.color.reg_theme_pandit));
                    register.setTextColor(getResources().getColor(R.color.reg_theme_pandit));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    registerUser();
                }
            }
        });

        (findViewById(R.id.loginText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this, loginActivity.class));
            }
        });
    }

    //validating all the inputs
    private boolean validateData() {
        progressBarSet(true);
        return validator.checkEmail(email, email.getText().toString().trim(), "Please correct your email") && password.getText().toString().trim().length() != 0;
    }

    //registering the user with firebase
    private void registerUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null)
                                addThisUserToDatabase(user);
                        } else {
                            // If sign in fails
                            Snackbar.make(findViewById(android.R.id.content), "User registration failed, try different password!", Snackbar.LENGTH_SHORT).show();
                            progressBarSet(false);
                        }
                    }
                });
    }

    private void createSnackBar(final FirebaseUser user) {

        Snackbar.make(findViewById(android.R.id.content), "Email Verification Required", 50000)
                .setAction("SEND", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.send))
                .show();
    }

    //keeping records of the users
    private void addThisUserToDatabase(final FirebaseUser user) {
        userPerson person = new userPerson();

        person.setUserType(spinner.getSelectedItem().toString());
        person.setEmail(user.getEmail());

        myRef.child(user.getUid()).setValue(person)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBarSet(false);
                        Snackbar.make(findViewById(android.R.id.content), "User successfully registered!", Snackbar.LENGTH_SHORT).show();
                        createSnackBar(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "User registration failed try again later!", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void progressBarSet(boolean b) {
        if(b)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
