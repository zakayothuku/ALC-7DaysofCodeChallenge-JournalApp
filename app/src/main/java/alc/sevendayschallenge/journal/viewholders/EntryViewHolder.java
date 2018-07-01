package alc.sevendayschallenge.journal.viewholders;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.models.Entry;

public class EntryViewHolder extends RecyclerView.ViewHolder {

    private final TextView entryDate;
    private final TextView entryTitle;
    private final TextView entryContent;
    private final TextView entryMonth;
    private final TextView entryTime;
    private final TextView entryYear;
    private final CardView dataHolder;

    public EntryViewHolder(View itemView) {
        super(itemView);
        entryTitle = itemView.findViewById(R.id.entry_title);
        entryContent = itemView.findViewById(R.id.entry_content);

        entryDate = itemView.findViewById(R.id.entry_date);
        entryTime = itemView.findViewById(R.id.entry_time);
        entryMonth = itemView.findViewById(R.id.entry_month);
        entryYear = itemView.findViewById(R.id.entry_year);
        dataHolder = itemView.findViewById(R.id.data_holder);
    }

    public void bindToEntry(Entry entry) {
        entryTitle.setText(entry.getTitle());
        entryContent.setText(entry.getContent());

        Date addedAt = entry.getAddedAt();

        Calendar cal = Calendar.getInstance();
        cal.setTime(addedAt);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        entryDate.setText(String.valueOf(cal.get(Calendar.DATE)));
        entryTime.setText(sdf.format(addedAt));
        entryMonth.setText(String.valueOf(cal.get(Calendar.MONTH)));
        entryYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int roomColor = generator.getColor(entry.getKey());
        dataHolder.setCardBackgroundColor(roomColor);
    }

}
