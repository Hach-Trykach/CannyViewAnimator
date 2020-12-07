package com.livetyping.cannyviewanimator.transition;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionSet;

import com.livetyping.cannyviewanimator.R;
import com.livetyping.library.CannyViewAnimator;
import com.livetyping.library.animators.property.PropertyAnimators;
import com.livetyping.library.interfaces.CannyTransition;

public class TransitionActivity extends AppCompatActivity {
    private CannyViewAnimator animator;
    private TextView startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        findViews();
        initViews();
    }

    private void findViews(){
        animator = (CannyViewAnimator) findViewById(R.id.transition_animator);
        startButton = (TextView) findViewById(R.id.transition_start_button);
    }

    private void initViews(){
        animator.setCannyTransition(new CannyTransition() {
            @Override
            public Transition getTransition(View inChild, View outChild) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Fade(Fade.IN).addTarget(inChild));
                transitionSet.addTransition(new Fade(Fade.OUT).addTarget(outChild));
                return transitionSet;
            }
        });
        animator.setInAnimator(PropertyAnimators.SCALE_X_HALF, PropertyAnimators.SCALE_Y_HALF);
        animator.setOutAnimator(PropertyAnimators.SCALE_X_HALF, PropertyAnimators.SCALE_Y_HALF);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.setDisplayedChildIndex(animator.getDisplayedChildIndex() + 1);
            }
        });
    }
}
