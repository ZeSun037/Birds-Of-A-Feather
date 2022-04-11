package com.example.birdsofafeather.sorting;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PrioritizeRecentCourses implements SortingStrategy {
    protected AppDatabase db;

    public PrioritizeRecentCourses(AppDatabase db) {
        this.db = db;
    }

    public void sort(List<BoF> bofs) {
        this.resetRecentCourses();
        this.updateRecentCoursesCount();

        // Use bof parameter to get IDs of the bofs we want to update
        // Updates list of bofs to have accurate recent score - pulled from database
        for (BoF bof : bofs) {
            bof.setRecentCoursesScore(db.boFDao().get(bof.getUserId()).getRecentCoursesScore());
        }

        this.removeBoFsWithoutMatches(bofs);

        // sorts based off descending recent score
        bofs.sort(new Comparator<BoF>() {
            @Override
            public int compare(BoF boF, BoF t1) {

                return Double.valueOf(t1.getRecentCoursesScore()).compareTo(boF.getRecentCoursesScore());
            }
        });
    }

    public void resetRecentCourses() {
        List<BoF> allBoFs = db.boFDao().getAll();

        for (BoF boF : allBoFs) {
            boF.setRecentCoursesScore(0);
            db.boFDao().updateBoF(boF);
        }
    }

    public void updateRecentCoursesCount() {
        // a list to know the correct ordering of the quarters
        List<String> quartersInOrder = new ArrayList<>();
        quartersInOrder.add("WI");
        quartersInOrder.add("SP");
        quartersInOrder.add("SSS"); // represents all summer sessions
        quartersInOrder.add("FA");


        // given above list and current quarter, a map to calculate weights associated with quarter
        String currentQuarter = "FA";
        String currentYear = "2021";
        int currentQuarterPosition = quartersInOrder.indexOf(currentQuarter);
        HashMap<String, Integer> weights = new HashMap<>();

        int weight = 5;
        while(weight > 1) {
            weights.put(currentQuarter + " " + currentYear, weight);

            // update
            if(currentQuarterPosition == 0) {
                currentQuarterPosition = quartersInOrder.size() - 1;
                currentYear = String.valueOf(Integer.parseInt(currentYear) - 1);
            }
            else currentQuarterPosition--;

            currentQuarter = quartersInOrder.get(currentQuarterPosition);
            weight--;
        }

        // calculate score for each BoF
        List<Course> userCourses = db.courseDao().getAllCourses(1);

        for(Course course: userCourses) {
            // get all the courses / bofs that match
            List<Course> matchingCourses = db.courseDao().getMatchingCourses(1, course.getQuarter(),
                    course.getYear(), course.getDepartment(), course.getClassNumber());

            // update their recent score
            for(Course course2: matchingCourses) {
                BoF match = db.boFDao().get(course2.getPersonId());
                double recentScore = match.getRecentCoursesScore();
                String quarter;

                // considers SS1 and SS2 with SSS
                if(course2.getQuarter() == "SS1" || course2.getQuarter() == "SS2") quarter = "SSS";
                else quarter = course2.getQuarter();

                // adds weight of given course and year
                String quarterAndYear = quarter + " " + course2.getYear();

                if(weights.containsKey(quarterAndYear)) {
                    recentScore += weights.get(quarterAndYear);
                } else {
                    if (!quarterAndYear.equals("WI 2022")) {
                        recentScore += weight; // weight is 1 here
                    } else {
                        recentScore += 0.1;
                    }
                }

                // update bof and db with new score
                match.setRecentCoursesScore(recentScore);
                db.boFDao().updateBoF(match);
            }
        }
    }

    private void removeBoFsWithoutMatches(List<BoF> bofs) {
        for (int i = 0; i < bofs.size(); i++) {
            if (bofs.get(i).getRecentCoursesScore() == 0) {
                bofs.remove(i);
                i--;
            }
        }
    }
}