package company.brandmore.com.mypooja;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //enable offline capabilities
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent openMainActivity =  new Intent(splashActivity.this, loginActivity.class);
                startActivity(openMainActivity);
                finish();

            }
        }, 3000);
    }
}
