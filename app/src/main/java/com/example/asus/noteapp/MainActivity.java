package com.example.asus.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.asus.noteapp.Room.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "com.example.asus.noteapp.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.asus.noteapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.asus.noteapp.EXTRA_PRIORITY";
    public static final String EXTRA_ID = "com.example.asus.noteapp.EXTRA_ID";
    public static final int ADD_NOTE_REQUEST_CODE = 1;

    final NoteAdapter adapter = new NoteAdapter(MainActivity.this);  //NoteAdapter for RecyclerView
    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton buttonAddNode;                             //Floating Action Button for adding new note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonAddNode = findViewById(R.id.button_add_node);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.main_activity_action_bar);

        buildRecyclerView();
        createViewModel();
        createItemTouchHelper();
        setClickListeners();

    }


    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void createViewModel() {
        //ViewModel
        noteViewModel = ViewModelProviders.of(MainActivity.this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });
    }

    private void createItemTouchHelper() {
        //Swipe for delete a note effect
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //delete node which swipe position
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setClickListeners() {
        //Floating Action Button for adding new note
        buttonAddNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);  //to get back data as a result of new activity
            }
        });

        //Click listener for each note
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, DisplayNote.class);
                intent.putExtra(EXTRA_TITLE, note.getTitle());
                intent.putExtra(EXTRA_ID, note.getId());
                intent.putExtra(EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(EXTRA_PRIORITY, note.getPriority());
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved:(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        //Searchview
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        //Collapse the search action item when its focus is lost
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean newViewFocus) {
                if (!newViewFocus) {
                    //Collapse the action item.
                    searchView.onActionViewCollapsed();
                    //Clear the filter/search query.
                    adapter.getFilter().filter("");
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_nodes:
                noteViewModel.deleteAllNodes();
                Toast.makeText(this, "All notes deleted:(", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                //handle search action
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
