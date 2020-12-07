package com.livetyping.library.interfaces;

import androidx.transition.Transition;
import android.view.View;

public interface CannyTransition {
    Transition getTransition(View inChild, View outChild);
}