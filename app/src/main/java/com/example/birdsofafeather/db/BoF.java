package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "bofs")
public class BoF {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private long userId = 0;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "profile_pic")
    private String profileImgURL;

    @ColumnInfo(name = "num_courses_shared")
    private int numCoursesShared;

    @ColumnInfo(name = "small_courses_score")
    private double smallCoursesScore;

    @ColumnInfo(name = "recent_courses_score")
    private double recentCoursesScore;

    @ColumnInfo(name = "favorite")
    private int isFavorite; // 0 for non-favorites, 1 otherwise

    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "has_waved")
    private int hasWaved; // 1 for waved to user, 0 otherwise

    @ColumnInfo(name = "waved_to")
    private int wavedTo; // 1 for user waved to BoF, 0 otherwise

    public BoF(String name, String profileImgURL) {
        this.name = name;
        this.profileImgURL = profileImgURL;
        this.isFavorite = 0;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImgURL() {
        return profileImgURL;
    }

    public void setProfileImgURL(String profileImgURL) {
        this.profileImgURL = profileImgURL;
    }

    public double getSmallCoursesScore() {
        return smallCoursesScore;
    }

    public void setSmallCoursesScore(double smallCoursesScore) {
        this.smallCoursesScore = smallCoursesScore;
    }

    public double getRecentCoursesScore() {
        return recentCoursesScore;
    }

    public void setRecentCoursesScore(double recentCoursesScore) {
        this.recentCoursesScore = recentCoursesScore;
    }

    public int getNumCoursesShared() {
        return numCoursesShared;
    }

    public void setNumCoursesShared(int numCoursesShared) {
        this.numCoursesShared = numCoursesShared;
    }


    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getHasWaved() {
        return hasWaved;
    }

    public void setHasWaved(int hasWaved) {
        this.hasWaved = hasWaved;
    }

    public int getWavedTo() {
        return wavedTo;
    }

    public void setWavedTo(int wavedTo) {
        this.wavedTo = wavedTo;
    }

    @Override
    public String toString() {
        return "BoF{" +
                "user_id=" + userId +
                ", name='" + name + '\'' +
                ", profileImgURL='" + profileImgURL + '\'' +
                '}';
    }
}