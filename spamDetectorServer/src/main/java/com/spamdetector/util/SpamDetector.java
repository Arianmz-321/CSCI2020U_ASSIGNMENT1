package com.spamdetector.util;

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
    public Map<String, Float> fileSpamProbDir(File dir, Map<String,Float> probSpam) throws IOException {
        Map<String, Float> probabilities = new TreeMap<>(); //global map for <Email, Prob that email is spam>

        File[] filesInDir = dir.listFiles();
        int numFiles = filesInDir.length;

        float PrSWi; //Prob that a file is spam, given that it contains the word Wi

        // iterate over each file in the dir, and calculate probability that email is spam
        for (int i = 0; i<numFiles; i++){
            //for each file in directory, check word by word
            Map<String, Integer> wordMap = findWordInFile(filesInDir[i]);
            float eta = 0;


            // merge the file wordMap into the global frequencies
            Set<String> keys = wordMap.keySet();
            Iterator<String> keyIterator = keys.iterator(); //iterate over all the keys
            while (keyIterator.hasNext()){
                String word  = keyIterator.next();

                PrSWi = probSpam.get(word); //Prob that a file is spam, given that it contains the word Wi

                eta += (Math.log((1 - PrSWi)) - Math.log(PrSWi));

            }

            float PrSF = (float) (1 / (1 + Math.pow(Math.E, eta))); //prob that a file is spam, after checking all words of file

            probabilities.put(filesInDir[i].getName(), PrSF);
        }

        return probabilities; //map of <Email, Prob that email is spam>
    }

    public List<TestFile> trainAndTest(File mainDirectory) throws IOException {
//        TODO: main method of loading the directories and files, training and testing the model

        Map<String, Integer> trainHamFreq = new TreeMap<>(); //map of <word, number of files containing that word in ham folder>
        Map<String, Integer> trainSpamFreq = new TreeMap<>(); //map of <word, number of files containing that word in spam folder>
        Map<String, Float> probSpam = new TreeMap<>(); //map of <word Wi, PSWi>
        //PSWi = Prob that file is spam, given that it contains the word Wi

        //????
        File hamFiles = new File("/train", "/ham"); //directory of ham files (/train/ham)
        File[] hamFilesList = hamFiles.listFiles();

        File spamFiles = new File("/train", "/spam"); //directory of spam files (/train/spam)
        File[] spamFilesList = spamFiles.listFiles();


        //????
        trainHamFreq = fileFreqDir(hamFiles); //map of <word, number of files containing that word in ham folder>
        trainSpamFreq = fileFreqDir(spamFiles); //map of <word, number of files containing that word in spam folder>


        int numHamFiles = hamFilesList.length; //number of files in (/train/ham) subfolder
        int numSpamFiles = spamFilesList.length; //number of files in (/train/spam) subfolder


        //Formulas
        float PrWiS;
        float PrWiH;
        float PrSWi = 0;
        String word;


        Set<String> keysSpam = trainSpamFreq.keySet();
        Iterator<String> frequenciesIteratorSpam = keysSpam.iterator(); //iterate over words

        while (frequenciesIteratorSpam.hasNext()) {
            //if(trainHamFreq.containsKey(word)) {
            word = frequenciesIteratorSpam.next();
            PrWiS = (trainSpamFreq.get(word) / numSpamFiles); //Prob that the word Wi appears in spam file

            if (trainHamFreq.containsKey(word)) {
                PrWiH = (trainHamFreq.get(word) / numHamFiles); //Prob that the word Wi appears in ham file
            } else {
                PrWiH = 0;
            }

            PrSWi = (PrWiS / (PrWiS + PrWiH)); //Prob that a file is spam, given that it contains the word Wi

            probSpam.put(word, PrSWi); //put word and probability PrSWi in map probSpam
        }

        //testing
        File TestHamFiles = new File("/test", "/ham"); //directory of ham files (/test/ham)
        File[] TestHamFilesList = TestHamFiles.listFiles();


        File TestSpamFiles = new File("/test", "/spam"); //directory of spam files (/test/spam)
        File[] TestSpamFilesList = TestSpamFiles.listFiles();

        Map<String, Float> testHamMap = new TreeMap<>();
        Map<String, Float> testSpamMap = new TreeMap<>();

        testHamMap = fileSpamProbDir(TestHamFiles, probSpam);
        testSpamMap = fileSpamProbDir(TestSpamFiles, probSpam);


        List<TestFile> spamResult = new ArrayList<TestFile>();


        Set<String> keys = testHamMap.keySet();
        Iterator<String> keyIterator = keys.iterator(); //iterate over all the keys
        while (keyIterator.hasNext()) { //iterate through map
            String name = keyIterator.next();
            float prob = testHamMap.get(name);

            //result.filename = name;
            //result.spamProbability = prob;
            //result.actualClass = "Ham";

            TestFile result = new TestFile(name, prob, "Ham"); //object of TestFile
            //result.TestFile(name, prob, "Ham");
            spamResult.add(result);
        }

        Set<String> keys1 = testSpamMap.keySet();
        Iterator<String> keyIterator1 = keys.iterator();
        while (keyIterator1.hasNext()) {
            String name = keyIterator1.next();
            float prob = testSpamMap.get(name);

            //result.filename = name;
            //result.spamProbability = prob;
            //result.actualClass = "Spam";

            TestFile result = new TestFile(name, prob, "Spam");
            spamResult.add(result);
        }

        return spamResult;
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

        //Positive: email is spam
        //set 0.95 as the threshold
        //assumption: if spam prob is >= 0.95, the prediction is that the email is spam

        int numTruePositive = 0; //model correctly predicts the positive class
        int numTrueNegative = 0; //model correctly predicts the negative class
        int numFalsePositive = 0; //model incorrectly predicts the positive class
        int numFiles = spamResult.size();

        float accuracy;
        float precision;
        double prob;
        String realClass;


        for (int i = 0; i<numFiles; i++) {
            prob = spamResult.get(i).getSpamProbability();
            realClass = spamResult.get(i).getActualClass();

            //if file is in spam folder and prob of file being spam is 0.95 or greater
            if (prob >= 0.95 && realClass == "Spam") {

                numTruePositive += 1; //increase by 1
            }

            else if (prob < 0.95 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is less than 0.95

                numTrueNegative += 1;
            }

            else if (prob >= 0.95 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is 0.95 or greater

                numFalsePositive += 1;
            }
        }

        accuracy = ((numTruePositive + numTrueNegative) / numFiles);
        return accuracy;
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

        float accuracy;
        float precision;
        double prob;
        String realClass;


        for (int i = 0; i<numFiles; i++) {
            prob = spamResult.get(i).getSpamProbability();
            realClass = spamResult.get(i).getActualClass();

            //if file is in spam folder and prob of file being spam is 0.95 or greater
            if ((prob >= 0.95) && (realClass == "Spam")) {

                numTruePositive += 1; //increase by 1
            }

            else if (prob < 0.95 && realClass == "Ham") { //if file is in ham folder and prob of file being spam is less than 0.95

                numTrueNegative += 1;
            }

            else if ((prob >= 0.95) && (realClass == "Ham")) { //if file is in ham folder and prob of file being spam is 0.95 or greater

                numFalsePositive += 1;
            }
        }

        precision = (numTruePositive / (numFalsePositive + numTruePositive));
        return precision;
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

    //public Map<String, Integer> frequencies(File data) {
    //}


    //check to make sure output works
    public static void displayList(List<TestFile> spamResult)
    {
        for (int i = 0; i < spamResult.size(); i++) {
            System.out.print(spamResult.get(i).getFilename() + " " + spamResult.get(i).getSpamProbability() + " " + spamResult.get(i).getActualClass());
        }
    }
    public static void main(String args[]) throws IOException {

        List<TestFile> spamResult = new ArrayList<>();

        SpamDetector myDetector = new SpamDetector();

        File data1 = new File("/src/main/resources/data/test/spam");
        spamResult = myDetector.trainAndTest(data1);

        //System.out.println(spamResult);
        displayList(spamResult);
    }
}