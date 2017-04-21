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
*/


/**
 * Changed by marianne-linhares on 21/04/17.
 */

import java.util.ArrayList;
import java.util.List;

public class DrawModel {

    public static class LineElem {
        public float x;
        public float y;

        private LineElem(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Line {
        private List<LineElem> elems = new ArrayList<>();

        private Line() {
        }

        private void addElem(LineElem elem) {
            elems.add(elem);
        }

        public int getElemSize() {
            return elems.size();
        }

        public LineElem getElem(int index) {
            return elems.get(index);
        }
    }

    private Line mCurrentLine;

    private int mWidth;  // pixel width = 28
    private int mHeight; // pixel height = 28

    private List<Line> mLines = new ArrayList<>();

    public DrawModel(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void startLine(float x, float y) {
        mCurrentLine = new Line();
        mCurrentLine.addElem(new LineElem(x, y));
        mLines.add(mCurrentLine);
    }

    public void endLine() {
        mCurrentLine = null;
    }

    public void addLineElem(float x, float y) {
        if (mCurrentLine != null) {
            mCurrentLine.addElem(new LineElem(x, y));
        }
    }

    public int getLineSize() {
        return mLines.size();
    }

    public Line getLine(int index) {
        return mLines.get(index);
    }

    public void clear() {
        mLines.clear();
    }
}