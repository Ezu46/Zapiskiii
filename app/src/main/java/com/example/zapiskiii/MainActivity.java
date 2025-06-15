package com.example.zapiskiii;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zapiskiii.ui.adapter.DiaryEntryAdapter;
import com.example.zapiskiii.ui.viewmodel.DiaryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DiaryViewModel diaryViewModel;
    private DiaryEntryAdapter adapter;
    private int currentSort = 0; // 0 - date desc, 1 - date asc, 2 - title asc, 3 - title desc
    private static final String[] SORT_OPTIONS = {
            "По дате (новые)", "По дате (старые)", "По заголовку (А-Я)", "По заголовку (Я-А)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new DiaryEntryAdapter();
        adapter.setFontSizeFromPrefs(this);
        recyclerView.setAdapter(adapter);

        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
        observeSortedEntries();

        FloatingActionButton fab = findViewById(R.id.fab_add_entry);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditEntryActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemClickListener(entry -> {
            Intent intent = new Intent(MainActivity.this, AddEditEntryActivity.class);
            intent.putExtra(AddEditEntryActivity.EXTRA_ID, entry.getId());
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                diaryViewModel.delete(adapter.getEntryAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                diaryViewModel.searchEntries(newText).observe(MainActivity.this, entries -> {
                    adapter.submitList(entries);
                });
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Сортировка")
                .setSingleChoiceItems(SORT_OPTIONS, currentSort, (dialog, which) -> {
                    currentSort = which;
                    observeSortedEntries();
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void observeSortedEntries() {
        if (currentSort == 0) {
            diaryViewModel.getAllEntriesByDateDesc().observe(this, entries -> adapter.submitList(entries));
        } else if (currentSort == 1) {
            diaryViewModel.getAllEntriesByDateAsc().observe(this, entries -> adapter.submitList(entries));
        } else if (currentSort == 2) {
            diaryViewModel.getAllEntriesByTitleAsc().observe(this, entries -> adapter.submitList(entries));
        } else if (currentSort == 3) {
            diaryViewModel.getAllEntriesByTitleDesc().observe(this, entries -> adapter.submitList(entries));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setFontSizeFromPrefs(this);
        }
    }
}