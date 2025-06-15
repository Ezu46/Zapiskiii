package com.example.zapiskiii;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.zapiskiii.data.model.DiaryEntry;
import com.example.zapiskiii.ui.viewmodel.DiaryViewModel;

public class AddEditEntryActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.zapiskiii.EXTRA_ID";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextTitle;
    private EditText editTextContent;
    private EditText editTextTags;
    private ImageView imageViewPhoto;
    private Button buttonAttachPhoto;

    private DiaryViewModel diaryViewModel;
    private Uri imageUri;
    private int currentEntryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        editTextTags = findViewById(R.id.edit_text_tags);
        imageViewPhoto = findViewById(R.id.image_view_photo);
        buttonAttachPhoto = findViewById(R.id.button_attach_photo);

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Entry");
            currentEntryId = intent.getIntExtra(EXTRA_ID, -1);
            diaryViewModel.getEntryById(currentEntryId).observe(this, entry -> {
                if (entry != null) {
                    editTextTitle.setText(entry.getTitle());
                    editTextContent.setText(entry.getContent());
                    editTextTags.setText(entry.getTags());
                    if (entry.getImagePath() != null) {
                        imageUri = Uri.parse(entry.getImagePath());
                        imageViewPhoto.setImageURI(imageUri);
                        imageViewPhoto.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            setTitle("Add Entry");
        }

        buttonAttachPhoto.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewPhoto.setImageURI(imageUri);
            imageViewPhoto.setVisibility(View.VISIBLE);
        }
    }

    private void saveEntry() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
        String tags = editTextTags.getText().toString();

        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryEntry entry = new DiaryEntry();
        entry.setTitle(title);
        entry.setContent(content);
        entry.setTags(tags);
        entry.setDate(System.currentTimeMillis());
        if (imageUri != null) {
            entry.setImagePath(imageUri.toString());
        }

        if (currentEntryId != -1) {
            entry.setId(currentEntryId);
            diaryViewModel.update(entry);
        } else {
            diaryViewModel.insert(entry);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_entry_menu, menu);
        if (currentEntryId == -1) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveEntry();
            return true;
        } else if (id == R.id.action_delete) {
            deleteEntry();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEntry() {
        if (currentEntryId != -1) {
            diaryViewModel.getEntryById(currentEntryId).observe(this, entry -> {
                if (entry != null) {
                    diaryViewModel.delete(entry);
                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
