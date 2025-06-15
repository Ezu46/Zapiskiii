package com.example.zapiskiii.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapiskiii.R;
import com.example.zapiskiii.data.model.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DiaryEntryAdapter extends ListAdapter<DiaryEntry, DiaryEntryAdapter.EntryViewHolder> {

    private OnItemClickListener listener;
    private int fontSizeSp = 16; // default

    public DiaryEntryAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setFontSizeFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("diary_prefs", Context.MODE_PRIVATE);
        int idx = prefs.getInt("font_size", 1); // 0 - small, 1 - medium, 2 - large
        if (idx == 0) fontSizeSp = 14;
        else if (idx == 1) fontSizeSp = 16;
        else if (idx == 2) fontSizeSp = 20;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<DiaryEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<DiaryEntry>() {
        @Override
        public boolean areItemsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getContent().equals(newItem.getContent()) &&
                   oldItem.getDate() == newItem.getDate();
        }
    };

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_entry, parent, false);
        return new EntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DiaryEntry currentEntry = getItem(position);
        holder.textViewTitle.setText(currentEntry.getTitle());
        holder.textViewContentSnippet.setText(currentEntry.getContent());

        holder.textViewTitle.setTextSize(fontSizeSp);
        holder.textViewContentSnippet.setTextSize(fontSizeSp - 2);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        holder.textViewDate.setText(sdf.format(currentEntry.getDate()));
        holder.textViewDate.setTextSize(fontSizeSp - 4);
    }

    public DiaryEntry getEntryAt(int position) {
        return getItem(position);
    }

    class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewContentSnippet;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewContentSnippet = itemView.findViewById(R.id.text_view_content_snippet);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
