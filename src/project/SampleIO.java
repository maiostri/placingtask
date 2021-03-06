package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import project.entity.Location;
import project.entity.RankedListElement;
import project.entity.Sample;

public class SampleIO {

    public static Sample readMediaSample(File sampleFile, GroundTruthResolver groundTruthResolver, final int numberOfElementsUsed) throws IOException {
        String sampleIdentifier = readSampleIdentifierFromFilename(sampleFile.getName());

        //System.out.println("Loading sample " + sampleIdentifier);

        Location sampleGroundTruth = null;
        if (groundTruthResolver != null) {
            sampleGroundTruth = groundTruthResolver.get(sampleIdentifier);
        }

        Sample mediaSample = new Sample(sampleIdentifier, sampleGroundTruth);

        String s = null;
        BufferedReader in = new BufferedReader(new FileReader(sampleFile));
        int count = 0;
        while (count < numberOfElementsUsed && (s = in.readLine()) != null) {
            String[] lineElements = s.trim().split("\\s++");

            String runIdentifier = readSampleIdentifierFromDevSampleName(lineElements[1]);
            double similarity = Double.parseDouble(lineElements[0].replace(",", "."));

            Location runGroundTruth = null;
            if (groundTruthResolver != null) {
                runGroundTruth = groundTruthResolver.get(runIdentifier);
            }
            RankedListElement run = new RankedListElement(runIdentifier, runGroundTruth, similarity);

            mediaSample.addSimilarityElement(run);
            count++;
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

    public static void writeSampleToFile(Sample sample, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (RankedListElement element : sample.getSimilarityList()) {
            bw.write(element.getSimilarityFactor() + "\t" + element.getIdentifier());
            bw.newLine();
        }
        bw.close();
    }
}
