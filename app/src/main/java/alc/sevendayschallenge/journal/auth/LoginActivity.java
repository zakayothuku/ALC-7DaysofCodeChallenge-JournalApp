package alc.sevendayschallenge.journal.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.activities.BaseActivity;
import alc.sevendayschallenge.journal.activities.MainActivity;
import alc.sevendayschallenge.journal.models.User;

public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "AuthActivity";

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String Uid;

    private EditText inputEmail, inputPassword;
    private TextView register, forgot;
    private Button signIn;

    private DatabaseReference mDatabaseUsers;
    private long tsLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Google Auth */
        setupGoogleAuth();
        Button googleButton = findViewById(R.id.btn_google);
        googleButton.setOnClickListener(this);

        tsLong = System.currentTimeMillis();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");

        inputEmail = findViewById(R.id.emailAddress);
        inputPassword = findViewById(R.id.password);

        register = findViewById(R.id.register);
        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(v -> {

            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

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
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {

                hideProgressDialog();

                if (task.isSuccessful()) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void setupGoogleAuth() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void google_auth() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            authenticateGoogle(requestCode, data);

        } else {
            Toast.makeText(this, "Login failed, Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void authenticateGoogle(int requestCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthSession(account);
            } else {
                Toast.makeText(this, "Login failed, Try again later.", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthSession(final GoogleSignInAccount acct) {

        showProgressDialog("Logging in...");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "signInWithGoogleCredential:onComplete:" + task.isSuccessful());
                        checkUserExists(acct.getId(), Uid, acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().getAuthority() + acct.getPhotoUrl().getPath());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d(TAG, "signInCredential:onFailed:", task.getException());
                        Toast.makeText(LoginActivity.this, "Login Failed. Try again later.", Toast.LENGTH_SHORT).show();
                    }

                    hideProgressDialog();
                });
    }

    public void checkUserExists(final String googleID, final String userId, final String email, final String user_name, final String photoUrl) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (snapshot.child(userId).getValue() == null) {

                            User user = new User(
                                    googleID,
                                    getUid(),
                                    email,
                                    user_name,
                                    photoUrl,
                                    tsLong,
                                    tsLong
                            );

                            databaseReference.child("users").child(userId).runTransaction(new Transaction.Handler() {

                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    mutableData.setValue(user);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                }
                            });

                        }
                    }

                } else {

                    User user = new User(
                            googleID,
                            getUid(),
                            email,
                            user_name,
                            photoUrl,
                            tsLong,
                            tsLong
                    );

                    databaseReference.child("users").child(userId).runTransaction(new Transaction.Handler() {

                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            mutableData.setValue(user);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.i("onConnectionFailed: ", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_google:
                google_auth();
                break;
        }
    }

}
