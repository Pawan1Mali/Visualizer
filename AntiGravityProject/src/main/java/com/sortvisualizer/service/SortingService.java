package com.sortvisualizer.service;

import com.sortvisualizer.model.SortStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SortingService {

    private int comparisons;
    private int swaps;
    private List<SortStep> steps;
    private List<Integer> sortedIndices;

    public List<SortStep> sort(String algorithm, int[] array) {
        comparisons = 0;
        swaps = 0;
        steps = new ArrayList<>();
        sortedIndices = new ArrayList<>();

        int[] arr = array.clone();

        switch (algorithm.toLowerCase()) {
            case "bubble":
                bubbleSort(arr);
                break;
            case "selection":
                selectionSort(arr);
                break;
            case "insertion":
                insertionSort(arr);
                break;
            case "merge":
                mergeSort(arr, 0, arr.length - 1);
                break;
            case "quick":
                quickSort(arr, 0, arr.length - 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        // Final step: mark all as sorted
        sortedIndices.clear();
        for (int i = 0; i < arr.length; i++) {
            sortedIndices.add(i);
        }
        steps.add(new SortStep(arr, -1, -1, "DONE", comparisons, swaps, new ArrayList<>(sortedIndices)));

        return steps;
    }

    // ==================== BUBBLE SORT ====================
    private void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Compare
                comparisons++;
                steps.add(new SortStep(arr, j, j + 1, "COMPARE", comparisons, swaps, new ArrayList<>(sortedIndices)));

                if (arr[j] > arr[j + 1]) {
                    // Swap
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    swapped = true;
                    steps.add(new SortStep(arr, j, j + 1, "SWAP", comparisons, swaps, new ArrayList<>(sortedIndices)));
                }
            }
            // After each pass, the last unsorted element is now sorted
            sortedIndices.add(n - i - 1);
            if (!swapped) {
                // If no swaps happened, remaining elements are sorted
                for (int k = 0; k < n - i - 1; k++) {
                    if (!sortedIndices.contains(k)) {
                        sortedIndices.add(k);
                    }
                }
                break;
            }
        }
        if (!sortedIndices.contains(0)) {
            sortedIndices.add(0);
        }
    }

    // ==================== SELECTION SORT ====================
    private void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                steps.add(new SortStep(arr, minIdx, j, "COMPARE", comparisons, swaps, new ArrayList<>(sortedIndices)));

                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
                swaps++;
                steps.add(new SortStep(arr, i, minIdx, "SWAP", comparisons, swaps, new ArrayList<>(sortedIndices)));
            }
            sortedIndices.add(i);
        }
        sortedIndices.add(arr.length - 1);
    }

    // ==================== INSERTION SORT ====================
    private void insertionSort(int[] arr) {
        int n = arr.length;
        sortedIndices.add(0);
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                steps.add(new SortStep(arr, j, j + 1, "COMPARE", comparisons, swaps, new ArrayList<>(sortedIndices)));

                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    swaps++;
                    steps.add(new SortStep(arr, j, j + 1, "SWAP", comparisons, swaps, new ArrayList<>(sortedIndices)));
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            sortedIndices.add(i);
        }
    }

    // ==================== MERGE SORT ====================
    private void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        System.arraycopy(arr, left, leftArr, 0, n1);
        System.arraycopy(arr, mid + 1, rightArr, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            comparisons++;
            steps.add(new SortStep(arr, left + i, mid + 1 + j, "COMPARE", comparisons, swaps, new ArrayList<>(sortedIndices)));

            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i];
                i++;
            } else {
                arr[k] = rightArr[j];
                j++;
            }
            swaps++;
            steps.add(new SortStep(arr, k, -1, "OVERWRITE", comparisons, swaps, new ArrayList<>(sortedIndices)));
            k++;
        }

        while (i < n1) {
            arr[k] = leftArr[i];
            swaps++;
            steps.add(new SortStep(arr, k, -1, "OVERWRITE", comparisons, swaps, new ArrayList<>(sortedIndices)));
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = rightArr[j];
            swaps++;
            steps.add(new SortStep(arr, k, -1, "OVERWRITE", comparisons, swaps, new ArrayList<>(sortedIndices)));
            j++;
            k++;
        }
    }

    // ==================== QUICK SORT ====================
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sortedIndices.add(pivotIndex);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        } else if (low == high) {
            sortedIndices.add(low);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisons++;
            steps.add(new SortStep(arr, j, high, "COMPARE", comparisons, swaps, new ArrayList<>(sortedIndices)));

            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    swaps++;
                    steps.add(new SortStep(arr, i, j, "SWAP", comparisons, swaps, new ArrayList<>(sortedIndices)));
                }
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        swaps++;
        steps.add(new SortStep(arr, i + 1, high, "SWAP", comparisons, swaps, new ArrayList<>(sortedIndices)));

        return i + 1;
    }
}
