package mariannelinhares.mnistandroid;

/*
   Copyright 2016 Narrative Nights Inc. All Rights Reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   From: https://raw.githubusercontent
   .com/miyosuda/TensorFlowAndroidMNIST/master/app/src/main/java/jp/narr/tensorflowmnist
   /DrawModel.java
*/

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import mariannelinhares.mnistandroid.models.Classification;
import mariannelinhares.mnistandroid.models.Classifier;
import mariannelinhares.mnistandroid.models.TensorFlowClassifier;
import mariannelinhares.mnistandroid.views.DrawModel;
import mariannelinhares.mnistandroid.views.DrawView;

/**
 * Changed by marianne-linhares on 21/04/17.
 * https://raw.githubusercontent.com/miyosuda/TensorFlowAndroidMNIST/master/app/src/main/java/jp
 * /narr/tensorflowmnist/DrawModel.java
 */

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private static final int PIXEL_WIDTH = 28;

    // ui related
    private Button clearBtn, classBtn;
    private TextView resText;
    private List<Classifier> mClassifiers = new ArrayList<>();

    // views related
    private DrawModel drawModel;
    private DrawView drawView;
    private PointF mTmpPiont = new PointF();

    private float mLastX;
    private float mLastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get drawing view
        drawView = (DrawView) findViewById(R.id.draw);
        drawModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);

        drawView.setModel(drawModel);
        drawView.setOnTouchListener(this);

        //clear button
        clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);

        //class button
        classBtn = (Button) findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        // res text
        resText = (TextView) findViewById(R.id.tfRes);

        // tensorflow
        loadModel();
    }

    @Override
    protected void onResume() {
        drawView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        drawView.onPause();
        super.onPause();
    }

    private void loadModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "TensorFlow",
                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
                                    "input", "output", true));
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "Keras",
                                    "opt_mnist_convnet-keras.pb", "labels.txt", PIXEL_WIDTH,
                                    "conv2d_1_input", "dense_2/Softmax", false));
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_clear) {
            drawModel.clear();
            drawView.reset();
            drawView.invalidate();

            resText.setText("");
        } else if (view.getId() == R.id.btn_class) {
            float pixels[] = drawView.getPixelData();

            String text = "";
            for (Classifier classifier : mClassifiers) {
                final Classification res = classifier.recognize(pixels);
                if (res.getLabel() == null) {
                    text += classifier.name() + ": ?\n";
                } else {
                    text += String.format("%s: %s, %f\n", classifier.name(), res.getLabel(),
                            res.getConf());
                }
            }
            resText.setText(text);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        drawView.calcPos(mLastX, mLastY, mTmpPiont);
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        drawModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawView.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }
}