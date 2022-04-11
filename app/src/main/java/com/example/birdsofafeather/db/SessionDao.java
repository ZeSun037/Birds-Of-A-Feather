package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SessionDao {
    @Query("SELECT * FROM sessions")
    List<Session> getAll();

    @Query("SELECT * FROM sessions WHERE session_name=:sessionName")
    Session getSessionByName(String sessionName);

    @Query("SELECT concat_ids FROM sessions WHERE session_name=:sessionName")
    String getAllIdsInSession(String sessionName);

    @Query("SELECT COUNT(*) FROM sessions")
    int count();

    @Insert
    void addSession(Session session);

    @Update
    void updateSession(Session session);

    @Delete
    void deleteSession(Session session);
}
