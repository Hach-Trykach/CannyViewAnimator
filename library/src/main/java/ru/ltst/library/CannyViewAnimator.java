package ru.ltst.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

public class CannyViewAnimator extends FrameLayout {

    int indexWhichChild = 0;

    private InAnimator inAnimator;
    private OutAnimator outAnimator;
    private Map<View, Boolean> attachedList;

    public CannyViewAnimator(Context context) {
        super(context);
        attachedList = new HashMap<>(getChildCount());
    }

    public CannyViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        attachedList = new HashMap<>(getChildCount());
    }

    public void setInAnimator(InAnimator inAnimator) {
        this.inAnimator = inAnimator;
    }

    public void setOutAnimator(OutAnimator outAnimator) {
        this.outAnimator = outAnimator;
    }

    public void setDisplayedChildId(int id) {
        if (getDisplayedChildId() == id) {
            return;
        }
        for (int i = 0, count = getChildCount(); i < count; i++) {
            if (getChildAt(i).getId() == id) {
                setDisplayedChild(i);
                return;
            }
        }
        throw new IllegalArgumentException("No view with ID " + id);
    }

    public void setDisplayedChild(int whichChild) {
        this.indexWhichChild = whichChild;
        if (whichChild >= getChildCount()) {
            this.indexWhichChild = 0;
        } else if (whichChild < 0) {
            this.indexWhichChild = getChildCount() - 1;
        }
        boolean hasFocus = getFocusedChild() != null;
        showOnly(this.indexWhichChild);
        if (hasFocus) {
            requestFocus(FOCUS_FORWARD);
        }
    }

    void showOnly(int inChildIndex) {
        final int count = getChildCount();
        int outChildIndex = 0;
        for (int i = 0; i < count; i++) {
            if (getChildAt(i).getVisibility() == VISIBLE) {
                outChildIndex = i;
                break;
            }
        }
        animate(inChildIndex, outChildIndex);
    }

    private void animate(final int inChildIndex, int outChildIndex) {
        final View inChild = getChildAt(inChildIndex);
        final View outChild = getChildAt(outChildIndex);
        final Animator inAnimator = this.inAnimator.getInAnimator(inChild, outChild);
        final Animator outAnimator = this.outAnimator.getOutAnimator(inChild, outChild);
        if (attachedList.get(outChild) && attachedList.get(inChild) && outAnimator != null) {
            outAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    outChild.setVisibility(GONE);
                    inChild.setVisibility(VISIBLE);
                    if (inAnimator != null) inAnimator.start();
                }
            });
            outAnimator.start();
        } else {
            outChild.setVisibility(GONE);
            inChild.setVisibility(VISIBLE);
        }
    }


    public int getDisplayedChildId() {
        return getChildAt(getDisplayedChild()).getId();
    }

    public int getDisplayedChild() {
        return indexWhichChild;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        attachedList.put(child, false);
        child.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                attachedList.put(v, true);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                attachedList.put(v, false);
            }
        });
        super.addView(child, index, params);
        if (getChildCount() == 1) {
            child.setVisibility(View.VISIBLE);
        } else {
            child.setVisibility(View.GONE);
        }
        if (index >= 0 && indexWhichChild >= index) {
            setDisplayedChild(indexWhichChild + 1);
        }
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        indexWhichChild = 0;
    }

    @Override
    public void removeView(View view) {
        final int index = indexOfChild(view);
        if (index >= 0) {
            removeViewAt(index);
        }
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        final int childCount = getChildCount();
        if (childCount == 0) {
            indexWhichChild = 0;
        } else if (indexWhichChild >= childCount) {
            setDisplayedChild(childCount - 1);
        } else if (indexWhichChild == index) {
            setDisplayedChild(indexWhichChild);
        }
    }

    @Override
    public void removeViewInLayout(View view) {
        removeView(view);
    }

    @Override
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
        if (getChildCount() == 0) {
            indexWhichChild = 0;
        } else if (indexWhichChild >= start && indexWhichChild < start + count) {
            setDisplayedChild(indexWhichChild);
        }
    }

    @Override
    public void removeViewsInLayout(int start, int count) {
        removeViews(start, count);
    }

    public View getCurrentView() {
        return getChildAt(indexWhichChild);
    }

    public interface InAnimator {
        Animator getInAnimator(View inChild, View outChild);
    }

    public interface OutAnimator {
        Animator getOutAnimator(View inChild, View outChild);
    }
}
