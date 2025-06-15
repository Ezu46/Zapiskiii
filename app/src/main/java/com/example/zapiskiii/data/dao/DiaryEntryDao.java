package com.example.zapiskiii.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapiskiii.data.model.DiaryEntry;

import java.util.List;

@Dao
public interface DiaryEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiaryEntry diaryEntry);

    @Update
    void update(DiaryEntry diaryEntry);

    @Delete
    void delete(DiaryEntry diaryEntry);

    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    LiveData<List<DiaryEntry>> getAllEntriesByDateDesc();

    @Query("SELECT * FROM diary_entries ORDER BY date ASC")
    LiveData<List<DiaryEntry>> getAllEntriesByDateAsc();

    @Query("SELECT * FROM diary_entries ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<DiaryEntry>> getAllEntriesByTitleAsc();

    @Query("SELECT * FROM diary_entries ORDER BY title COLLATE NOCASE DESC")
    LiveData<List<DiaryEntry>> getAllEntriesByTitleDesc();

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    LiveData<DiaryEntry> getEntryById(int id);

    @Query("SELECT * FROM diary_entries WHERE title LIKE :query OR content LIKE :query OR tags LIKE :query ORDER BY date DESC")
    LiveData<List<DiaryEntry>> searchEntries(String query);
}
