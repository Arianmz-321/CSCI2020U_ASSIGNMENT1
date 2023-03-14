package com.spamdetector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    private String readFileContents;


    @Path("/data")
    Response SpamResource(){
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

//      TODO: call  this.trainAndTest();
        URL url = this.getClass().getClassLoader().getResource("/data"); //Access data folder
        File data = null;
        try {
            data = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        SpamDetector myDetector = new SpamDetector();
        Map<String, Integer> mapFunc = myDetector.frequencies(data);

        ObjectMapper mapper;
        Response myResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:8448")
                .header("Content-Type", "application/json")
                .entity(mapper.writeValueAsString(mapFunc))
                .build();

        return myResp;
    }
    @GET
    @Produces("application/json")
    public Response getSpamResults() {
//       TODO: return the test results list of TestFile, return in a Response object
        String content = this.readFileContents("/TestFile.java");

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(content)
                .build();

        return null;
    }


    @GET
    @Path("//accuracy") //endpoint: /api/spam/accuracy
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object
        String content = this.readFileContents("/TestFile.java");

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(content)
                .build();

        return null;
    }

    @GET
    @Path("/precision") //endpoint: /api/spam/precision
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object
        String content = this.readFileContents("/TestFile.java");

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(content)
                .build();

        return null;
    }

    private List<TestFile> trainAndTest()  {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        File mainDirectory = null;
        return this.detector.trainAndTest(mainDirectory);
    }
}