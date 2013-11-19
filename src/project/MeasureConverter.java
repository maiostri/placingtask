package project;

import java.io.File;
import java.io.IOException;
import project.entity.Sample;

/**
 * Utilitario para converter medidas dos ranked lists de uma pasta, de similaridade para distancia e vice-versa
 */
public class MeasureConverter {

    public static void main(String[] args) throws IOException {
        File sampleDir = new File("C:/MO633_projeto/rlsimExecucao1Dados");
        File outputDir = new File("C:/MO633_projeto/rlsimExecucao1Dados2");

        FileUtils.deleteDirectory(outputDir);
        outputDir.mkdirs();

        for (File sampleFile : sampleDir.listFiles()) {
            Sample sample = SampleIO.readMediaSample(sampleFile, null, Integer.MAX_VALUE);
            System.out.println("converting " + sample);
            sample.convertSimilarityFactorMeasures();
            SampleIO.writeSampleToFile(sample, new File(outputDir, sampleFile.getName()));
        }
    }
}
