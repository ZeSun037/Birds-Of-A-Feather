package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BoFDao {
    @Query("SELECT * FROM bofs")
    List<BoF> getAll();

    @Query("SELECT * FROM bofs WHERE user_id!=:userId")
    List<BoF> getAllExceptUser(int userId);

    @Query("SELECT * FROM bofs WHERE favorite=1")
    List<BoF> getFavorites();

    @Query("SELECT * FROM bofs WHERE has_waved=1")
    List<BoF> getWaved();

    @Query("SELECT * FROM bofs WHERE user_id=:userId")
    BoF get(long userId);

    @Query("SELECT * FROM bofs WHERE uuid=:uniqueID")
    BoF get(String uniqueID);

    @Query("SELECT * FROM bofs WHERE num_courses_shared!=0 ORDER BY num_courses_shared DESC")
    List<BoF> getBoFsWithMatchingCoursesSorted();

    @Query("SELECT COUNT(*) FROM bofs")
    int count();

    @Query("DELETE FROM bofs")
    void clearBofs();

    @Insert
    long addBoF(BoF boF);

    @Delete
    void deleteBoF(BoF boF);

    @Update
    void updateBoF(BoF boF);

}