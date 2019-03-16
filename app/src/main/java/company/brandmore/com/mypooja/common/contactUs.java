package company.brandmore.com.mypooja.common;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import company.brandmore.com.mypooja.R;

public class contactUs extends AppCompatActivity {

    private EditText subject, message;
    private FloatingActionButton sendMessage;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        subject = findViewById(R.id.subject);
        message = findViewById(R.id.messsage);
        sendMessage = findViewById(R.id.sendMessage);

        databaseReference = FirebaseDatabase.getInstance().getReference("messages/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatatoFirebase(databaseReference);
            }
        });
    }

    private void addDatatoFirebase(DatabaseReference databaseReference) {
        String subj = subject.getText().toString().trim();
        String msg = message.getText().toString();

        String key = databaseReference.push().getKey();
        databaseReference.child(key).child("subject").setValue(subj);
        databaseReference.child(key).child("message").setValue(msg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(contactUs.this, "Your message has been sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(contactUs.this, "Problem sending your message", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
