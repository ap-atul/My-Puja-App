package company.brandmore.com.mypooja.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.common.chats;
import company.brandmore.com.mypooja.models.message;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.pandit.selectedPoojaPandit;
import company.brandmore.com.mypooja.utils.panditListAdapter;

public class selectedPandit extends AppCompatActivity{

    public int id;
    public String poojaId;
    public pandit panditSelected;

    private DatabaseReference database;
    private Button bookPandit;
    private ImageView panditPic1;
    private TextView panditName1, panditEmail1, panditExperience1, panditAddress1, panditCity1, panditFees;
    public static String totalFees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_pandit);

        //starting payment service (preload)
        Checkout.preload(getApplicationContext());

        id = getIntent().getIntExtra("id", 0);
        panditSelected = panditListAdapter.getSelectedPandit(id);
        poojaId = selectedPooja.getSelectedPoojaId();
        bookPandit = findViewById(R.id.book_pandit);
        panditPic1 = findViewById(R.id.panditPic1);
        panditName1 = findViewById(R.id.panditName1);
        panditEmail1 = findViewById(R.id.panditEmail1);
        panditExperience1 = findViewById(R.id.panditExperience1);
        panditAddress1 = findViewById(R.id.panditAddress1);
        panditCity1 = findViewById(R.id.panditCity1);
        panditFees = findViewById(R.id.panditFees);

        totalFees = panditSelected.getFees();

        Picasso.get().load(panditSelected.getProfilePic()).into(panditPic1);
        panditName1.setText(panditSelected.getName());
        panditEmail1.setText(panditSelected.getEmail());
        panditExperience1.setText(panditSelected.getExperience());
        panditAddress1.setText(panditSelected.getAddress());
        panditCity1.setText(panditSelected.getCity());
        panditFees.setText(totalFees);

        database = FirebaseDatabase.getInstance().getReference("booking");

        if (panditSelected.getAlloc().equals("f")) {
            bookPandit.setEnabled(true);
            bookPandit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialog();
                }
            });
        }else{
            bookPandit.setEnabled(false);
        }
    }

    private void setDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(selectedPandit.this);
        builder1.setMessage("Proceed With Payment");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivityForResult(new Intent(selectedPandit.this, paymentResult.class), 11);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void updateDatabase() {
        database.child(poojaId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final String panditId = panditList.getSelectedPanditKey(id);

                database.child(poojaId).child(panditId).child("alloc").setValue("t");
                database.child(poojaId).child(panditId).child("fees").setValue(panditSelected.getFees());
                database.child(poojaId).child(panditId).child("payment").setValue("t");

                //start messaging session
                message object = new message(panditId, "Hi, lets get to details");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentDateandTime = sdf.format(new Date());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                 databaseReference
                                    .child("chats")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + panditId)
                                    .child(currentDateandTime)
                                    .setValue(object);

                Snackbar.make(findViewById(android.R.id.content), "Pandit Ji has been booked", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(selectedPandit.this, chats.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 11 && resultCode == 2){
            if(data.getStringExtra("result").equals("f")){
                updateDatabase();
            }
        }
    }
}
