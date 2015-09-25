package com.example.pedalka.mycustomviewapplication.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.example.pedalka.mycustomviewapplication.R;

/**
 * Created by Vitali on 24/09/2015.
 */
public class CellLayout extends ViewGroup {

    private float cellSize;

    /**
     * Number of coumns.
     */
    private int columns = 4;
    /**
     * Default size in dp that will be used for a cell in case no other clues
     * were given by parent.
     */
    private static final int DEFAULT_CELL_SIZE = 48;
    /**
     * An optional margin to be applied to each child.
     */
    private int spacing = 0;


    //--------------------------------
    // Contractors
    //--------------------------------
    public CellLayout(Context context) {
        super(context);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);

    }

    //----------------------------
    // Override methods
    //----------------------------
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int top = (int) (layoutParams.top * cellSize) + getPaddingTop() + spacing;
            int left = (int) (layoutParams.left * cellSize) + getPaddingLeft() + spacing;
            int righgt = (int) ((layoutParams.left + layoutParams.width) * cellSize) + getPaddingLeft() - spacing;
            int bottom = (int) ((layoutParams.top + layoutParams.height) * cellSize) + getPaddingTop() - spacing;

            child.layout(left,top,righgt,bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*super.onMeasure(widthMeasureSpec, heightMeasureSpec);*/
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY)
        {
            width = MeasureSpec.getSize(widthMeasureSpec);
            cellSize = (float)(getMeasuredWidth() - getPaddingLeft() - getPaddingRight())/(float)columns;
        }
        else //MeasureSpec.UNSPECIFIED
        {
            cellSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CELL_SIZE, getResources().getDisplayMetrics());
            width = (int) (columns * cellSize);
        }

        int childCount = getChildCount();
        View child;

        int maxRow = 0;

        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int top = layoutParams.top;
            int w = layoutParams.width;
            int h = layoutParams.height;

            int bottom = top + h;

            int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (w * cellSize) - spacing * 2, MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec((int) (h * cellSize) - spacing * 2, MeasureSpec.EXACTLY);

            child.measure(childWidthSpec, childHeightSpec);

            if(bottom > maxRow){
                maxRow = bottom;
            }

        }

        int measuredHeight = Math.round(maxRow * cellSize) + getPaddingTop() + getPaddingBottom();
        if(heightMode == MeasureSpec.EXACTLY)
        {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        else if (heightMode == MeasureSpec.AT_MOST)
        {
            int atMostHeight = MeasureSpec.getSize(heightMeasureSpec);
            height = Math.min(atMostHeight, measuredHeight);
        }
        else //MeasureSpec.UNSPECIFIED
        {
            height = measuredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        /*return super.generateLayoutParams(attrs);*/
        return new CellLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        /*return super.generateLayoutParams(p);*/
        return new CellLayout.LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        /*return super.generateDefaultLayoutParams();*/
        return new LayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        /*return super.checkLayoutParams(p);*/
        return p instanceof CellLayout.LayoutParams;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CellLayout, 0, 0);


        try {
            columns = a.getInt(R.styleable.CellLayout_columns, 4);
            spacing = a.getDimensionPixelSize(R.styleable.CellLayout_spacing, 0);
        } finally {
            a.recycle();
        }
    }


    //LayoutParams
    public static class LayoutParams extends ViewGroup.LayoutParams{
        /**
         * An Y coordinate of the top most cell the view resides in.
         */
        public int top = 0;
        /**
         * An X coordinate of the left most cell the view resides in.
         */
        public int left = 0;
        /**
         * Number of cells occupied by the view horizontally.
         */
        public int width = 1;
        /**
         * Number of cells occupied by the view vertically.
         */
        public int height = 1;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CellLayout);
            top = a.getInt(R.styleable.CellLayout_layout_top, 0);
            left = a.getInt(R.styleable.CellLayout_layout_left, 0);
            height = a.getInt(R.styleable.CellLayout_layout_cellsHeight, -1);
            width = a.getInt(R.styleable.CellLayout_layout_cellsWidth, -1);

            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            if (source instanceof LayoutParams)
            {
                LayoutParams cellLayoutParams = (LayoutParams)source;
                top = cellLayoutParams.top;
                left = cellLayoutParams.left;
                width = cellLayoutParams.width;
                height = cellLayoutParams.height;
            }
        }

        public LayoutParams (){
            this(MATCH_PARENT,MATCH_PARENT);
        }
    }
}
