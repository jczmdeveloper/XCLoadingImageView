package com.xc.xcloadingimageview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;

/**
 * Created by caizhiming on 2015/12/4.
 * XCLoadingImageView - Loading ImageView By MaskDrawable Progress
 */
public class XCLoadingImageView extends ImageView {

    private Paint mImagePaint;
    private int mImageHeight, mImageWidth;
    private boolean mIsAutoStart = false;
    private int mMaskColor = Color.TRANSPARENT;
    private ClipDrawable mClipDrawable;
    private Drawable mMaskDrawable;
    private int maskHeight;
    private int mProgress;
    private ObjectAnimator mAnimator;
    private long mAnimDuration = 2500;
    private float mScaleX, mScaleY;
    private int mGravity = Gravity.BOTTOM;
    private int mOrientaion = ClipDrawable.VERTICAL;
    private int mMaskOrientation = MaskOrientation.BottomToTop;

    //Loading oriention
    public static final class MaskOrientation {
        public static final int LeftToRight = 1;
        public static final int RightToLeft = 2;
        public static final int TopToBottom = 3;
        public static final int BottomToTop = 4;
    }

    public XCLoadingImageView(Context context) {
        super(context);
        init();
    }

    public XCLoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    public XCLoadingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);
    }

    /**
     * initial attributes
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.XCLoadingImageView);
        mMaskColor = t.getColor(R.styleable.XCLoadingImageView_mask_color, mMaskColor);
        mIsAutoStart = t.getBoolean(R.styleable.XCLoadingImageView_auto_start_anim, mIsAutoStart);
        setMaskColor(mMaskColor);
        t.recycle();
    }

    /**
     * initial paint
     */
    private void init() {
        if (mImagePaint == null) {
            mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mImagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //measure the matrix size of ImageView drawable
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        mScaleX = f[Matrix.MSCALE_X];
        mScaleY = f[Matrix.MSCALE_Y];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleX, mScaleY);
        mClipDrawable.setBounds(getDrawable().getBounds());
        mClipDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mMaskColor != Color.TRANSPARENT) {
            init();
            initMaskBitmap(mMaskColor);
            initAnim();
        }
    }

    /**
     * combine tow bitmap to one bitmap
     */
    private Bitmap combineBitmap(Bitmap bg, Bitmap fg) {
        Bitmap bmp = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bg, 0, 0, null);
        canvas.drawBitmap(fg, 0, 0, mImagePaint);
        return bmp;
    }

    private void initMaskBitmap(int maskColor) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bgBmp = ((BitmapDrawable) drawable).getBitmap();
        mImageWidth = drawable.getIntrinsicWidth();
        mImageHeight = drawable.getIntrinsicHeight();

        Bitmap fgBmp = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        Canvas fgCanvas = new Canvas(fgBmp);
        fgCanvas.drawColor(maskColor);

        Bitmap bitmap = combineBitmap(bgBmp, fgBmp);
        mMaskDrawable = new BitmapDrawable(null, bitmap);
        mClipDrawable = new ClipDrawable(mMaskDrawable, mGravity, mOrientaion);
    }
    private void setMaskHeight(int y) {
        maskHeight = y;
        postInvalidate();
    }
    private void initAnim() {
        stopAnim();
        mAnimator = ObjectAnimator.ofInt(mClipDrawable, "level", 0, 10000);
        mAnimator.setDuration(mAnimDuration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        if (mIsAutoStart) {
            mAnimator.start();
        }
    }
    public void setProgress(int progress){
        mProgress = progress;
        mClipDrawable.setLevel(mProgress * 100);
        postInvalidate();
    }
    private void stopAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    /**
     * set animation duration
     */
    public void setMaskAnimDuration(long duration) {
        mAnimDuration = duration;
        initAnim();
    }

    /**
     * start animation
     */
    public void startMaskAnim() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    /**
     * set mask color
     */
    public void setMaskColor(int maskColor) {
        initMaskBitmap(maskColor);
        initAnim();
    }

    /**
     * set orientation
     */
    public void setMaskOrientation(int orientation) {
        switch (orientation) {
            case MaskOrientation.LeftToRight:
                mGravity = Gravity.LEFT;
                mOrientaion = ClipDrawable.HORIZONTAL;
                break;
            case MaskOrientation.RightToLeft:
                mGravity = Gravity.RIGHT;
                mOrientaion = ClipDrawable.HORIZONTAL;
                break;
            case MaskOrientation.TopToBottom:
                mGravity = Gravity.TOP;
                mOrientaion = ClipDrawable.VERTICAL;
                break;
            case MaskOrientation.BottomToTop:
            default:
                mGravity = Gravity.BOTTOM;
                mOrientaion = ClipDrawable.VERTICAL;
                break;
        }
        if (mMaskDrawable == null) {
            return;
        }
        mClipDrawable = new ClipDrawable(mMaskDrawable, mGravity, mOrientaion);
        initAnim();
    }

    public int getMaskOrientation() {
        return mMaskOrientation;
    }

}
