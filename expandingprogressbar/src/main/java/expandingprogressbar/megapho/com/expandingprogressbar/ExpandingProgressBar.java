package expandingprogressbar.megapho.com.expandingprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

public class ExpandingProgressBar extends View {
    private static final int DEFAULT_STROKE_WIDTH = 6;
    private static final int DEFAULT_ROTATION_SPEED = 10;
    private static final int DEFAULT_COLOR1 = Color.parseColor("#3F51B5");
    private static final int DEFAULT_COLOR2 = Color.parseColor("#3F51B5");
    private static final int DEFAULT_GRAVITY = Gravity.CENTER;
    private static final float DEFAULT_RADIUS = 120.f;
    private static final float DEFAULT_TOP_PERCENTAGE = 0.7f;

    private Paint mPaint;

    // mRectF holds the position of the circle, it will be used when drawing the arches
    private RectF mRectF;

    // Center of the circle, will be used when drawing the circle in the closing and in the
    // expanding animation.
    private int mCenterX;
    private int mCenterY;

    private int mTopArchAngle = 10;
    private int mBottomArchAngle= 190;

    private float mSweepAngle;
    private double mSweepAngleDelta;

    private boolean mAreArchesExtending = true;

    private double mStrokeWidth;

    private float mTopPercentage = DEFAULT_TOP_PERCENTAGE;

    private int mGravity = DEFAULT_GRAVITY;

    private int mColor = DEFAULT_COLOR1;
    private int mColor2 = DEFAULT_COLOR2;

    private int mRotationSpeed = DEFAULT_ROTATION_SPEED;

    private double mRadius = DEFAULT_RADIUS;

    private final double mClosingCircleSpeedMultiplier = 1.2;

    boolean mIsBackgroundTransparent = false;

    // mCircleRadius is the radius without the both of the arc's strokes
    private double mCircleRadius;

    // Counters for closing and expanding animations
    private int mNumSteps = 0;
    private int mCurrentStep = 0;

    private onExpandListener mOnExpandListener;

    private AnimationState mAnimationState = AnimationState.INVISIBLE;

    private enum AnimationState {
        INVISIBLE,
        LOADING,
        CONNECTING_ARCHES,
        CLOSING,
        EXPANDING
    }

    public interface onExpandListener {
        void preExpand();
        void postExpand();
    }

    public ExpandingProgressBar(Context context) {
        this(context, null);
    }

