package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CourseDao {
    @Transaction
    @Query("SELECT * FROM courses WHERE person_id=:person_id")
    List<Course> getAllCourses(long person_id);

    @Query("SELECT * FROM courses WHERE person_id=:person_id AND year=:year AND quarter=:quarter")
    List<Course> getAllCoursesInQuarter(long person_id, String quarter, int year);

    @Query("SELECT * FROM courses")
    List<Course> getAll();

    @Query("SELECT * FROM courses WHERE cid=:course_id")
    Course getCourse(long course_id);

    @Query("SELECT * FROM courses WHERE person_id!=:user_id AND quarter=:quarter " +
            "AND year=:year AND department=:department AND class_number=:class_number")
    List<Course> getMatchingCourses(long user_id, String quarter,
                                    int year, String department, String class_number);

    @Query("SELECT * FROM courses WHERE person_id=:user_id AND quarter=:quarter " +
            "AND year=:year AND department=:department AND class_number=:class_number " +
            "AND size=:size")
    Course getMatchedCourse(long user_id, String quarter, int year, String department,
                            String class_number, String size);

    @Query("SELECT COUNT(*) FROM courses")
    int count();

//    @Query("SELECT * FROM courses WHERE person_id=:person_id AND quarter=:quarter AND year=:year")
//    List<Course> getThisQuarterCourses(long person_id, String quarter, int year);

    // Include link for outside reference
    @Query("DELETE FROM courses")
    void clearCourses();

    @Insert
    long addCourse(Course course);

    @Delete
    void deleteCourse(Course course);
}
