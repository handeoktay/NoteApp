package com.example.asus.noteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.asus.noteapp.Room.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Filterable {
    private Context context;
    private List<Note> notes = new ArrayList<>();
    private List<Note> notesListFull;
    private OnItemClickListener listener;

    public NoteAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
        holder.itemView.setBackgroundColor(context.getResources().getColor(getBackgroundColor(currentNote.getPriority())));

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    //Set the observed list from MainActivity in RecyclerView
    public void setNotes(List<Note> notes) {
        this.notes.clear();
        this.notes.addAll(notes);
        notesListFull = new ArrayList<>(notes);
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }



    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.textview_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }
    //Set listener on each Note
    public interface OnItemClickListener {
        void onItemClick(Note note);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    //For search filter
    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private Filter noteFilter = new Filter() {
        //performFiltering() automatically be executed on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Note> filteredList = new ArrayList<>(); //contains filtered items

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(notesListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Note note : notesListFull) {
                    if (note.getTitle().toLowerCase().contains(filterPattern) || note.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;  //pass to publishResults()
        }

        //Results will be automatically publish to the UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
           //  setNotes((List) filterResults.values);
            notes.clear();
            notes.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    private int getBackgroundColor(int priority) {
        switch (priority % 10) {
            case 0:
                return R.color.color1;
            case 1:
                return R.color.color10;
            case 2:
                return R.color.color9;
            case 3:
                return R.color.color8;
            case 4:
                return R.color.color7;
            case 5:
                return R.color.color6;
            case 6:
                return R.color.color5;
            case 7:
                return R.color.color4;
            case 8:
                return R.color.color3;
            case 9:
                return R.color.color2;
        }
        return R.color.color_toolbar;
    }
}

