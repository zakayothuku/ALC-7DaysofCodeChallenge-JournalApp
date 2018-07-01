package alc.sevendayschallenge.journal.auth;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.activities.BaseActivity;
import alc.sevendayschallenge.journal.activities.MainActivity;
import alc.sevendayschallenge.journal.models.User;

public class SignupActivity extends BaseActivity {


    private EditText displayName, inputEmail, inputPassword;
    private Button signUp;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");

        displayName = findViewById(R.id.displayName);
        inputEmail = findViewById(R.id.emailAddress);
        inputPassword = findViewById(R.id.password);

        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(v -> {

            String fullName = displayName.getText().toString();
            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(getApplicationContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Email cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(getApplicationContext(), "Password cannot be less than 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog("Processing...");

            //authenticate user
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {

                if (task.isSuccessful()) {

                    User user = new User(
                            "",
                            getUid(),
                            email,
                            fullName,
                            "",
                            System.currentTimeMillis(),
                            System.currentTimeMillis()
                    );

                    mDatabaseUsers.child(getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        hideProgressDialog();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });

                } else {
                    hideProgressDialog();
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                }
            });
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
