package mariannelinhares.mnistandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Changed from ornew on 2017/02/04 at
 * https://github.com/ornew/MNIST-for-Android/blob/master/android/app/src/main/java/net/ornew/mnistforandroid/CanvasView.java
 * Changed by marianne-linhares on 20/04/17.
 */


public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private float scale;
    final int VIEW_WIDTH = 28;
    final int VIEW_HEIGHT = 28;

    public DrawingView(Context context) {
        super(context);
        initialize();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        float scaleX = getWidth() / VIEW_WIDTH;
        float scaleY = getHeight() / VIEW_HEIGHT;
        System.out.println("AQUIIIIIIIIII");
        System.out.println(getWidth());
        System.out.println(getHeight());
        scale = scaleX > scaleY ? scaleY : scaleX;

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        }
        if (canvas == null) {
            canvas = new Canvas(bitmap);
        }
        clear();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //bitmap.recycle();
    }

    void initialize(){
        super.setZOrderOnTop(true);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
    }

    public void clear() {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        holder.unlockCanvasAndPost(canvas);
    }

    public float[] getPixels() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixels[] = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // Set 0 for white and 255 for black pixel
        for (int i = 0; i < pixels.length; ++i) {
            int pix = pixels[i];
            int b = pix & 0xff;
            pixels[i] = 0xff - b;
        }

        for(int i = 0; i < pixels.length; i++) {
            if(i % 28 == 0) { System.out.println(); }
            System.out.print(pixels[i]);
            System.out.print(" ");
        }

        float p[] = new float[width * height];
        for(int i = 0; i < p.length; i++) {
            p[i] = (float)(1.0 * pixels[i]/255.0);
        }
        return p;
    }

    private void drawLine(Path path) {
        Canvas canvas = holder.lockCanvas();
        canvas.scale(scale, scale);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(path, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() / scale;
        float y = event.getY() / scale;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(x, y);
                getPixels();
                break;

            default:
        }

        return true;
    }

    private void onTouchDown(float x, float y) {
        path = new Path();
        path.moveTo(x, y);
    }

    private void onTouchMove(float x, float y) {
        path.lineTo(x, y);
        drawLine(path);
    }

    private void onTouchUp(float x, float y) {
        path.lineTo(x, y);
        canvas.drawPath(path, paint);
        Canvas canvas = holder.lockCanvas();
        canvas.scale(scale, scale);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        holder.unlockCanvasAndPost(canvas);
    }
}