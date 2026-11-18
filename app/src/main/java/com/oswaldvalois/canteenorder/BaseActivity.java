package com.oswaldvalois.canteenorder;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Shared screen behaviour: keep content clear of the system bars (the app
 * targets Android 15+ where windows are edge-to-edge by default) and animate
 * screen transitions.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void applySystemBarInsets(int rootViewId) {
        View root = findViewById(rootViewId);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });
    }

    /** Opens another screen with a slide-in transition. */
    protected void open(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }
}
