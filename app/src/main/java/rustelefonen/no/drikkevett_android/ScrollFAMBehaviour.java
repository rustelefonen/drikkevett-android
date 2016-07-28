package rustelefonen.no.drikkevett_android;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by simenfonnes on 28.07.2016.
 */

public class ScrollFAMBehaviour extends CoordinatorLayout.Behavior<FloatingActionMenu>{
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean isAnimatingOut = false;

    public ScrollFAMBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        /*if (dyConsumed > 0 && !this.isAnimatingOut && child.getVisibility() == View.VISIBLE) {
            this.animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            this.animateIn(child);
        }*/
    }

    private void animateOut(final FloatingActionMenu menu) {
        ViewCompat.animate(menu).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {

            public void onAnimationStart(View view) {
                ScrollFAMBehaviour.this.isAnimatingOut = true;
            }

            public void onAnimationCancel(View view) {
                ScrollFAMBehaviour.this.isAnimatingOut = false;
            }

            public void onAnimationEnd(View view) {
                ScrollFAMBehaviour.this.isAnimatingOut = false;

                view.setVisibility(View.GONE);
            }
        }).start();
    }

    private void animateIn(FloatingActionMenu menu) {
        menu.setVisibility(View.VISIBLE);

        ViewCompat.animate(menu).scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start();
    }
}















//public class ScrollFAMBehaviour extends CoordinatorLayout.Behavior<FloatingActionMenu>{
//    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
//    private boolean isAnimatingOut = false;
//
//    public ScrollFAMBehaviour(Context context, AttributeSet attrs) {
//        super();
//    }
//
//    @Override
//    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View directTargetChild, View target, int nestedScrollAxes) {
//        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
//    }
//
//    @Override
//    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//
//        /*if (dyConsumed > 0 && !this.isAnimatingOut && child.getVisibility() == View.VISIBLE) {
//            this.animateOut(child);
//        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
//            this.animateIn(child);
//        }*/
//    }
//
//    private void animateOut(final FloatingActionMenu menu) {
//        ViewCompat.animate(menu).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
//
//            public void onAnimationStart(View view) {
//                ScrollFAMBehaviour.this.isAnimatingOut = true;
//            }
//
//            public void onAnimationCancel(View view) {
//                ScrollFAMBehaviour.this.isAnimatingOut = false;
//            }
//
//            public void onAnimationEnd(View view) {
//                ScrollFAMBehaviour.this.isAnimatingOut = false;
//
//                view.setVisibility(View.GONE);
//            }
//        }).start();
//    }
//
//    private void animateIn(FloatingActionMenu menu) {
//        menu.setVisibility(View.VISIBLE);
//
//        ViewCompat.animate(menu).scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start();
//    }
//}
