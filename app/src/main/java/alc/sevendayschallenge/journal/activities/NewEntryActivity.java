package alc.sevendayschallenge.journal.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.models.Entry;

public class NewEntryActivity extends AppCompatActivity {

    private static final String TAG = NewEntryActivity.class.getSimpleName();

    private EditText entryTitle;
    private EditText entryContent;

    // Reference to Firebase Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.add_new_entry_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewJournalEntry(view);
            }
        });

        entryTitle = findViewById(R.id.entry_title_et);
        entryContent = findViewById(R.id.entry_content_et);

        initDatabase();
    }

    private void initDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // create a leaf on root
        databaseReference = database.getReference("entries");
    }

    private void addNewJournalEntry(final View view) {

        String title = entryTitle.getText().toString();
        String content = entryContent.getText().toString();
        Long createdAt = new Date().getTime();
        String key = databaseReference.push().getKey();

        if (title.equals("") || title.isEmpty()) {
            Toast.makeText(this, "Make sure to add a title :)", Toast.LENGTH_SHORT).show();
        } else {

            Entry entry = new Entry();
            entry.setTitle(title);
            entry.setContent(content);
            entry.setTimestamp(createdAt);
            entry.setKey(key);

            // send data to firebase
            databaseReference.child(key).setValue(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "addNewJournalEntry: onComplete");
                    Toast.makeText(NewEntryActivity.this, "Journal entry saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "addNewJournalEntry: onComplete");
                }
            });
        }
    }

}
