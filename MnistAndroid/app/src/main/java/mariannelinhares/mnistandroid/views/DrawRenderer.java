package mariannelinhares.mnistandroid.views;
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

   https://raw.githubusercontent.com/miyosuda/TensorFlowAndroidMNIST/master/app/src/main/java/jp/narr/tensorflowmnist/DrawRenderer.java
*/

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mariannelinhares.mnistandroid.views.DrawModel;

/**
 * Changed by marianne-linhares on 21/04/17.
 * https://raw.githubusercontent.com/miyosuda/TensorFlowAndroidMNIST/master/app/src/main/java/jp/narr/tensorflowmnist/DrawRenderer.java
 */

public class DrawRenderer {
    /**
     * Draw lines to canvas
     */
    public static void renderModel(Canvas canvas, DrawModel model, Paint paint,
                                   int startLineIndex) {
        paint.setAntiAlias(true);

        int lineSize = model.getLineSize();
        for (int i = startLineIndex; i < lineSize; ++i) {
            DrawModel.Line line = model.getLine(i);
            paint.setColor(Color.BLACK);
            int elemSize = line.getElemSize();
            if (elemSize < 1) {
                continue;
            }
            DrawModel.LineElem elem = line.getElem(0);
            float lastX = elem.x;
            float lastY = elem.y;

            for (int j = 0; j < elemSize; ++j) {
                elem = line.getElem(j);
                float x = elem.x;
                float y = elem.y;
                canvas.drawLine(lastX, lastY, x, y, paint);
                lastX = x;
                lastY = y;
            }
        }
    }
}