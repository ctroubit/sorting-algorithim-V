package se2203.sorting;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.Timer;


public class SortingHubController implements Initializable {

    @FXML
    private ComboBox<String> algoComboBox;

    @FXML
    private Label arraySizeNumberLabel;

    @FXML
    private Slider arraySizeSlider;

    @FXML
    private Button resetButton;

    @FXML
    private Button sortButton;

    @FXML
    private Pane rectangleBox;

    private SortingStrategy sortingStrategy;

    private int[] intArray;

    private ArrayList<Rectangle> rectangles;
    @FXML
    private Label timeLabel;

    private Timer timer;

    private long startTime;

    @FXML
    public void setSortingStrategy() {
        switch(algoComboBox.getValue()){
            case "Merge Sort":
                sortingStrategy = new MergeSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
            case "Selection Sort":
                sortingStrategy = new SelectionSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
            case "Quick Sort":
                sortingStrategy = new QuickSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
            case "Bubble Sort":
                sortingStrategy = new BubbleSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
            case "Heap Sort":
                sortingStrategy = new HeapSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
            case "Radix Sort":
                sortingStrategy = new RadixSort(intArray, this);
                sortingStrategy.sort(intArray);
                startTimer();
                resetButton.setDisable(true);
                break;
        }
    }

    public void updateGraph(int[] data) {
        rectangles.clear();
        rectangleBox.getChildren().clear();

        double width = (rectangleBox.getPrefWidth() / data.length);


        double height = (rectangleBox.getPrefHeight() / data.length);


        for (int i = 0; i < data.length; i++) {
            Rectangle r = new Rectangle();
            r.setWidth(width - 2);
            r.setHeight(data[i] * height - 2);
            r.setFill(Color.RED);
            r.setX((i * width));
            r.setY(rectangleBox.getPrefHeight() - r.getHeight()-1);
            rectangles.add(r);
            rectangleBox.getChildren().add(rectangles.get(i));
        }
    }
    @FXML
    public void resetController(){
        arraySizeNumberLabel.setText(String.format("%d", 64));
        arraySizeSlider.setValue(64);
        timeLabel.setText("Timer: 0");

        intArray = new int[64];
        shuffleArray(intArray);
        updateGraph(intArray);
        timer.stop();

        algoComboBox.getSelectionModel().select("Merge Sort");
    }

    public void startTimer(){
        startTime = System.currentTimeMillis();

        timer.start();

    }

    public void updateTime(){
        long elapsedTime =  System.currentTimeMillis() - startTime;
        timeLabel.setText("Time: " + String.format("%.3f",(float)elapsedTime/1000));
    }

    @FXML
    public void arraySizeController() {
        arraySizeNumberLabel.setText(String.format("%d", (int) arraySizeSlider.getValue()));

        intArray = new int[(int) arraySizeSlider.getValue()];
        shuffleArray(intArray);
        updateGraph(intArray);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rectangles = new ArrayList<>();
        intArray = new int[64];

        shuffleArray(intArray);
        updateGraph(intArray);

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("Merge Sort");
        sortList.add("Selection Sort");
        sortList.add("Quick Sort");
        sortList.add("Bubble Sort");
        sortList.add("Heap Sort");
        sortList.add("Radix Sort");

        timer = new Timer(0, e -> Platform.runLater(() -> updateTime()));
        timeLabel.setText("Time: 0");

        algoComboBox.getItems().setAll(sortList);
        algoComboBox.getSelectionModel().select("Merge Sort");
    }

    public void complete(){
        new Thread(()->{
            int speed;

            if(rectangleBox.getChildren().size()>250){
                speed = 5;
            }else{
                speed = 20;
            }

            for (int i = 0; i < rectangleBox.getChildren().size(); i++) {
                Rectangle r = (Rectangle) rectangleBox.getChildren().get(i);

                try {
                    Thread.sleep(speed);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }
                r.setFill(Color.GREEN);


            }
            resetButton.setDisable(false);
        }).start();

    }

    public void shuffleArray(int[] numbers) {

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        Random rnd = ThreadLocalRandom.current();
        for (int i = numbers.length - 1; i > 0; i--) {
            int in = rnd.nextInt(i + 1);
            int num = numbers[in];
            numbers[in] = numbers[i];
            numbers[i] = num;
        }
    }

