package com.example.pedalka.mycustomviewapplication.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.pedalka.mycustomviewapplication.R;
import com.example.pedalka.mycustomviewapplication.ui.customviews.CellLayout;

import java.util.ArrayList;
import java.util.Random;

;

public class CellsActivity extends AppCompatActivity {
    private CellLayout cellLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_cell_layout);

        cellLayout = (CellLayout) findViewById(R.id.cell_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cells, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        doRandomRearrange();


        return true;
    }

    private void doRandomRearrange() {
        Random rnd = new Random();


        ArrayList<View> children = new ArrayList<View>();
        final ArrayList<ChildPair> pairs = new ArrayList<ChildPair>();
        for (int i = 0; i < cellLayout.getChildCount(); i++) {
            children.add(cellLayout.getChildAt(i));
        }


        int pairsSize = cellLayout.getChildCount() / 2;
        for (int i = 0; i < pairsSize; i++) {
            ChildPair pair = new ChildPair();
            int randomIdx = rnd.nextInt(children.size());
            pair.child1 = children.remove(randomIdx);
            randomIdx = rnd.nextInt(children.size());
            pair.child2 = children.remove(randomIdx);


            pair.child1Bounds = getViewBounds(pair.child1);
            pair.child2Bounds = getViewBounds(pair.child2);


            pairs.add(pair);
        }


        for (ChildPair childPair : pairs) {
            final View child1 = childPair.child1;
            final View child2 = childPair.child2;


            CellLayout.LayoutParams child1Params = (CellLayout.LayoutParams) child1.getLayoutParams();
            CellLayout.LayoutParams child2Params = (CellLayout.LayoutParams) child2.getLayoutParams();
            CellLayout.LayoutParams tmp = new CellLayout.LayoutParams(child1Params);


            swap(child1Params, child2Params);
            swap(child2Params, tmp);
        }
        cellLayout.requestLayout();


        cellLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                cellLayout.getViewTreeObserver().removeOnPreDrawListener(this);


                ArrayList<ValueAnimator> animators = new ArrayList<ValueAnimator>();
                for (ChildPair childPair : pairs) {
                    Rect child1New = getViewBounds(childPair.child1);
                    Rect child2New = getViewBounds(childPair.child2);


                    animators.add(createAnimator(childPair.child1, childPair.child1Bounds, child1New));
                    animators.add(createAnimator(childPair.child2, childPair.child2Bounds, child2New));
                }


                AnimatorSet as = new AnimatorSet();
                as.playTogether(animators.toArray(new ValueAnimator[animators.size()]));
                as.setDuration(300);
                as.setInterpolator(new AccelerateDecelerateInterpolator());
                as.start();
                return true;
            }
        });


    }

    public ValueAnimator createAnimator(View view, Rect child1Old, Rect child1New) {
        PropertyValuesHolder leftHolder = PropertyValuesHolder.ofInt("left", child1Old.left, child1New.left);
        PropertyValuesHolder rightHolder = PropertyValuesHolder.ofInt("right", child1Old.right, child1New.right);
        PropertyValuesHolder topHolder = PropertyValuesHolder.ofInt("top", child1Old.top, child1New.top);
        PropertyValuesHolder bottomHolder = PropertyValuesHolder.ofInt("bottom", child1Old.bottom, child1New.bottom);


        return ObjectAnimator.ofPropertyValuesHolder(view, leftHolder, rightHolder, topHolder, bottomHolder);
    }

    private static void swap(CellLayout.LayoutParams dst, CellLayout.LayoutParams src) {
        dst.width = src.width;
        dst.height = src.height;
        dst.top = src.top;
        dst.left = src.left;
    }

    private static class ChildPair {
        View child1;
        View child2;
        Rect child1Bounds;
        Rect child2Bounds;
    }

    private static Rect getViewBounds(View child) {
        return new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
    }
}
