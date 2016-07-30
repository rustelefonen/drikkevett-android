package rustelefonen.no.drikkevett_android;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * Created by simenfonnes on 29.07.2016.
 */

public class ViewCallback {

    public static void onAppear(final View v, final Runnable runnable){
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                runnable.run();
                if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }
}
