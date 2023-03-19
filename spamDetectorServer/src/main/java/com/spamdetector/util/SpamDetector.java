package com.spamdetector.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;

//import static java.util.regex.ASCII.isWord;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {


    //helper function
    public Map<String, Integer> fileFreqDir(File dir) throws IOException {
        Map<String, Integer> frequencies = new TreeMap<>(); //global map <Word, Num of files containing word>

        File[] filesInDir = dir.listFiles();
        int numFiles = filesInDir.length;

        // iterate over each file in the dir and find words
        for (int i = 0; i<numFiles; i++) {
            Map<String, Integer> wordMap = findWordInFile(filesInDir[i]);

            // merge the file wordMap into the global frequencies
            Set<String> keys = wordMap.keySet();
            Iterator<String> keyIterator = keys.iterator(); //iterate over all the keys
            while (keyIterator.hasNext()){
                String word  = keyIterator.next();
                //int count = wordMap.get(word);

                if(frequencies.containsKey(word)){
                    // increment if word already exists in previous email (or emails)
                    int oldCount = frequencies.get(word);
                    frequencies.put(word, 1 + oldCount);
                }
                else{
                    frequencies.put(word, 1); //set count to 1 if it's the first time the word appears
                }
            }

        }

        return frequencies; //return treemap of <Word, Num of files containing word>
    }

    //helper function
    public Map<String, Double> fileSpamProbDir(File dir, Map<String,Double> probSpam) throws IOException {
        Map<String, Double> probabilities = new TreeMap<>(); //global map for <Email, Prob that email is spam>

        File[] filesInDir = dir.listFiles();
        int numFiles = filesInDir.length;

        double PrSWi; //Prob that a file is spam, given that it contains the word Wi

        // iterate over each file in the dir, and calculate probability that email is spam
        for (int i = 0; i<numFiles; i++){
            //for each file in directory, check word by word
            Map<String, Integer> wordMap = findWordInFile(filesInDir[i]);
            double eta = 0;


            // merge the file wordMap into the global frequencies
            Set<String> keys = wordMap.keySet();
            Iterator<String> keyIterator = keys.iterator(); //iterate over all the keys
            while (keyIterator.hasNext()){
                String word  = keyIterator.next();


                //probSpam: map of <word Wi, PrSWi>
                if (probSpam.containsKey(word)) {
                    PrSWi = probSpam.get(word); //Prob that a file is spam, given that it contains the word Wi

                    eta += ((double) Math.log((1.0 - PrSWi)) - (double) Math.log(PrSWi));
                }
            }

            double PrSF = (double) (1.0 / (1.0 + Math.pow(Math.E, eta))); //prob that a file is spam, after checking all words of file

            probabilities.put(filesInDir[i].getName(), PrSF);
        }

        return probabilities; //map of <Email, Prob that email is spam>
    }

    public List<TestFile> trainAndTest(File mainDirectory) throws IOException {
//        TODO: main method of loading the directories and files, training and testing the model

        Map<String, Integer> trainHamFreq = new TreeMap<>(); //map of <word, number of files containing that word in ham folder>
        Map<String, Integer> trainSpamFreq = new TreeMap<>(); //map of <word, number of files containing that word in spam folder>
        Map<String, Double> probSpam = new TreeMap<>(); //map of <word Wi, PSWi>
        //PrSWi = Prob that file is spam, given that it contains the word Wi

        File hamFiles = new File(mainDirectory, "/train/ham"); //directory of ham files (/train/ham)
        File[] hamFilesList = hamFiles.listFiles();

        File spamFiles = new File(mainDirectory, "/train/spam"); //directory of spam files (/train/spam)
        File[] spamFilesList = spamFiles.listFiles();


        trainHamFreq = fileFreqDir(hamFiles); //map of <word, number of files containing that word in ham folder>
        trainSpamFreq = fileFreqDir(spamFiles); //map of <word, number of files containing that word in spam folder>


        int numHamFiles = hamFilesList.length; //number of files in (/train/ham) subfolder
        int numSpamFiles = spamFilesList.length; //number of files in (/train/spam) subfolder


        //Formulas
        double PrWiS;
        double PrWiH;
        double PrSWi = 0;
        String word;


        Set<String> keysSpam = trainSpamFreq.keySet();
        Iterator<String> frequenciesIteratorSpam = keysSpam.iterator(); //iterate over words

        while (frequenciesIteratorSpam.hasNext()) {
            //if(trainHamFreq.containsKey(word)) {
            word = frequenciesIteratorSpam.next();
            PrWiS = ((double) trainSpamFreq.get(word) / (double) numSpamFiles); //Prob that the word Wi appears in spam file

            if (trainHamFreq.containsKey(word)) {
                PrWiH = (trainHamFreq.get(word) / numHamFiles); //Prob that the word Wi appears in ham file
            } else {
                PrWiH = 0;
            }

            PrSWi = (PrWiS / (PrWiS + PrWiH)); //Prob that a file is spam, given that it contains the word Wi

            probSpam.put(word, PrSWi); //put word and probability PrSWi in map probSpam
        }

        //testing
        File TestHamFiles = new File(mainDirectory, "/test/ham"); //directory of ham files (/test/ham)
        File[] TestHamFilesList = TestHamFiles.listFiles();


        File TestSpamFiles = new File(mainDirectory, "/test/spam"); //directory of spam files (/test/spam)
        File[] TestSpamFilesList = TestSpamFiles.listFiles();

        Map<String, Double> testHamMap = new TreeMap<>();
        Map<String, Double> testSpamMap = new TreeMap<>();

        testHamMap = fileSpamProbDir(TestHamFiles, probSpam);
        testSpamMap = fileSpamProbDir(TestSpamFiles, probSpam);


        List<TestFile> spamResult = new ArrayList<TestFile>();


        Set<String> keys = testHamMap.keySet();
        Iterator<String> keyIterator = keys.iterator(); //iterate over all the keys
        while (keyIterator.hasNext()) { //iterate through map
            String name = keyIterator.next();
            double prob = testHamMap.get(name);

            //result.filename = name;
            //result.spamProbability = prob;
            //result.actualClass = "Ham";

            TestFile result = new TestFile(name, prob, "Ham"); //object of TestFile
            //result.TestFile(name, prob, "Ham");
            spamResult.add(result);
        }

        Set<String> keys1 = testSpamMap.keySet();
        Iterator<String> keyIterator1 = keys1.iterator();
        while (keyIterator1.hasNext()) {
            String name = keyIterator1.next();
            double prob = testSpamMap.get(name);

            //result.filename = name;
            //result.spamProbability = prob;
            //result.actualClass = "Spam";

            TestFile result = new TestFile(name, prob, "Spam"); //put name, prob, class into TestFile obj
            spamResult.add(result);
        }

        return spamResult; //return ArrayList<TestFile>
        //TestFile contains name, probability, actualclass
    }

    //helper function: determine which new words exist in each file (ie each email)
    private Map<String, Integer> findWordInFile(File file) throws IOException { //helper function
        Map<String, Integer> wordMap = new TreeMap<>();
        if (file.exists()) {
            //load all the data and process it into words
            Scanner scanner = new Scanner(file); //read file
            while (scanner.hasNext()) {
                String word = (scanner.next()).toLowerCase(); //ignore casing for words
                if (isWord(word)) { //if string is a word
                    //add the word if it has not appeared in same email
                    if (!wordMap.containsKey(word)) {
                        wordMap.put(word, 1);
                    }
                }

            }
        }
        return wordMap;

    }

    //helper function
    public double spamAccuracy(List<TestFile> spamResult) {

        //Define Positive: email is spam
        //set 0.6 as the threshold
        //assumption: if spam prob is >= 0.6, the prediction is that the email is spam

        int numTruePositive = 0; //model correctly predicts the positive class
        int numTrueNegative = 0; //model correctly predicts the negative class
        int numFalsePositive = 0; //model incorrectly predicts the positive class
        int numFiles = spamResult.size();

        double accuracy;
        double precision;
        double prob;
        String realClass;


        for (int i = 0; i<numFiles; i++) {
            prob = spamResult.get(i).getSpamProbability();
            realClass = spamResult.get(i).getActualClass();

            //if file is in spam folder and prob of file being spam is 0.95 or greater
            if (prob >= 0.6 && realClass == "Spam") {

                numTruePositive += 1; //increase by 1
            }

            else if (prob < 0.6 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is less than 0.95

                numTrueNegative += 1;
            }

            else if (prob >= 0.6 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is 0.95 or greater

                numFalsePositive += 1;
            }
        }

        accuracy = ((numTruePositive + numTrueNegative) / numFiles);
        return accuracy;
    }

    //Creating a class
    public class SpamAccuracy1 {
        @JsonProperty("accuracy")
        private double accuracy;

        public SpamAccuracy1(double accuracy) { //constructor
            this.accuracy = accuracy;
        }
        public double getAccuracy() {
            return this.accuracy;
        }

        public void setAccuracy(double value) {
            this.accuracy = value;
        }
    }


    //helper function
    public double spamPrecision(List<TestFile> spamResult) {

        //Positive: email is spam
        //set 0.95 as the threshold
        //assumption: if spam prob is >= 0.95, the prediction is that the email is spam

        int numTruePositive = 0; //model correctly predicts the positive class
        int numTrueNegative = 0; //model correctly predicts the negative class
        int numFalsePositive = 0; //model incorrectly predicts the positive class
        int numFiles = spamResult.size();

        double accuracy;
        double precision;
        double prob;
        String realClass;


        for (int i = 0; i<numFiles; i++) {
            prob = spamResult.get(i).getSpamProbability();
            realClass = spamResult.get(i).getActualClass();

            //if file is in spam folder and prob of file being spam is 0.95 or greater
            if ((prob >= 0.6) && (realClass == "Spam")) {

                numTruePositive += 1; //increase by 1
            }

            else if (prob < 0.6 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is less than 0.95

                numTrueNegative += 1;
            }

            else if ((prob >= 0.6) && (realClass == "Ham")) { //if file is in ham folder and prob of file being spam is 0.95 or greater

                numFalsePositive += 1;
            }
        }

        precision = (numTruePositive / (numFalsePositive + numTruePositive));
        return precision;
    }

    //creating a class
    public class SpamPrecision1 {

        @JsonProperty("precision")
        private double precision;

        public SpamPrecision1(double precision) { //constructor
            this.precision = precision;
        }
        public double getPrecision() {
            return this.precision;
        }

        public void setPrecision(double value) {
            this.precision = value;
        }
    }


    //check if string is a word or not
    private Boolean isWord(String word){
        if (word == null){
            return false;
        }

        String pattern = "^[a-zA-Z]*$";
        if(word.matches(pattern)){
            return true;
        }

        return false;

    }


    //check to make sure output works
    public static void displayList(List<TestFile> spamResult)
    {
        for (int i = 0; i < spamResult.size(); i++) {
            System.out.println(spamResult.get(i).getFilename() + " " + spamResult.get(i).getSpamProbability() + " " + spamResult.get(i).getActualClass());
        }
    }
    public static void main(String args[]) throws IOException {

        List<TestFile> spamResult = new ArrayList<>();

        SpamDetector myDetector = new SpamDetector();

        //File data1 = new File("C:/Users/Matthew/Desktop/MatthewWongFiles/wongm/CSCI2020U/Assignments/csci2020u-assignment01-template/spamDetectorServer/src/main/resources/data");
        File data1 = new File("/src/main/resources/data/test/spam"); //can also do this way

        spamResult = myDetector.trainAndTest(data1); //call function

        double precisionResult = myDetector.spamPrecision(spamResult); //call function

        double accuracyResult = myDetector.spamAccuracy(spamResult); //call function

        //System.out.println(spamResult);
        //displayList(spamResult);
        //System.out.println("Precision result:" + precisionResult);
        //System.out.println("Accuracy result:" + accuracyResult);
    }
}