package com.example.zapiskiii.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zapiskiii.data.model.DiaryEntry;
import com.example.zapiskiii.data.repository.DiaryRepository;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private DiaryRepository repository;
    public DiaryViewModel(Application application) {
        super(application);
        repository = new DiaryRepository(application);
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByDateDesc() {
        return repository.getAllEntriesByDateDesc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByDateAsc() {
        return repository.getAllEntriesByDateAsc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByTitleAsc() {
        return repository.getAllEntriesByTitleAsc();
    }

    public LiveData<List<DiaryEntry>> getAllEntriesByTitleDesc() {
        return repository.getAllEntriesByTitleDesc();
    }

    public void insert(DiaryEntry diaryEntry) {
        repository.insert(diaryEntry);
    }

    public void update(DiaryEntry diaryEntry) {
        repository.update(diaryEntry);
    }

    public void delete(DiaryEntry diaryEntry) {
        repository.delete(diaryEntry);
    }

    public LiveData<DiaryEntry> getEntryById(int id) {
        return repository.getEntryById(id);
    }

    public LiveData<List<DiaryEntry>> searchEntries(String query) {
        return repository.searchEntries(query);
    }
}
