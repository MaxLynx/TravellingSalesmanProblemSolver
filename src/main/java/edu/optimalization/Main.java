package edu.optimalization;


import edu.optimalization.model.Solver;

public class Main {

    public static void main(String[] args) {

        Solver solver = new Solver("src\\main\\resources\\Matica_TT_(0249).txt");

        int[][] frequencies = solver.getFrequencies();
        System.out.println("Frequencies:");
        for (int i = 0; i < frequencies.length; i++) {
            for (int j = 0; j < frequencies[0].length; j++) {
                System.out.print(frequencies[i][j] + " ");
            }
            System.out.println();
        }

        solver.findStartingSolution();

        System.out.println("Starting solution found:");
        for (int i: solver.getResult()) {
            System.out.print((i+1) + " "); // apply numeration from 1
        }
        System.out.println();
        System.out.println("Starting solution total frequency: " + solver.getResultTotalFrequency());

        solver.solve();

        System.out.println("Simulated Annealing completed");
        System.out.println("Final solution:");
        for (int i: solver.getResult()) {
            System.out.print((i+1) + " "); // apply numeration from 1
        }
        System.out.println();
        System.out.println("Solution total frequency: " + solver.getResultTotalFrequency());
    }
}
