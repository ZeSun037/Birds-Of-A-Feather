package com.example.birdsofafeather.sorting;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;

import java.util.LinkedList;
import java.util.List;

/**
 * Moves all BoFs who have waved to the top of the list, while
 * maintaining their relative ordering using the sorting strategy that
 * the user has currently selected
 */
public class PrioritizeWaves implements WavingSortStrategy {
    protected AppDatabase db;

    public PrioritizeWaves(AppDatabase db) {
        this.db = db;
    }

    @Override
    public void sort(List<BoF> bofs) {
        List<BoF> wavedList = new LinkedList<>();

        // Update local BoF list to incorporate wave status from database, and
        // add all users who have waved to wavedList
        for (int i = 0; i < bofs.size(); i++) {
            BoF bof = bofs.get(i);
            bof.setHasWaved(db.boFDao().get(bof.getUserId()).getHasWaved());

            if (bof.getHasWaved() == 1) {
                wavedList.add(bof);
                bofs.remove(i);
                i--;
            }
        }

        // Add waved BoFs to the top of the list, maintaining their order
        // in the original list
        for (int j = wavedList.size() - 1; j >= 0; j--) {
            bofs.add(0, wavedList.get(j));
        }
    }
}
