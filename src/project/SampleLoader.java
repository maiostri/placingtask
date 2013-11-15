package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import project.entity.Location;
import project.entity.RankedListElement;
import project.entity.Sample;

public class SampleLoader {

    public static Sample readMediaSample(File sampleFile, GroundTruthResolver groundTruthResolver) throws IOException {
        String sampleIdentifier = readSampleIdentifierFromFilename(sampleFile.getName());

        System.out.println(sampleIdentifier);

        Location sampleGroundTruth = groundTruthResolver.get(sampleIdentifier);

        Sample mediaSample = new Sample(sampleIdentifier, sampleGroundTruth);

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(sampleFile));
        while ((s = in.readLine()) != null) {
            String[] s_args = s.trim().split("\\s++");

            String runIdentifier = readSampleIdentifierFromDevSampleName(s_args[1]);
            Location runGroundTruth = groundTruthResolver.get(runIdentifier);

            RankedListElement run = new RankedListElement(runIdentifier, runGroundTruth, Double.parseDouble(s_args[0].replace(",", ".")));

            mediaSample.addSimilarityElement(run);
        }
        in.close();

        return mediaSample;
    }

    private static String readSampleIdentifierFromFilename(String name) {
        return name.replace("_list.txt", "");
    }

    private static String readSampleIdentifierFromDevSampleName(String devSampleName) {
        return devSampleName.replace("FlickrVideosTrain/", "");
    }
}
