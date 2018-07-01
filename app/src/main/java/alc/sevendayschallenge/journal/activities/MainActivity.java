package alc.sevendayschallenge.journal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.auth.LoginActivity;
import alc.sevendayschallenge.journal.models.Entry;
import alc.sevendayschallenge.journal.viewholders.EntryViewHolder;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView entriesRecyclerView;
    private FirebaseRecyclerAdapter<Entry, EntryViewHolder> entryFirebaseRecyclerAdapter;

    // Reference to Firebase Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.new_entry_button);
        fab.setOnClickListener(view -> {
            Intent newEntryIntent = new Intent(MainActivity.this, EntryActivity.class);
            startActivity(newEntryIntent);
        });

        entriesRecyclerView = findViewById(R.id.entries_recycler_view);
        entriesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);

        entriesRecyclerView.setLayoutManager(layoutManager);

        initDatabase();
        loadJournalEntries();
    }

    private void initDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // create a leaf on root
        databaseReference = database.getReference("entries");
    }

    private void loadJournalEntries() {

        /*
         * Query the database for results
         * */
        Query entriesQuery = getQuery(databaseReference);

        entryFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Entry, EntryViewHolder>(Entry.class, R.layout.entry_list_item,
                EntryViewHolder.class, entriesQuery) {

            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, Entry model, int position) {

                final DatabaseReference entryReference = getRef(position);
                final String entryKey = entryReference.getKey();

                // Bind Entry Model to ViewHolder
                viewHolder.bindToEntry(model);

                viewHolder.itemView.setOnClickListener(v -> {
                    Intent editJournalEntryintent = new Intent(MainActivity.this, EntryActivity.class);
                    editJournalEntryintent.putExtra("entry_key", entryKey);
                    startActivity(editJournalEntryintent);
                });

            }
        };

        entriesRecyclerView.setAdapter(entryFirebaseRecyclerAdapter);

    }

    public Query getQuery(DatabaseReference databaseReference) {
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
