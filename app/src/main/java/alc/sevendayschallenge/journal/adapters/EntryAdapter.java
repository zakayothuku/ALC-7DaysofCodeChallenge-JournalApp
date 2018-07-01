package alc.sevendayschallenge.journal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import alc.sevendayschallenge.journal.R;
import alc.sevendayschallenge.journal.models.Entry;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryAdapterViewHolder> {

    private ArrayList<Entry> entriesList;

    public EntryAdapter(ArrayList<Entry> entriesList) {
        this.entriesList = entriesList;
    }

    @NonNull
    @Override
    public EntryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.entry_list_item, parent, false);
        return new EntryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryAdapterViewHolder entryAdapterViewHolder, int position) {
        // entryAdapterViewHolder.entryDate.setText(entriesList.get(position).getTimestamp());
        entryAdapterViewHolder.entryTitle.setText(entriesList.get(position).getTitle());
        entryAdapterViewHolder.entryContent.setText(entriesList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    public class EntryAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView entryDate;
        final TextView entryTitle;
        final TextView entryContent;

        public EntryAdapterViewHolder(View itemView) {
            super(itemView);
            entryDate = itemView.findViewById(R.id.entry_date);
            entryTitle = itemView.findViewById(R.id.entry_title);
            entryContent = itemView.findViewById(R.id.entry_content);
        }
    }

}
