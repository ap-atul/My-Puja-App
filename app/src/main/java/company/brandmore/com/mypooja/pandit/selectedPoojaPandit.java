package company.brandmore.com.mypooja.pandit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.utils.poojaListAdapter;

public class selectedPoojaPandit extends AppCompatActivity {

    private TextView poojaTitle, poojaTime, poojaDate, poojaDetails, poojaLocation, poojaPincode;
    private Button bid;

    public pooja poojaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_pooja_pandit);

        poojaTitle = findViewById(R.id.poojaTitle2);
        poojaTime = findViewById(R.id.poojaTime2);
        poojaDate = findViewById(R.id.poojaDate2);
        poojaDetails = findViewById(R.id.poojaDetails2);
        poojaLocation = findViewById(R.id.poojaLocation2);
        poojaPincode = findViewById(R.id.poojaPincode2);
        bid = findViewById(R.id.bid);

        final int id = getIntent().getIntExtra("id", 0);
        poojaSelected = poojaListAdapter.getPooja(id);

        setData(poojaSelected);

        bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });
    }

    //dialog for accepting bidding values
    private void setDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(selectedPoojaPandit.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bidding_pooja, null);

        final EditText fees = view.findViewById(R.id.fees);
        Button bid = view.findViewById(R.id.dialog_bid);
        Button cancel = view.findViewById(R.id.dialog_cancel);

        mBuilder.setView(view);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataToFirebase(fees);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //check for biddings:
    // if previously bid it won't be updated but
    // if not book status will be updated
    private void setDataToFirebase(final EditText fees) {
        final String key = poojaSelected.getRid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pooja");
        myRef.limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            for(DataSnapshot dsp2 : dsp.getChildren()){
                                if(dsp2.getKey().equals(key)){
                                    pooja object = dsp2.getValue(pooja.class);
                                    String Pkey = dsp.getKey();
                                    int oldBid = Integer.parseInt(object.getBid());
                                    setBooking(key, Pkey, oldBid, fees);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(selectedPoojaPandit.this, "Error updating booking value in user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //updating book status and writing bidding values
    private void setBooking(final String key, final String pkey, int oldBid, EditText fees) {
        String feesText = fees.getText().toString().trim();
        int commission = (Integer.parseInt(fees.getText().toString().trim()) / 100) * 10;
        final int newBid = oldBid + 1;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("booking/"+ key +"/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        database.child("fees").setValue(Integer.toString(Integer.parseInt(feesText) + commission));
        database.child("alloc").setValue("f")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(android.R.id.content), "Your bid has been sent", Snackbar.LENGTH_SHORT).show();
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("pooja/"+pkey+"/"+key);
                        newRef.child("bid").setValue(Integer.toString(newBid));
                        startActivity(new Intent(selectedPoojaPandit.this, panditHomePage.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "Problem sending your bid", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void setData(pooja poojaSelected) {
        poojaTitle.setText(poojaSelected.getTitle());
        poojaTime.setText(poojaSelected.getTime());
        poojaDate.setText(poojaSelected.getDate());
        poojaDetails.setText(poojaSelected.getDetails());
        poojaLocation.setText(poojaSelected.getPlace());
        poojaPincode.setText(poojaSelected.getPincode());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(selectedPoojaPandit.this, searchPoojaList.class);
        finish();
        startActivity(intent);
    }
}
