package androidsamples.java.journalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalEntryListAdapter extends RecyclerView.Adapter<JournalEntryListAdapter.EntryViewHolder> {

    private final LayoutInflater mInflater;
    public List<JournalEntry> mEntries;
    private onItemClickListener listener;

    public JournalEntryListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }
    class EntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtTitle;
        private final TextView mTxtDate;
        private final TextView mTxtStart;
        private final TextView mTxtEnd;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.txt_item_title);
            mTxtDate = itemView.findViewById(R.id.txt_item_date);
            mTxtStart = itemView.findViewById(R.id.txt_item_start_time);
            mTxtEnd = itemView.findViewById(R.id.txt_item_end_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = EntryViewHolder.this.getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(itemView, position);
                }
            });
        }
    }
    public interface onItemClickListener{
        void onItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.fragment_entry, parent, false);
        return new EntryViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull JournalEntryListAdapter.EntryViewHolder holder, int position) {
        if (mEntries != null) {
            JournalEntry current = mEntries.get(position);
            holder.mTxtTitle.setText(current.getTitle());
            holder.mTxtDate.setText(current.getDate());
            holder.mTxtStart.setText(current.getStartTime());
            holder.mTxtEnd.setText(current.getEndTime());
        }
    }
    @Override
    public int getItemCount() {
        return (mEntries == null) ? 0 : mEntries.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setEntries(List<JournalEntry> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }
}
