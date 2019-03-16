package company.brandmore.com.mypooja.pandit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import company.brandmore.com.mypooja.R;
import company.brandmore.com.mypooja.common.chats;
import company.brandmore.com.mypooja.common.contactUs;
import company.brandmore.com.mypooja.loginActivity;
import company.brandmore.com.mypooja.models.pandit;
import company.brandmore.com.mypooja.models.userPerson;
import company.brandmore.com.mypooja.user.paymentResult;
import company.brandmore.com.mypooja.user.selectedPandit;

public class panditHomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView panditEmail, panditName;
    private ImageView imageView;
    private RelativeLayout chatsBtn;

    public static pandit panditObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandit_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pandit_toolbar);
        setSupportActionBar(toolbar);


        chatsBtn = findViewById(R.id.chatsBtn);
        chatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(panditHomePage.this, chats.class));
            }
        });

        RelativeLayout fab = findViewById(R.id.pandit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(panditHomePage.this, searchPoojaList.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pandit_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.pandit_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        panditEmail = header.findViewById(R.id.panditEmail);
        panditName = header.findViewById(R.id.panditName);
        imageView = header.findViewById(R.id.panditImage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pandits/"+user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                panditObject = dataSnapshot.getValue(pandit.class);
                if(panditObject != null){
                    Picasso.get().load(panditObject.getProfilePic()).into(imageView);
                    panditName.setText(panditObject.getName());
                }else{
                    panditName.setText(null);
                    startActivity(new Intent(panditHomePage.this, profilePandit.class));
                    Toast.makeText(panditHomePage.this, "Complete your profile first!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userPerson person = loginActivity.getUserObject();
        panditEmail.setText(person.getEmail());
    }

    public static pandit getPanditObject(){
        return panditObject;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pandit_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pandit_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.pandit_action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.pandit_nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(panditHomePage.this, loginActivity.class));
        } else if (id == R.id.nav_pandit_about) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(panditHomePage.this);
            builder1.setMessage("We are a group of people who wants user to get in touch with Pandit Ji and persorm their pujan as expected!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else if (id == R.id.nav_pandit_contact) {
            startActivity(new Intent(panditHomePage.this, contactUs.class));
        }else if (id == R.id.nav_pandit_profile) {
            startActivity(new Intent(panditHomePage.this, profilePandit.class));
        }
//
//        } else if (id == R.id.nav_about) {
//
//        } else if (id == R.id.nav_contact) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pandit_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