    public ExpandingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStrokeWidth = dpToPx(context, DEFAULT_STROKE_WIDTH);

        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandingProgressBar);
            mColor = typedArray.getColor(R.styleable.ExpandingProgressBar_color, DEFAULT_COLOR1);
            mColor2 = typedArray.getColor(R.styleable.ExpandingProgressBar_color_gradient, DEFAULT_COLOR2);
            mRadius = typedArray.getDimension(R.styleable.ExpandingProgressBar_radius, DEFAULT_RADIUS);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ExpandingProgressBar_stroke_width, dpToPx(context, DEFAULT_STROKE_WIDTH));
            mRotationSpeed = typedArray.getInt(R.styleable.ExpandingProgressBar_speed, DEFAULT_ROTATION_SPEED);
            mGravity = typedArray.getInteger(R.styleable.ExpandingProgressBar_android_gravity, DEFAULT_GRAVITY);
            mTopPercentage = typedArray.getFloat(R.styleable.ExpandingProgressBar_topPercentage, DEFAULT_TOP_PERCENTAGE);
            typedArray.recycle();
        }

        mSweepAngleDelta = mRotationSpeed / 4;

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth((int)mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mSweepAngle = 10;

        if(mGravity == Gravity.START)
            mCenterX = (int)mRadius;
        else if(mGravity == Gravity.END)
            mCenterX = (int)(w - mRadius);
        else
            mCenterX = w/2;

        mCenterY = (int)(h * mTopPercentage + mRadius);

        mRectF = new RectF(
                (int) (mCenterX - mRadius + 2 * mStrokeWidth),
                (int) (mCenterY - mRadius + 2 * mStrokeWidth),
                (int) (mCenterX + mRadius - 2 * mStrokeWidth),
                (int) (mCenterY + mRadius - 2 * mStrokeWidth));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mAnimationState) {
            case INVISIBLE:
                return;
            case LOADING:
                mTopArchAngle += mRotationSpeed;
                mBottomArchAngle += mRotationSpeed;

                if (mTopArchAngle > 360)
                    mTopArchAngle = mTopArchAngle - 360;

                if (mBottomArchAngle > 360)
                    mBottomArchAngle = mBottomArchAngle - 360;

                if (mAreArchesExtending) {
                    if (mSweepAngle < 160) {
                        mSweepAngle += mSweepAngleDelta;
                        invalidate();
                    }
                }
                else {
                    if (mSweepAngle > mRotationSpeed) {
                        mSweepAngle -= 2 * mSweepAngleDelta;
                        invalidate();
                    }
                }

                if (mSweepAngle >= 160 || mSweepAngle <= 10) {
                    mAreArchesExtending = !mAreArchesExtending;
                    invalidate();
                }
                break;
            case CONNECTING_ARCHES:
                if (mSweepAngle < 180)
                    mSweepAngle += 5;
                else
                    mAnimationState = AnimationState.CLOSING;

                invalidate();
                break;
            case CLOSING:
                if (mCurrentStep <= mNumSteps) {
                    mStrokeWidth += (mClosingCircleSpeedMultiplier * 2);
                    mPaint.setStrokeWidth((int) (mStrokeWidth));

                    mCircleRadius -= mClosingCircleSpeedMultiplier;

                    mCurrentStep++;
                }
                else{
                    if(mOnExpandListener != null)
                        mOnExpandListener.preExpand();

                    mAnimationState = AnimationState.EXPANDING;
                    mCurrentStep = 0;
                    mRadius = mCircleRadius;
                }
                break;
            case EXPANDING:
                double percent = ((mCircleRadius - mRadius) / (getHeight() / 1.1));

                mPaint.setColor(gradient(mColor, mColor2, Math.min(percent, 1)));

                mCircleRadius += mCurrentStep + 1.5;
                mStrokeWidth += mCurrentStep;
                mPaint.setStrokeWidth((int) mStrokeWidth);
                if (mCircleRadius >= getHeight() + mStrokeWidth) {
                    mAnimationState = AnimationState.INVISIBLE;
                }
                else if (mCircleRadius >= getHeight() && !mIsBackgroundTransparent) {
                    mIsBackgroundTransparent = true;
                    setBackgroundColor(Color.parseColor("#00000000"));

                    if(mOnExpandListener != null)
                        mOnExpandListener.postExpand();
                }

                mCurrentStep++;
        }

        if (mAnimationState == AnimationState.LOADING || mAnimationState == AnimationState.CONNECTING_ARCHES) {
            canvas.drawArc(mRectF, mTopArchAngle, mSweepAngle, false, mPaint);
            canvas.drawArc(mRectF, mBottomArchAngle, mSweepAngle, false, mPaint);
        }
        else {
            canvas.drawCircle(mCenterX, mCenterY, (int) mCircleRadius, mPaint);
            invalidate();
        }
    }

    // Calculates the gradient given a starting color, an ending color,
    // and what percentage of the gradient is needed
    // 0 -> color1, 1 -> color2
    private int gradient(int color1, int color2, double percent){
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);

        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);

        int diffRed = r2 - r1;
        int diffGreen = g2 - g1;
        int diffBlue = b2 - b1;

        diffRed = (int) ((diffRed * percent) + r1);
        diffGreen = (int) ((diffGreen * percent) + g1);
        diffBlue = (int) ((diffBlue * percent) + b1);

        return Color.rgb(diffRed, diffGreen, diffBlue);
    }

    private int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    public void start() {
        mAnimationState = AnimationState.LOADING;
        invalidate();
    }

    public void expand() {
        mAnimationState = AnimationState.CONNECTING_ARCHES;

        mCircleRadius = mRadius - mStrokeWidth * 2;
        mNumSteps = (int) ((mCircleRadius - mStrokeWidth * 2) / mClosingCircleSpeedMultiplier);
    }

    public void setOnExpandListener(onExpandListener onExpandListener){
        mOnExpandListener = onExpandListener;
    }
}