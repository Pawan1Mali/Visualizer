package com.sortvisualizer.model;

import java.util.List;

public class SortStep {

    private int[] array;
    private int idx1;
    private int idx2;
    private String type; // COMPARE, SWAP, OVERWRITE, DONE
    private int comparisons;
    private int swaps;
    private List<Integer> sortedIndices;

    public SortStep() {
    }

    public SortStep(int[] array, int idx1, int idx2, String type, int comparisons, int swaps,
            List<Integer> sortedIndices) {
        this.array = array.clone();
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.type = type;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.sortedIndices = sortedIndices;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public int getIdx1() {
        return idx1;
    }

    public void setIdx1(int idx1) {
        this.idx1 = idx1;
    }

    public int getIdx2() {
        return idx2;
    }

    public void setIdx2(int idx2) {
        this.idx2 = idx2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getComparisons() {
        return comparisons;
    }

    public void setComparisons(int comparisons) {
        this.comparisons = comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public void setSwaps(int swaps) {
        this.swaps = swaps;
    }

    public List<Integer> getSortedIndices() {
        return sortedIndices;
    }

    public void setSortedIndices(List<Integer> sortedIndices) {
        this.sortedIndices = sortedIndices;
    }
}
