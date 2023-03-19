package com.spamdetector.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.Response;
//import jdk.incubator.vector.VectorOperators;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic

    SpamDetector detector = new SpamDetector();
    List<TestFile> spamResult; //declare
    double accuracy; //declare



    SpamResource() throws IOException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");


//      TODO: call  this.trainAndTest();
        //call function
        spamResult = this.trainAndTest();

    }


    @GET
    @Produces("application/json")
    public Response getSpamResults() throws IOException {
//       TODO: return the test results list of TestFile, return in a Response object
        //Endpoint URL: http://localhost:8080/spamDetector-1.0/api/spam

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(spamResult)
                .build();

    }


    @GET
    @Path("/accuracy") //endpoint: /api/spam/accuracy
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object
        //Endpoint URL: http://localhost:8080/spamDetector-1.0/api/spam/accuracy

        accuracy = this.detector.spamAccuracy(spamResult); //call function

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(accuracy)
                .build();

        //return null;
    }


    @GET
    @Path("/precision") //endpoint: /api/spam/precision
    @Produces("application/json")
    public Response getPrecision() {
        //      TODO: return the precision of the detector, return in a Response object
        //Endpoint URL: http://localhost:8080/spamDetector-1.0/api/spam/precision

        //send results of test

        double precision1 = detector.spamPrecision(spamResult);
        //String content = this.readFileContents("/");


        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(precision1)
                .build();

        //return null;
    }


    private List<TestFile> trainAndTest() throws IOException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        //URL url = this.getClass().getClassLoader().getResource("/csci2020u-assignment01-template/spamDetectorServer/src/main/resources/data"); //Access data folder

        File mainDirectory = new File(getClass().getClassLoader().getResource("data").getFile());
        File data;

        return this.detector.trainAndTest(mainDirectory);
    }

    private double spamAccuracy() throws IOException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//      TODO: load the main directory "data" here from the Resources folder
        File data;

        return this.detector.spamAccuracy(spamResult);
    }

    private double spamPrecision() throws IOException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//      TODO: load the main directory "data" here from the Resources folder
        File data;

        return this.detector.spamPrecision(spamResult);
    }
}