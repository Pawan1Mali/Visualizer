package com.sortvisualizer.controller;

import com.sortvisualizer.model.SortRequest;
import com.sortvisualizer.model.SortStep;
import com.sortvisualizer.service.SortingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SortingController {

    private final SortingService sortingService;

    public SortingController(SortingService sortingService) {
        this.sortingService = sortingService;
    }

    @PostMapping("/sort")
    public List<SortStep> sort(@RequestBody SortRequest request) {
        return sortingService.sort(request.getAlgorithm(), request.getArray());
    }

    @GetMapping("/algorithms")
    public List<Map<String, String>> getAlgorithms() {
        return List.of(
            Map.of("id", "bubble", "name", "Bubble Sort",
                    "timeAvg", "O(n²)", "timeWorst", "O(n²)", "timeBest", "O(n)",
                    "space", "O(1)", "stable", "Yes",
                    "description", "Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. Simple but inefficient for large datasets."),
            Map.of("id", "selection", "name", "Selection Sort",
                    "timeAvg", "O(n²)", "timeWorst", "O(n²)", "timeBest", "O(n²)",
                    "space", "O(1)", "stable", "No",
                    "description", "Divides the array into sorted and unsorted regions. Repeatedly finds the minimum element from the unsorted part and moves it to the sorted part."),
            Map.of("id", "insertion", "name", "Insertion Sort",
                    "timeAvg", "O(n²)", "timeWorst", "O(n²)", "timeBest", "O(n)",
                    "space", "O(1)", "stable", "Yes",
                    "description", "Builds the sorted array one item at a time by repeatedly picking the next element and inserting it into its correct position."),
            Map.of("id", "merge", "name", "Merge Sort",
                    "timeAvg", "O(n log n)", "timeWorst", "O(n log n)", "timeBest", "O(n log n)",
                    "space", "O(n)", "stable", "Yes",
                    "description", "Divides the array in half, recursively sorts each half, and then merges the sorted halves. Efficient and consistent performance."),
            Map.of("id", "quick", "name", "Quick Sort",
                    "timeAvg", "O(n log n)", "timeWorst", "O(n²)", "timeBest", "O(n log n)",
                    "space", "O(log n)", "stable", "No",
                    "description", "Picks a pivot element and partitions the array around it. Elements smaller than the pivot go left, larger go right. Then recursively sorts each partition.")
        );
    }
}
