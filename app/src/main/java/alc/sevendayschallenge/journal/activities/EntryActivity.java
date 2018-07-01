package alc.sevendayschallenge.journal.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.models.Entry;

public class EntryActivity extends AppCompatActivity {

    private static final String TAG = EntryActivity.class.getSimpleName();

    private EditText entryTitle;
    private EditText entryContent;

    private boolean isUpdating = false;
    private String entryKey;

    // Reference to Firebase Database
    private DatabaseReference databaseReference;

    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    // Listeners
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.add_new_entry_button);
        fab.setOnClickListener(view -> {
            if (isUpdating) {
                updateJournalEntry();
            } else {
                addNewJournalEntry();
            }
        });

        entryTitle = findViewById(R.id.entry_title_et);
        entryContent = findViewById(R.id.entry_content_et);

        initDatabase();
        getEntryIfExists();
    }

    private void getEntryIfExists() {

        if (getIntent().getExtras() != null) {

            isUpdating = true;

            entryKey = getIntent().getStringExtra("entry_key");

            valueEventListener = databaseReference.child(entryKey).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Entry entry = dataSnapshot.getValue(Entry.class);
                    if (entry != null) {
                        entryTitle.setText(entry.getTitle());
                        entryContent.setText(entry.getContent());
                        createdAt = entry.getAddedAt();
                        updatedAt = entry.getUpdatedAt();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void initDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // create a leaf on root
        databaseReference = database.getReference("entries");
    }

    private void addNewJournalEntry() {

        title = entryTitle.getText().toString();
        content = entryContent.getText().toString();
        createdAt = new Date();
        String key = databaseReference.push().getKey();

        if (title.equals("") || title.isEmpty()) {
            Toast.makeText(this, "Make sure to add a title :)", Toast.LENGTH_SHORT).show();
        } else {

            Entry entry = new Entry();
            entry.setTitle(title);
            entry.setContent(content);
            entry.setAddedAt(createdAt);
            entry.setUpdatedAt(createdAt);
            entry.setKey(key);

            // send data to firebase
            assert key != null;
            databaseReference.child(key).setValue(entry).addOnCompleteListener(task -> {

                Log.d(TAG, "addNewJournalEntry: onComplete");
                Toast.makeText(EntryActivity.this, "Journal entry saved!", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> Log.d(TAG, "addNewJournalEntry: onComplete"));

            finish();
        }
    }

    private void updateJournalEntry() {

        title = entryTitle.getText().toString();
        content = entryContent.getText().toString();
        Date updatedAt = new Date();

        if (title.equals("") || title.isEmpty()) {
            Toast.makeText(this, "Make sure to add a title :)", Toast.LENGTH_SHORT).show();
        } else {

            Entry entry = new Entry();
            entry.setTitle(title);
            entry.setContent(content);
            entry.setAddedAt(createdAt);
            entry.setUpdatedAt(updatedAt);
            entry.setKey(entryKey);

            // send data to firebase
            databaseReference.child(entryKey).setValue(entry).addOnCompleteListener(task -> {

                Log.d(TAG, "updateJournalEntry: addOnCompleteListener");
                Toast.makeText(EntryActivity.this, "Journal entry updated!", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> Log.d(TAG, "updateJournalEntry: addOnFailureListener"));

            finish();
        }

    }

    private void deleteJournalEntry() {

        title = entryTitle.getText().toString();
        content = entryContent.getText().toString();

        if (title.equals("") || title.isEmpty()) {
            finish();
        } else {
            databaseReference.child(entryKey).removeValue().addOnCompleteListener(task -> {

                Log.d(TAG, "deleteJournalEntry: addOnSuccessListener");
                Toast.makeText(EntryActivity.this, "Journal entry deleted!", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> Log.d(TAG, "deleteJournalEntry: addOnFailureListener"));

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_entry) {
            deleteJournalEntry();
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null)
            databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null)
            databaseReference.removeEventListener(valueEventListener);
    }
}
