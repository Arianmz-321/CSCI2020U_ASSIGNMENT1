# Assignment 01 - Spam Detector (Instructions)
> Course: CSCI 2020U: Software Systems Development and Integration

This is the template for your Assignment 01.

## Overview
You have become frustrated with all the advertisements in your inbox. You resolve to create a spam detector to filter out the spam. The spam detector will use a dataset of E-Mails (spam or otherwise) to train your program to recognize whether or not new E-Mails are spam. The program will use a unigram approach [1], where each word is counted and associated with whether or not the message is spam. Your program will calculate probabilities based on each word’s frequency [2]. Luckily, you have not emptied your spam folder or inbox in quite a while, so you have many samples to use to train your system. 

- Check the `Canvas/Assingments/Assignment 01` for more the detailed instructions.

### SpamDetectorServer - Endpoints

**Listing all the test files**

This will return a `application/json` content type.
- `http://localhost:8080/spamDetector-1.0/api/spam`
See a sample of the response data:
```
[{"spamProbRounded":"0.00000","file":"00006.654c4ec7c059531accf388a807064363","spamProbability":5.901245803391957E-62,"actualClass":"Ham"},{"spamProbRounded":"0.00000","file":"00007.2e086b13730b68a21ee715db145522b9","spamProbability":2.800348071907053E-12,"actualClass":"Ham"},{"spamProbRounded":"0.00000","file":"00008.6b73027e1e56131377941ff1db17ff12","spamProbability":8.66861037294167E-14,"actualClass":"Ham"},{"spamProbRounded":"0.00000","file":"00009.13c349859b09264fa131872ed4fb6e4e","spamProbability":6.947265471550557E-12,"actualClass":"Ham"},{"spamProbRounded":"0.00000","file":"00010.d1b4dbbad797c5c0537c5a0670c373fd","spamProbability":1.8814467288977145E-7,"actualClass":"Ham"},{"spamProbRounded":"0.00039","file":"00011.bc1aa4dca14300a8eec8b7658e568f29","spamProbability":3.892844289937937E-4,"actualClass":"Ham"}]
```

**Calculate and get accuracy**
This will return a `application/json` content type.
- `http://localhost:8080/spamDetector-1.0/api/spam/accuracy`
See a sample of the response data:
```
{"val": 0.87564}
```

**Calculate and get precision**
This will return a `application/json` content type.
- `http://localhost:8080/spamDetector-1.0/api/spam/precision`
See a sample of the response data:
```
{"val": 0.56484}
```
### SpamDetectorServer - SpamDetector class

Most of your programming will be in the `SpamDetector` class. This class will be responsible for reading the testing and training data files, training, and tesing the model.

> Obs1. Feel free to create other helper classes as you see fit.
> 
> Obs2. You are not expected to get the exact same values as the ones shown in the samples.

### References 
[1] https://en.wikipedia.org/wiki/Bag-of-words_model 

[2] https://en.wikipedia.org/wiki/Naive_Bayes_spam_filtering 
