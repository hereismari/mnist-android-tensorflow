package mariannelinhares.mnistandroid;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements View.OnClickListener {

    // ui related
    private DrawingView drawView;
    private Button clearBtn, classBtn;
    private TextView resText;

    // tensorflow input and output
    private static final int INPUT_SIZE = 28;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/expert-graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get drawing view
        drawView = (DrawingView)findViewById(R.id.drawing);

        //clear button
        clearBtn = (Button)findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);

        //class button
        classBtn = (Button)findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        // res text
        resText = (TextView)findViewById(R.id.tfRes);

        // tensorflow
        loadModel();
    }

    private void loadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = Classifier.create(getApplicationContext().getAssets(),
                                                   MODEL_FILE,
                                                   LABEL_FILE,
                                                   INPUT_SIZE,
                                                   INPUT_NAME,
                                                   OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    public void onClick(View view){

        if(view.getId() == R.id.btn_clear) {
            drawView.clear();
            resText.setText("Result: ");
        }
        else if(view.getId() == R.id.btn_class){

            float pixels[] = drawView.getPixels();

            final Classification res = classifier.recognize(pixels);
            String result = "Result: ";
            if (res.getLabel() == null) {
                resText.setText(result + "?");
            }
            else {
                result += res.getLabel();
                result += "\nwith probability: " + res.getConf();
                resText.setText(result);
            }
        }
    }
}
