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
            setTitle(getString(R.string.edit_entry));
            currentEntryId = intent.getIntExtra(EXTRA_ID, -1);
            diaryViewModel.getEntryById(currentEntryId).observe(this, entry -> {
                if (entry != null) {
                    editTextTitle.setText(entry.getTitle());
                    editTextContent.setText(entry.getContent());
                    editTextTags.setText(entry.getTags());
                    if (entry.getImagePath() != null) {
                        try {
                            java.io.File imgFile = new java.io.File(entry.getImagePath());
                            if (imgFile.exists()) {
                                imageUri = Uri.fromFile(imgFile);
                                imageViewPhoto.setImageURI(imageUri);
                                imageViewPhoto.setVisibility(View.VISIBLE);
                            } else {
                                imageViewPhoto.setVisibility(View.GONE);
                                Toast.makeText(this, "Изображение не найдено", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            imageViewPhoto.setVisibility(View.GONE);
                            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        imageViewPhoto.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            setTitle(getString(R.string.add_entry));
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
            Uri pickedUri = data.getData();
            try {
                String localPath = copyImageToInternalStorage(this, pickedUri);
                imageUri = Uri.fromFile(new java.io.File(localPath));
                imageViewPhoto.setImageURI(imageUri);
                imageViewPhoto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Копирование изображения во внутреннее хранилище
    public static String copyImageToInternalStorage(android.content.Context context, Uri sourceUri) throws java.io.IOException {
        java.io.InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
        java.io.File dir = new java.io.File(context.getFilesDir(), "diary_images");
        if (!dir.exists()) dir.mkdirs();
        String fileName = java.util.UUID.randomUUID().toString() + ".jpg";
        java.io.File destFile = new java.io.File(dir, fileName);
        java.io.OutputStream outputStream = new java.io.FileOutputStream(destFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();
        return destFile.getAbsolutePath();
    }

    private void saveEntry() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
        String tags = editTextTags.getText().toString();

        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_title_content), Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryEntry entry = new DiaryEntry();
        entry.setTitle(title);
        entry.setContent(content);
        entry.setTags(tags);
        entry.setDate(System.currentTimeMillis());
        if (imageUri != null) {
            // Сохраняем абсолютный путь к локальному файлу
            if ("file".equals(imageUri.getScheme())) {
                entry.setImagePath(new java.io.File(imageUri.getPath()).getAbsolutePath());
            } else {
                entry.setImagePath(imageUri.toString());
            }
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
