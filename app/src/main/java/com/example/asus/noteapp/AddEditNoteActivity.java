package com.example.asus.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity {
    //Keys for sending data
    public static final String EXTRA_TITLE = "com.example.asus.noteapp.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.asus.noteapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.asus.noteapp.EXTRA_PRIORITY";
    public static final String EXTRA_ID = "com.example.asus.noteapp.EXTRA_ID";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        //Set NumberPicker range for priority numbers
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //hide keyboard when activity started

        //Check this activity started for editing existing note or adding new note
        Intent intent = getIntent();
        if (intent.hasExtra(DisplayNote.EXTRA_ID)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(DisplayNote.EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(DisplayNote.EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(DisplayNote.EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }

    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        //Prevent empty entries for title and description
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please enter title and description", Toast.LENGTH_SHORT).show();
            return;
        }


        //Send back data to activity that started this one
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        //Only send the id back to caller if its exists
        int id = getIntent().getIntExtra(DisplayNote.EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(DisplayNote.EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    //For menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
