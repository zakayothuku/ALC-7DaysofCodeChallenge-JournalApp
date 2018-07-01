package alc.sevendayschallenge.journal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.adapters.EntryAdapter;
import alc.sevendayschallenge.journal.models.Entry;

public class MainActivity extends AppCompatActivity {

    private RecyclerView entriesRecyclerView;
    private EntryAdapter entryAdapter;

    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Entry> entries = new ArrayList<>();

    // Reference to Firebase Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.new_entry_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newEntryIntent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(newEntryIntent);
            }
        });

        entriesRecyclerView = findViewById(R.id.entries_recycler_view);
        entriesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        entriesRecyclerView.setLayoutManager(layoutManager);

        entryAdapter = new EntryAdapter(entries);

        entriesRecyclerView.setAdapter(entryAdapter);

        initDatabase();
        loadJournalEntries();
    }

    private void initDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // create a leaf on root
        databaseReference = database.getReference("entries");
    }

    private void loadJournalEntries() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Entry entry = dataSnapshot.getValue(Entry.class);
                entries.add(entry);
                entryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                Entry entryOnFB = dataSnapshot.getValue(Entry.class);

                // find the item by uid in list
                int index = entries.indexOf(entryOnFB);
                Entry entryOnList = entries.get(index);

                // update list from firebase
                if (entryOnFB != null) {
                    entryOnList.setTitle(entryOnFB.getTitle());
                    entryOnList.setContent(entryOnFB.getContent());
                }
                entryAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Entry entryOnFB = dataSnapshot.getValue(Entry.class);

                // find the item by uid in list
                int index = entries.indexOf(entryOnFB);

                if (index > -1) {
                    entries.remove(index);
                    entryAdapter.notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Query getQuery(DatabaseReference databaseReference) {
        databaseReference = databaseReference.child("entries");
        databaseReference.keepSynced(true);
        return databaseReference;
    }


}