    @FXML
    public void showInfo() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(SortingHubController.class.getResource("ViewInfo-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("About Us");
        stage.getIcons().add(new Image("file:src/main/resources/se2203b/assignment1/WesternLogo.png"));
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public class MergeSort implements SortingStrategy {

        private int[] list;
        private SortingHubController controller;

        public MergeSort(int[] l, SortingHubController c) {
            this.list = l;
            this.controller = c;
        }

        public void sort(int[] numbers) {
            new Thread(()->{
                mergeSort(numbers,0,numbers.length-1);
                timer.stop();
                complete();
            }).start();

        }
        public void merge(int[] numbers, int s, int m, int l)
        {
            int s2 = m + 1;

            if (numbers[m] <= numbers[s2]) {return;}

            while (s <= m && s2 <= l) {

                if (numbers[s] <= numbers[s2]) {s++;}
                else {
                    int value = numbers[s2];
                    int index = s2;

                    while (index != s) {
                        numbers[index] = numbers[index - 1];
                        index--;
                    }

                    numbers[s] = value;
                    Platform.runLater(() -> controller.updateGraph(numbers));
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException();
                    }

                    s++;
                    m++;
                    s2++;

                }
            }
        }

        public void mergeSort(int[] numbers, int l, int r)
        {
            if (l < r) {

                int m = l + (r - l) / 2;

                mergeSort(numbers, l, m);
                mergeSort(numbers, m + 1, r);

                merge(numbers, l, m, r);
            }

        }

        @Override
        public void run() {
            sort(list);
        }
    }

    public class SelectionSort implements SortingStrategy {

        private int[] list;
        private SortingHubController controller;

        public SelectionSort(int[] l, SortingHubController c) {
            this.list = l;
            this.controller = c;
        }

        public void selectionSort(int[] numbers){
            for (int i = 0; i < numbers.length - 1; i++) {

                int in = i;

                for (int j = i + 1; j < numbers.length; j++) {
                    if (numbers[j] < numbers[in]) {in = j;}
                }

                int num = numbers[in];
                numbers[in] = numbers[i];
                numbers[i] = num;

                Platform.runLater(() -> controller.updateGraph(numbers));
                try {
                    Thread.sleep(40);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }
            }
        }

        public void sort(int[] numbers) {
            new Thread(() -> {
                selectionSort(numbers);
                timer.stop();
                complete();
            }).start();
        }

        @Override
        public void run() {
            sort(list);
        }
    }
    public class QuickSort implements SortingStrategy {
        private int[] list;
        private SortingHubController controller;

        public QuickSort(int[] l, SortingHubController c) {
            this.list = l;
            this.controller = c;
        }

        static void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        static int partition(int[] arr, int low, int high) {
            int pivot = arr[high];
            int i = (low - 1);

            for (int j = low; j <= high - 1; j++) {
                if (arr[j] < pivot) {
                    i++;
                    swap(arr, i, j);
                }
            }

            swap(arr, i + 1, high);
            return (i + 1);
        }

        private void quickSort(int low, int high) {
            if (low < high) {

                int pi = partition(list, low, high);

                Platform.runLater(() -> controller.updateGraph(list));
                try {
                    Thread.sleep(40);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }

                quickSort(low, pi - 1);
                quickSort(pi + 1, high);

            }
        }

        public void sort(int[]numbers) {

            new Thread(() -> {
                quickSort(0, numbers.length-1);
                timer.stop();
                complete();
            }).start();
        }

        @Override
        public void run() {
            sort(list);
        }
    }

    public class BubbleSort implements SortingStrategy{

        public int[] list;
        public SortingHubController controller;

        public BubbleSort(int[] l, SortingHubController c){
            this.list = l;
            this.controller = c;
        }

        public void bubbleSort(int[] arr, int n){
            int i, j, temp;
            boolean swapped;
            for (i = 0; i < n - 1; i++) {
                swapped = false;
                for (j = 0; j < n - i - 1; j++) {
                    if (arr[j] > arr[j + 1]) {

                        // Swap arr[j] and arr[j+1]
                        temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                        swapped = true;
                    }
                }
                if (swapped) {
                    Platform.runLater(() -> controller.updateGraph(list));
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt(); // handle interruption properly
                        return; // or some other appropriate handling
                    }
                }
                if (!swapped) break; // No swap means array is sorted
            }
        }


        public void sort(int[] numbers){
            new Thread(()->{
                bubbleSort(numbers,numbers.length);
                timer.stop();
                complete();
            }).start();
        }

        @Override
        public void run() {
            sort(list);
        }
    }

