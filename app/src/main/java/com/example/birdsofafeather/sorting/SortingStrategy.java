package com.example.birdsofafeather.sorting;

import com.example.birdsofafeather.db.BoF;

import java.util.List;

public interface SortingStrategy {

    public void sort(List<BoF> bof);
}
