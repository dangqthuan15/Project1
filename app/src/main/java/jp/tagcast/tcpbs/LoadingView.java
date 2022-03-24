package jp.tagcast.tcpbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class LoadingView extends View {

    private boolean flgStartDraw = false;
    private int animCount = 0;
    private float scale;

    private OnAnimationFinishListener onAnimationFinishListener = null;
    public interface OnAnimationFinishListener{
        void onFinish(View view);
    }

    public LoadingView(@NonNull final Context context, final float scale, @Nullable OnAnimationFinishListener listener) {
        super(context);
        this.scale = scale;
        this.onAnimationFinishListener = listener;
        initialize(context);
    }

    public void start(){
        animCount = 0;
        if (animationThread != null && animationThread.isAlive() && !animationThread.isInterrupted()) {
            animationThread.interrupt();
        }
        animationThread = new AnimationThread();
        animationThread.start();
    }

    private AnimationThread animationThread = null;
    private class AnimationThread extends Thread{
        @Override
        public void run() {
            boolean flgFirst = true;
            long startTime = 0;
            while (!isInterrupted()) {
                long nowTime = System.currentTimeMillis();
                if (flgStartDraw) {
                    if (flgFirst){
                        flgFirst = false;
                        startTime = System.currentTimeMillis();
                    }
                    animCount = (int) (nowTime - startTime);
                    postInvalidate();
                    if (animCount >= 3000) {
                        animCount = 3000;
                        break;
                    }
                }
                long sleepTime = (long) ((1000f / 60f) - (System.currentTimeMillis() - nowTime));
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            if (onAnimationFinishListener != null) {
                onAnimationFinishListener.onFinish(LoadingView.this);
            }
        }
    }

    private Rect src = new Rect();
    private RectF dst = new RectF();
    private Bitmap bmpChecking = null;
    private Bitmap bmpBuilding = null;
    private Paint paintDef = new Paint();
    private Paint paintBuilding = new Paint();

    private void initialize(Context context){
        setDrawingCacheEnabled(false);
        if (animationThread != null && animationThread.isAlive() && !animationThread.isInterrupted()) {
            animationThread.interrupt();
        }
        animationThread = null;
        animCount = 0;
        paintDef.setDither(false);
        paintDef.setFilterBitmap(true);
        paintDef.setAntiAlias(true);
        paintDef.setStyle(Paint.Style.FILL);
        paintBuilding.setDither(false);
        paintBuilding.setFilterBitmap(true);
        paintBuilding.setAntiAlias(true);
        paintBuilding.setColorFilter(new PorterDuffColorFilter(0xffb31011, PorterDuff.Mode.SRC_ATOP));
        bmpChecking = BitmapFactory.decodeResource(context.getResources(), R.drawable.caption_middle_a);
        bmpBuilding = BitmapFactory.decodeResource(context.getResources(), R.drawable.building_a);
        src.left = 0;
        src.top = 0;

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        flgStartDraw = true;
        final int width = getWidth();
        final int height = getHeight();
        final float cx = width/2f;
        final float cy = height/2f;

        int defAlpha = 255;
        if (animCount > 2700) {
            if (animCount < 3000) {
                defAlpha = Math.round(((3000 - animCount) / 300f) * 255f);
            } else  {
                defAlpha = 0;
            }
            if (defAlpha < 0) {
                defAlpha=0;
            }
        }

        // CircleBG
        if (animCount > 200) {
            paintDef.setColor(0xff999999);
            paintDef.setAlpha(defAlpha);
            final float circleBgRad;
            if (animCount < 500) {
                circleBgRad = (1f - (500 - animCount) / 300f) * 100f * scale;
            } else {
                circleBgRad = 100f * scale;
            }
            canvas.drawCircle(cx, cy - 68f * scale, circleBgRad, paintDef);
        }

        // CheckingBmp
        if (animCount > 500) {
            src.right = 154;
            src.bottom = 37;
            dst.left = cx - 77f * scale;
            dst.top = cy + 80f * scale;
            dst.right = dst.left + src.right * scale;
            dst.bottom = dst.top + src.bottom * scale;
            final float alpha;
            if (animCount < 600) {
                alpha = 1f -(600 - animCount) / 100f;
            } else if (animCount < 1000) {
                alpha = 1.0f;
            } else if (animCount < 1300) {
                alpha = (1300 - animCount) / 300f;
            } else if (animCount < 1400) {
                alpha = 0f;
            } else if (animCount < 1800) {
                alpha = 1f - (1800 - animCount) / 400f;
            } else if (animCount < 2100) {
                alpha = (2100 - animCount) / 300f;
            } else if (animCount < 2200) {
                alpha = 0f;
            } else {
                alpha = 1.0f;
            }
            if (defAlpha == 255) {
                paintDef.setAlpha(Math.round(255f * alpha));
            } else {
                paintDef.setAlpha(defAlpha);
            }
            canvas.drawBitmap(bmpChecking, src, dst, paintDef);
        }

        // CircleWhite
        if (animCount > 700) {
            dst.left = cx - 100f * scale;
            dst.top = cy - (100f + 68f) * scale;
            dst.right = cx + 100f * scale;
            dst.bottom = cy + (100f - 68f) * scale;
            paintDef.setColor(0xffffffff);
            paintDef.setAlpha(defAlpha);
            float angle;
            if (animCount < 1000){
                angle = (1f - (1000 - animCount) / 300f) * 133.2f;
            } else if (animCount < 1800) {
                angle = (1f - (1800 - animCount) / 800f) * 90f + 133.2f;
            } else if (animCount < 2100) {
                angle = (1f - (2100 - animCount) / 300f) * 136.8f + 223.2f;
            } else {
                angle = 360f;
            }
            canvas.drawArc(dst, -90, angle, true, paintDef);
        }

        // BuildingBmp
        if (animCount > 600) {
            src.right = 88;
            src.bottom = 88;
            dst.left = cx - 44f * scale;
            dst.top = cy - 68f * scale - 44f * scale;
            dst.right = dst.left + src.right * scale;
            dst.bottom = dst.top + src.bottom * scale;
            paintDef.setAlpha(defAlpha);
            paintBuilding.setAlpha(defAlpha);
            if (animCount < 2200) {
                canvas.drawBitmap(bmpBuilding, src, dst, paintDef);
            } else {
                canvas.drawBitmap(bmpBuilding, src, dst, paintBuilding);
            }
        }
    }
}
