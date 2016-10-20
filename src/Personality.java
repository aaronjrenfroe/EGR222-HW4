/*
This Program accepts an input File Name and and Output FileName
The Infile should be at least one Keirsey Test result with a Name and 70 answers
It then scores the test and outputs the results to the Output
Created by Aaron Renfroe 2016
*/

import java.util.*;
import java.io.*;

public class Personality {
    // Class constant FOUR because of the 4 sections
    static final int FOUR = 4;
    //object that contains file locations
    static final class IOLocations {
        private String inFileName;
        private String outFileName;

        private IOLocations() {
            this.inFileName = "";
            this.outFileName = "";
        }
        //makes sure filenames have extention ".txt"
        //if not it will add them
        private IOLocations(String inName, String outName) {

                checkFileNames(inName);
                this.inFileName = checkFileNames(inName);

                this.outFileName = checkFileNames(outName);
        }

        public String getInFileName(){
            return this.inFileName;
        }
        public String getOutFileName(){
            return this.outFileName;
        }
        public void setInFileName(String inName){
            this.inFileName = checkFileNames(inName);
        }
        public void setOutFileName(String outName){
            this.outFileName = checkFileNames(outName);
        }
        private String checkFileNames(String fileName){
            String last4 = fileName.substring(fileName.length()-4);
            if (!last4.equals(".txt")){
                return (fileName + ".txt");
            }
            else{
                return fileName;
            }
        }
    }
    //object that contains myTest Taker:
    // name, Answers in AB-form, answers in ISTJ form, and Answers in Numerical score form
    static final class KeirseyData {
        private String name;
        private String data;
        private String score = "";
        public int[] numericalScore = {0,0,0,0};


        private KeirseyData(String name, String data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return this.name;
        }

        public String getData() {
            return data;
        }

        public void setScore(String score) {
            this.score += score;
        }
        public String getNumericalScoreAsString(){
            String numbersToString = "[";
            for (int i = 0; i < FOUR; i++) {
                numbersToString +=numericalScore[i];
            }
            return numbersToString;
        }

        public String getScore() {
            return this.score;
        }
    }

    // main method
    public static void main(String[] args) {
        //Print intro
        printIntro();

        //get search terms
        IOLocations myLocations = getSearchTerms();

        //Open file and set KTest objects
        KeirseyData[] myTests = getFileData(myLocations.inFileName);

        //scores tests
        scoreMyTests(myTests);

        //print results
        printResults(myTests, myLocations.outFileName);

    }
    // Prints the intro to what the program does
    public static void printIntro() {
        System.out.println("This program processes a file of answers to the\n" +
                "Keirsey Temperament Sorter.  It converts the\n" +
                "various A and B answers for each myTest into\n " +
                "a sequence of B-percentages and then into a\n" +
                "four-letter myTestality type.");
    }
    // Request the in and out file from the user returns IOLocation
    public static IOLocations getSearchTerms() {
        Scanner console = new Scanner(System.in);
        IOLocations myLocations = new IOLocations();
        System.out.print("input file name? ");
        myLocations.setInFileName(console.next());
        System.out.print("output file name? ");
        myLocations.setOutFileName(console.next());
        return myLocations;
    }
    // opens file and extracts the test answers, creates and returns KeirseyData[]
    public static KeirseyData[] getFileData(String inFile) {

        int numberOfLines = 1;

        try {

            Scanner openedFile = new Scanner(new File(inFile));
            while (openedFile.hasNextLine()) {
                openedFile.nextLine();
                numberOfLines++;
            }
            KeirseyData[] myTests = new KeirseyData[numberOfLines / 2];

            openedFile = new Scanner(new File(inFile));

            for (int i = 0; i < myTests.length; i++) {
                KeirseyData myTest = new KeirseyData(openedFile.nextLine(), openedFile.nextLine());
                myTests[i] = myTest;
            }
            return myTests;


        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + inFile+ " " + ex);
        }

        return new KeirseyData[0];

    }
    //Looks at answers and creates the numberical score
    public static void scoreMyTests(KeirseyData[] myTests) {
        for (KeirseyData test :myTests) {


            String answers = test.data.toLowerCase();
            int[][] scoreCounts = new int[FOUR][2];

            for (int i = 0; i < 70; i++) {
                int bit;
                if (answers.charAt(i) == 'a') {
                    bit = 0;
                } else if (answers.charAt(i) == 'b') {
                    bit = 1;

                } else {
                    bit = -1;
                }

                if (bit >= 0) {
                    //expression
                    int dimension = (((i % 7) + 1) / 2);
                    scoreCounts[dimension][0] += bit;
                    scoreCounts[dimension][1]++;
                }
            }
            scoreToAlpha(test, scoreCounts);



        }
    }
    //Converts the numerical score to an alpha score
    public static void scoreToAlpha(KeirseyData test, int[][] scoreCounts){
        String[][] possibleValues = {{"E", "I"}, {"S", "N"}, {"T", "F"}, {"J", "P"}};
        for (int i = 0; i < FOUR; i++) {
            double score = (scoreCounts[i][0] / (double) (scoreCounts[i][1]));
            test.numericalScore[i] = (int) (Math.round(score * 100));
            test.setScore(inscribeScore(Math.round(score * 100), possibleValues[i][0], possibleValues[i][1]));
        }
    }
    // helper method used to calculate if score is leaning towards 0 or 100
    public static String inscribeScore(double score, String a, String b){
        if (score > 50) {
            return b;
        }else if (score < 50){
            return a;
        }
        else{
            return "X";
        }
    }
    //prints the results to the given file
    public static void printResults(KeirseyData[] myTestTakers, String outFileName) {
        try {
            PrintStream writer = new PrintStream(new File(outFileName));
            for (int i = 0; i < myTestTakers.length; i++) {
                writer.print(myTestTakers[i].getName() + ": [");
                for (int j = 0; j < FOUR; j++) {
                    writer.print(myTestTakers[i].numericalScore[j]);
                    if (j < FOUR - 1) {
                        writer.print(", ");
                    }

                }
                writer.println("] = " + myTestTakers[i].getScore());
            }
        }catch (FileNotFoundException ex){


        }
    }


}
