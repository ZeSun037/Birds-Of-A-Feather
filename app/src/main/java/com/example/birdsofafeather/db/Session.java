package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "sessions")
public class Session {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "session_name")
    private String name;

    @ColumnInfo(name = "concat_ids")
    private String concatIds;

    public Session(@NonNull String name, String concatIds) {
        this.name = name;
        this.concatIds = concatIds;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getConcatIds() {
        return concatIds;
    }

    public void setConcatIds(String concatIds) {
        this.concatIds = concatIds;
    }
}
