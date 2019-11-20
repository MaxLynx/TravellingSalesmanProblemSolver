package edu.optimalization.model;

import edu.optimalization.utils.FileInput;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {

    public static int MAX_TEMPERATURE = 1000;
    public static int TEMPERATURE_DECREASE_COEFFICIENT = 100;
    public static int MAX_COUNT_FROM_THE_LAST_TEMPERATURE_CHANGE = 15;
    public static int MAX_COUNT_FROM_THE_LAST_SOLUTION_CHANGE = 15;

    private FileInput fileInput;
    private int frequencies [][];

    public Solver(String file){
        fileInput = new FileInput();
        fileInput.readFile(new File(file));
        calculateFrequencies();
    }

    public void solve(){
        fileInput.setX(improve(fileInput.getX()));
    }

    public int[] findStartingSolution(){
        long bestTotalFrequency = Integer.MAX_VALUE;
        int[] startingSolution = null;
        for (int i = 0; i < frequencies[0].length; i++) {
            int[] currentSolution = buildRoad(i);
            long currentFrequency = calculateTotalFrequency(currentSolution);
            if(currentFrequency < bestTotalFrequency){
                bestTotalFrequency = currentFrequency;
                startingSolution = currentSolution;
            }
        }
        fileInput.setX(startingSolution);
        return startingSolution;
    }

    public int[] improve(int[] currentSolution){
        int[] betterSolution = currentSolution;

        double temperature = 1.0 * MAX_TEMPERATURE;

        int i = 0, v, r, w;

        do {
            v = 0;
            r = 0;
            w = 0;
            do {
                int[] newSolution = Arrays.copyOf(currentSolution, currentSolution.length);
                int tmp = newSolution[i];
                newSolution[i] = newSolution[i + 3];
                newSolution[i + 3] = tmp;
                tmp = newSolution[i + 1];
                newSolution[i + 1] = newSolution[i + 2];
                newSolution[i + 2] = tmp;
                w++;
                r++;
                i++;
                System.out.println("New trace has frequency " + calculateTotalFrequency(newSolution));
                if (w == MAX_COUNT_FROM_THE_LAST_TEMPERATURE_CHANGE) {
                    temperature /= 1 + TEMPERATURE_DECREASE_COEFFICIENT * temperature;
                    w = 0;
                }
                if (calculateTotalFrequency(newSolution) <= calculateTotalFrequency(currentSolution)) {
                    currentSolution = Arrays.copyOf(newSolution, currentSolution.length);
                    r = 0;
                    System.out.println("Accepted new trace with frequency " + calculateTotalFrequency(newSolution));
                    if (calculateTotalFrequency(currentSolution) < calculateTotalFrequency(betterSolution)) {
                        betterSolution = Arrays.copyOf(currentSolution, currentSolution.length);
                        v++;
                    }
                } else {
                    double probability = Math.exp(calculateTotalFrequency(currentSolution)
                            - calculateTotalFrequency(newSolution)) / temperature;
                    double randomNumber = Math.random();
                    if (randomNumber <= probability) {
                        currentSolution = Arrays.copyOf(newSolution, currentSolution.length);
                        r = 0;
                        System.out.println("Accepted new trace with frequency " + calculateTotalFrequency(newSolution));

                    }
                }
            } while (r != MAX_COUNT_FROM_THE_LAST_SOLUTION_CHANGE);
            if(v > 0){
                temperature = MAX_TEMPERATURE;
            }
        } while(v > 0);
        return betterSolution;
    }

    public int[] getResult(){
        return fileInput.getX();
    }

    public long getResultTotalFrequency(){
        return calculateTotalFrequency(fileInput.getX());
    }

    public int[][] getFrequencies(){
        return frequencies;
    }

    private long calculateTotalFrequency(int[] nodes){
        long totalFrequency = 0;
        for (int i = 0; i < nodes.length - 1; i++) {
            totalFrequency += frequencies[nodes[i]][nodes[i+1]];
        }
        return totalFrequency;
    }

    private int[] buildRoad(int startingNode){
        int[] result = new int[frequencies.length];
        result[0] = startingNode;
        List<Integer> candidates = new ArrayList<Integer>(frequencies.length - 1);
        for (int j = 0; j < frequencies.length; j++) {
            if(j == startingNode){
                continue;
            }
            candidates.add(j);
        }
        int currentNode = startingNode;
        for (int k = 0; k < frequencies.length - 1; k++) {
            int minFrequency = Integer.MAX_VALUE;
            int bestNeighbour = 0;
            for (int j:
                 candidates) {
                if(frequencies[currentNode][j] < minFrequency){
                    minFrequency = frequencies[currentNode][j];
                    bestNeighbour = j;
                }

            }
            result[k+1] = bestNeighbour;
            candidates.remove((Integer)bestNeighbour);
            currentNode = bestNeighbour;
        }
        return result;
    }

    private void calculateFrequencies(){
        int[][] distances = fileInput.getData();
        frequencies = new int[distances.length][distances.length];

        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances.length; j++) {
                int sum = 0;
                for (int k = 0; k < distances.length; k++) {
                    sum += distances[i][k] + distances[k][j];
                }
                frequencies[i][j] = distances.length * distances[i][j] - sum;

            }
        }
    }

}
