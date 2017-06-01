package mariannelinhares.mnistandroid.models;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * Changed from https://github.com/MindorksOpenSource/AndroidTensorFlowMNISTExample/blob/master
 * /app/src/main/java/com/mindorks/tensorflowexample/TensorFlowImageClassifier.java
 * Created by marianne-linhares on 20/04/17.
 */

public class TensorFlowClassifier implements Classifier {

    // Only returns if at least this confidence
    private static final float THRESHOLD = 0.1f;

    private TensorFlowInferenceInterface tfHelper;

    private String name;
    private String inputName;
    private String outputName;
    private int inputSize;
    private boolean feedKeepProb;

    private List<String> labels;
    private float[] output;
    private String[] outputNames;

    private static List<String> readLabels(AssetManager am, String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)));

        String line;
        List<String> labels = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            labels.add(line);
        }

        br.close();
        return labels;
    }

    public static TensorFlowClassifier create(AssetManager assetManager, String name,
            String modelPath, String labelFile, int inputSize, String inputName, String outputName,
            boolean feedKeepProb) throws IOException {
        TensorFlowClassifier c = new TensorFlowClassifier();

        c.name = name;

        c.inputName = inputName;
        c.outputName = outputName;

        c.labels = readLabels(assetManager, labelFile);

        c.tfHelper = new TensorFlowInferenceInterface(assetManager, modelPath);
        int numClasses = 10;

        c.inputSize = inputSize;

        // Pre-allocate buffer.
        c.outputNames = new String[] { outputName };

        c.outputName = outputName;
        c.output = new float[numClasses];

        c.feedKeepProb = feedKeepProb;

        return c;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Classification recognize(final float[] pixels) {

        tfHelper.feed(inputName, pixels, 1, inputSize, inputSize, 1);
        if (feedKeepProb) {
            tfHelper.feed("keep_prob", new float[] { 1 });
        }
        tfHelper.run(outputNames);

        tfHelper.fetch(outputName, output);

        // Find the best classification
        Classification ans = new Classification();
        for (int i = 0; i < output.length; ++i) {
            System.out.println(output[i]);
            System.out.println(labels.get(i));
            if (output[i] > THRESHOLD && output[i] > ans.getConf()) {
                ans.update(output[i], labels.get(i));
            }
        }

        return ans;
    }
}
