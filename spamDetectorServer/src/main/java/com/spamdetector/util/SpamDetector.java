package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;

import static java.util.regex.ASCII.isWord;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {

    //helper function
    public Map<String, Integer> fileFreqDir(File dir) {
        Map<String, Integer> frequencies = new TreeMap<>(); //global map <Word, Num of files containing word>

        File[] filesInDir = dir.listFiles();
        int numFiles = filesInDir.length;

        // iterate over each file in the dir and find words
        for (int i = 0; i<numFiles; i++){
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
    public Map<File, Float> fileSpamProbDir(File dir, Map<String,Float> probSpam) {
        Map<File, Float> probabilities = new TreeMap<>(); //global map for <Email, Prob that email is spam>

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

            probabilities.put(filesInDir[i], PrSF);
        }

        return probabilities; //map of <Email, Prob that email is spam>
    }

    public List<TestFile> trainAndTest(File mainDirectory) {
//        TODO: main method of loading the directories and files, training and testing the model

        Map<String, Integer> trainHamFreq = new TreeMap<>(); //map of <word, number of files containing that word in ham folder>
        Map<String, Integer> trainSpamFreq = new TreeMap<>(); //map of <word, number of files containing that word in spam folder>
        Map<String,Float> probSpam = new TreeMap<>(); //map of <word Wi, PSWi>
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
            }

            else {
                PrWiH = 0;
            }

            PrSWi = (PrWiS / (PrWiS + PrWiH)); //Prob that a file is spam, given that it contains the word Wi

            probSpam.put(word, PrSWi); //put word and probability PrSWi in map probSpam
        }

        File TestHamFiles = new File("/test", "/ham"); //directory of ham files (/test/ham)
        File[] TestHamFilesList = TestHamFiles.listFiles();


        File TestSpamFiles = new File("/test", "/spam"); //directory of spam files (/test/spam)
        File[] TestSpamFilesList = TestSpamFiles.listFiles();

        Map<File, Float> testHamMap = new TreeMap<>();
        Map<File, Float> testSpamMap = new TreeMap<>();

        testHamMap = fileSpamProbDir(TestHamFiles, probSpam);
        testSpamMap = fileSpamProbDir(TestSpamFiles, probSpam);

        //????
        return new ArrayList<TestFile>();
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
            return wordMap;
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

    public Map<String, Integer> frequencies(File data) {
    }
}