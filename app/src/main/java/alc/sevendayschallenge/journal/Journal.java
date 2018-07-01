package alc.sevendayschallenge.journal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Journal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
