package com.example.birdsofafeather.sorting;

import com.example.birdsofafeather.sorting.SortingStrategy;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrioritizeMatchNumbers implements SortingStrategy {

    protected AppDatabase db;

    public PrioritizeMatchNumbers(AppDatabase db) {
        this.db = db;
    }

    /**
     * Sorts based off number of matches
     * @param bofs - list to be sorted
     */
    public void sort(List<BoF> bofs) {
        this.resetMatchingCourses();
        this.updateMatchingCoursesCount();

        // Use bof parameter to get IDs of the bofs we want to update
        // Updates list of bofs to have accurate matches - pulled from database
        for (BoF bof : bofs) {
            bof.setNumCoursesShared(db.boFDao().get(bof.getUserId()).getNumCoursesShared());
        }

        this.removeBoFsWithoutMatches(bofs);

        // sorts based off descending courses shared
        bofs.sort(new Comparator<BoF>() {
            @Override
            public int compare(BoF boF, BoF t1) {

                return Integer.valueOf(t1.getNumCoursesShared()).compareTo(boF.getNumCoursesShared());
            }
        });

    }

    private void resetMatchingCourses() {
        List<BoF> allBoFs = db.boFDao().getAll();

        for (BoF boF : allBoFs) {
            boF.setNumCoursesShared(0);
            db.boFDao().updateBoF(boF);
        }
    }

    private void updateMatchingCoursesCount() {
        List<Course> allCourses = db.courseDao().getAllCourses(1);

        for(Course course : allCourses) {
            List<Course> matches = db.courseDao().getMatchingCourses(1,
                    course.getQuarter(), course.getYear(), course.getDepartment(),
                    course.getClassNumber());

            for(Course course2 : matches) {
                // This gets the user associated with the matched course
                BoF matched_user = db.boFDao().get(course2.getPersonId());
                matched_user.setNumCoursesShared(matched_user.getNumCoursesShared() + 1);
                db.boFDao().updateBoF(matched_user);
            }
        }

    }

    private void removeBoFsWithoutMatches(List<BoF> bofs) {
        for (int i = 0; i < bofs.size(); i++) {
            if (bofs.get(i).getNumCoursesShared() == 0) {
                bofs.remove(i);
                i--;
            }
        }
    }

}
