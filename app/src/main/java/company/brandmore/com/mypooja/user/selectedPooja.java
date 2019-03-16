package company.brandmore.com.mypooja.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Date;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.models.pooja;
import company.brandmore.com.mypooja.utils.poojaListAdapter;

public class selectedPooja extends AppCompatActivity {

    private TextView poojaTitle, poojaTime, poojaDate, poojaDetails, poojaLocation, poojaPincode;
    private Button showPandit, deletePooja;

    public static pooja poojaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_pooja);

        poojaTitle = findViewById(R.id.poojaTitle);
        poojaTime = findViewById(R.id.poojaTime);
        poojaDate = findViewById(R.id.poojaDate);
        poojaDetails = findViewById(R.id.poojaDetails);
        poojaLocation = findViewById(R.id.poojaLocation);
        poojaPincode = findViewById(R.id.poojaPincode);
        showPandit = findViewById(R.id.showPandits);
        deletePooja = findViewById(R.id.deletePooja);

        final int id = getIntent().getIntExtra("id", 0);
        poojaSelected = poojaListAdapter.getPooja(id);

        setData(poojaSelected);
        showPandit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(selectedPooja.this, panditList.class).putExtra("id",id));
            }
        });

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            try {
//                Date date = dateFormat.parse(poojaDate.getText().toString());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

        deletePooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pooja/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
                AlertDialog.Builder builder1 = new AlertDialog.Builder(selectedPooja.this);
                builder1.setMessage("Are you sure you want to delete all the requirements for your Puja? You will no longer have access to it.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                databaseReference.child(poojaSelected.getRid()).removeValue();
                                Snackbar.make(findViewById(android.R.id.content), "Your Puja has been deleted!", Snackbar.LENGTH_SHORT).show();
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

    public static String getSelectedPoojaId(){
        return poojaSelected.getRid();
    }
}
