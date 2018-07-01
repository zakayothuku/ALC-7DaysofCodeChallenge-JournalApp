package alc.sevendayschallenge.journal.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.activities.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();

    private EditText emailAddress;
    private FirebaseAuth mAuth;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        emailAddress = findViewById(R.id.emailAddress);
        email = emailAddress.getText().toString();

        Button sendResetLink = findViewById(R.id.sendResetLink);
        sendResetLink.setOnClickListener(view -> {
            if (emailAddress.getText().length() > 0) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                onBackPressed();
                                Toast.makeText(ResetPasswordActivity.this, "Check your email to rest your password", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Email sent.");
                            }
                        });
            }else {
                Toast.makeText(this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
