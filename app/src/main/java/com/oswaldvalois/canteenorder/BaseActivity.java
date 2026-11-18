package com.oswaldvalois.canteenorder;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Keeps content clear of the status and navigation bars. The app targets
 * Android 15+ where windows are edge-to-edge by default, so every screen
 * pads its root view by the system bar insets.
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
}
