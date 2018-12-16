package com.example.asus.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.noteapp.Room.Note;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class DisplayNote extends AppCompatActivity {
    public static final String EXTRA_TITLE = "com.example.asus.noteapp.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.asus.noteapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.asus.noteapp.EXTRA_PRIORITY";
    public static final String EXTRA_ID = "com.example.asus.noteapp.EXTRA_ID";
    public static final int EDIT_NOTE_REQUEST_CODE = 1;

    private NoteViewModel noteViewModel;
    private TextView text_title;
    private TextView text_description;
    private String title, description;
    private int id, priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        getDataFromMainActivity();  //Get the information from MainActivity to display them in screen
        createViewModel();

        text_title = findViewById(R.id.display_title);
        text_title.setText(title);
        text_description = findViewById(R.id.display_description);
        text_description.setText(description);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Display Note");

    }

    private void getDataFromMainActivity() {
        //Get the information from MainActivity to display them in screen
        title = getIntent().getStringExtra(MainActivity.EXTRA_TITLE);
        description = getIntent().getStringExtra(MainActivity.EXTRA_DESCRIPTION);
        priority = getIntent().getIntExtra(MainActivity.EXTRA_PRIORITY, 1);
        id = getIntent().getIntExtra(MainActivity.EXTRA_ID, -1);
    }

    private void createViewModel() {
        //ViewModel
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //TODO:
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.display_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_note:
                //TODO: EDIT MODE ADD NOTE ACTIVITY INTENT
                //Pass the data for editing
                Intent intent = new Intent(DisplayNote.this, AddEditNoteActivity.class);
                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_DESCRIPTION, description);
                intent.putExtra(EXTRA_PRIORITY, priority);
                startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
            id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Note can not be updated:(", Toast.LENGTH_SHORT).show();
                return;
            }
            text_title.setText(title);
            text_description.setText(description);

            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved:(", Toast.LENGTH_SHORT).show();
        }
    }
}
