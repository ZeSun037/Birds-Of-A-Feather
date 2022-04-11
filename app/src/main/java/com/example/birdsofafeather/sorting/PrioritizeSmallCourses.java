package com.example.birdsofafeather.sorting;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrioritizeSmallCourses implements SortingStrategy {
    private AppDatabase db;
    private Map<String, Double> sizeWeights;

    public PrioritizeSmallCourses(AppDatabase db) {
        this.db = db;
        this.initSizeWeights();
    }

    private void initSizeWeights() {
        this.sizeWeights = new HashMap<>();
        sizeWeights.put("Tiny", 1.0);
        sizeWeights.put("Small", 0.33);
        sizeWeights.put("Medium", 0.18);
        sizeWeights.put("Large", 0.10);
        sizeWeights.put("Huge", 0.06);
        sizeWeights.put("Gigantic", 0.03);
    }

    public void sort(List<BoF> bofs) {
        this.resetSmallCoursesScore();
        this.updateSmallCoursesScore();

        // Use bofs parameter to get IDs of the bofs we want to update
        // Updates list of bofs to have accurate small course scores
        for (BoF bof : bofs) {
            bof.setSmallCoursesScore(db.boFDao().get(bof.getUserId()).getSmallCoursesScore());
        }

        this.removeBoFsWithoutMatches(bofs);

        bofs.sort(new Comparator<BoF>() {
            @Override
            public int compare(BoF bof1, BoF bof2) {
                return Double.valueOf(bof2.getSmallCoursesScore()).compareTo(bof1.getSmallCoursesScore());
            }
        });
    }

    private void resetSmallCoursesScore() {
        List<BoF> allBoFs = db.boFDao().getAll();

        for (BoF boF : allBoFs) {
            boF.setSmallCoursesScore(0);
            db.boFDao().updateBoF(boF);
        }
    }

    private void updateSmallCoursesScore() {
        List<Course> allCoursesUser = db.courseDao().getAllCourses(1);

        for (Course course : allCoursesUser) {
            double courseWeight = this.sizeWeights.get(course.getSize());
            List<Course> matches = db.courseDao().getMatchingCourses(1,
                    course.getQuarter(), course.getYear(), course.getDepartment(),
                    course.getClassNumber());

            for (Course match : matches) {
                // This gets the user associated with the matched course
                BoF matchedUser = db.boFDao().get(match.getPersonId());
                matchedUser.setSmallCoursesScore(matchedUser.getSmallCoursesScore() + courseWeight);
                db.boFDao().updateBoF(matchedUser);
            }
        }

    }

    private void removeBoFsWithoutMatches(List<BoF> bofs) {
        for (int i = 0; i < bofs.size(); i++) {
            if (bofs.get(i).getSmallCoursesScore() == 0) {
                bofs.remove(i);
                i--;
            }
        }
    }
}
