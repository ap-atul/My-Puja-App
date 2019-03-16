package company.brandmore.com.mypooja;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.pandit.panditHomePage;
import company.brandmore.com.mypooja.user.homePage;
import company.brandmore.com.mypooja.utils.validationInput;

public class loginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView register, forgotPassword;
    private Button login;
    private ProgressBar progressBar;

    public validationInput validator;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static userPerson person;

    public static String emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.registerText);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        //data validation
        validator = new validationInput();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        if(checkNetwork()){
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validateData()){
                        signInUser();
                    }
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(loginActivity.this, registerActivity.class));
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(findViewById(android.R.id.content), "Check your MailBox!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }else{
            Snackbar.make(findViewById(android.R.id.content), "Check your Network Connection!", Snackbar.LENGTH_LONG).show();
        }

    }

    private void signInUser() {
        emailText = email.getText().toString().trim();
        passwordText = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            checkUserType(user);
                        }
                    } else {
                        // If sign in fails
                        Snackbar.make(findViewById(android.R.id.content), "Please check your Email & Password", Snackbar.LENGTH_SHORT).show();
                        progressBarSet(false);
                    }
                    }
                });
    }

    private void checkUserType(FirebaseUser user) {
        person = new userPerson();
        String uid = user.getUid();

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person = dataSnapshot.getValue(userPerson.class);
                if(person != null){
                    String type = person.getUserType();
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        progressBarSet(false);
                        if(type.toUpperCase().equals("YAJMAN")){
                            startActivity(new Intent(loginActivity.this, homePage.class));
                        }else{
                            startActivity(new Intent(loginActivity.this, panditHomePage.class));
                        }
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "Check your MailBox!", 5000)
                                .setAction("RESEND", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.send))
                                .show();
                        progressBarSet(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(findViewById(android.R.id.content), "Server error while retrieving data", Snackbar.LENGTH_SHORT).show();
                progressBarSet(false);
            }
        });
    }

    private boolean validateData() {
        progressBarSet(true);
        if(validator.checkEmail(email, email.getText().toString().trim(), "Please correct your email")){
            return true;
        }
        return false;
    }

    private void progressBarSet(boolean b) {
        if(b)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    public boolean checkNetwork(){
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(checkNetwork()){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null){
                progressBarSet(true);
                checkUserType(user);
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Check your Network Connection!", Snackbar.LENGTH_LONG).show();
        }
    }

    public static userPerson getUserObject(){
        return person;
    }

}
