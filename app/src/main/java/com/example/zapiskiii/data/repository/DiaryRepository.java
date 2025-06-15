package com.example.zapiskiii.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.zapiskiii.data.AppDatabase;
import com.example.zapiskiii.data.dao.DiaryEntryDao;
import com.example.zapiskiii.data.model.DiaryEntry;

import java.util.List;

public class DiaryRepository {

    private DiaryEntryDao diaryEntryDao;
    public DiaryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        diaryEntryDao = db.diaryEntryDao();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByDateDesc() {
        return diaryEntryDao.getAllEntriesByDateDesc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByDateAsc() {
        return diaryEntryDao.getAllEntriesByDateAsc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByTitleAsc() {
        return diaryEntryDao.getAllEntriesByTitleAsc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByTitleDesc() {
        return diaryEntryDao.getAllEntriesByTitleDesc();
    }

    public void insert(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            diaryEntryDao.insert(diaryEntry);
        });
    }

    public void update(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            diaryEntryDao.update(diaryEntry);
        });
    }

    public void delete(DiaryEntry diaryEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            diaryEntryDao.delete(diaryEntry);
        });
    }

    public LiveData<DiaryEntry> getEntryById(int id) {
        return diaryEntryDao.getEntryById(id);
    }

    public LiveData<List<DiaryEntry>> searchEntries(String query) {
        return diaryEntryDao.searchEntries("%" + query + "%");
    }
}