    public class HeapSort implements SortingStrategy{
        public int [] list;
        public SortingHubController controller;

        public HeapSort(int[] l, SortingHubController c){
            this.list = l;
            this.controller = c;
        }

        public void heapSort(int[] arr)
        {
            int N = arr.length;

            // Build heap (rearrange array)
            for (int i = N / 2 - 1; i >= 0; i--)
                heapify(arr, N, i);

            // One by one extract an element from heap
            for (int i = N - 1; i > 0; i--) {
                // Move current root to end
                int temp = arr[0];
                arr[0] = arr[i];
                arr[i] = temp;

                // call max heapify on the reduced heap
                heapify(arr, i, 0);
                Platform.runLater(() -> controller.updateGraph(list));
                try {
                    Thread.sleep(40);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }
            }

        }

        // To heapify a subtree rooted with node i which is
        // an index in arr[]. n is size of heap
       public void heapify(int[] arr, int N, int i)
        {
            int largest = i; // Initialize largest as root
            int l = 2 * i + 1; // left = 2*i + 1
            int r = 2 * i + 2; // right = 2*i + 2

            // If left child is larger than root
            if (l < N && arr[l] > arr[largest])
                largest = l;

            // If right child is larger than largest so far
            if (r < N && arr[r] > arr[largest])
                largest = r;

            // If largest is not root
            if (largest != i) {
                int swap = arr[i];
                arr[i] = arr[largest];
                arr[largest] = swap;

                // Recursively heapify the affected sub-tree
                heapify(arr, N, largest);
            }
        }
        public void sort(int[] numbers){
            new Thread(()->{
                heapSort(numbers);
                timer.stop();
                complete();
            }).start();
        }

        @Override
        public void run() {
            sort(list);
        }
    }
    public class RadixSort implements SortingStrategy{
        public int [] list;
        public SortingHubController controller;

        public RadixSort(int[] l, SortingHubController c){
            this.list = l;
            this.controller = c;
        }

        static int getMax(int arr[], int n)
        {
            int mx = arr[0];
            for (int i = 1; i < n; i++)
                if (arr[i] > mx)
                    mx = arr[i];
            return mx;
        }

        // A function to do counting sort of arr[] according to
        // the digit represented by exp.
        public void countSort(int arr[], int n, int exp)
        {

            int output[] = new int[n]; // output array
            int i;
            int count[] = new int[10];
            Arrays.fill(count, 0);

            // Store count of occurrences in count[]
            for (i = 0; i < n; i++)
                count[(arr[i] / exp) % 10]++;

            // Change count[i] so that count[i] now contains
            // actual position of this digit in output[]
            for (i = 1; i < 10; i++)
                count[i] += count[i - 1];

            // Build the output array
            for (i = n - 1; i >= 0; i--) {
                output[count[(arr[i] / exp) % 10] - 1] = arr[i];
                count[(arr[i] / exp) % 10]--;
            }

            // Copy the output array to arr[], so that arr[] now
            // contains sorted numbers according to current
            // digit
            for (i = 0; i < n; i++){
                arr[i] = output[i];
                Platform.runLater(() -> controller.updateGraph(list));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }
            }



        }

        // The main function to that sorts arr[] of
        // size n using Radix Sort
        public void radixsort(int arr[], int n)
        {
            // Find the maximum number to know number of digits
            int m = getMax(arr, n);

            // Do counting sort for every digit. Note that
            // instead of passing digit number, exp is passed.
            // exp is 10^i where i is current digit number
            for (int exp = 1; m / exp > 0; exp *= 10){

                countSort(arr, n, exp);

            }


        }

        public void sort(int[] numbers){
            new Thread(()->{
                radixsort(numbers,numbers.length);
                timer.stop();
                complete();
            }).start();
        }

        @Override
        public void run() {
            sort(list);
        }
    }
}
