package com.oswaldvalois.canteenorder;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive order form: numbered section cards, horizontal menu picker,
 * highlighted toppings, portion stepper + seekbar and an animated running total.
 * Saves the order to SQLite, then offers to open the list through an Intent.
 */
public class MainActivity extends BaseActivity {

    private static final int TOPPING_CHEESE_PRICE = 3000;
    private static final int TOPPING_EGG_PRICE = 2000;
    private static final int TOPPING_SAMBAL_PRICE = 1000;

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<View> menuViews = new ArrayList<>();

    private TextInputLayout nameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText inputName;
    private TextInputEditText inputPassword;
    private CheckBox checkCheese;
    private CheckBox checkEgg;
    private CheckBox checkSambal;
    private SeekBar seekPortion;
    private Spinner spinnerMenu;
    private TextView textPortion;
    private TextView textTotal;
    private TextView summaryMenuPrice;
    private TextView summaryToppingPrice;
    private TextView summaryPortion;
    private ViewGroup menuRow;

    private int selectedIndex = 0;
    private int shownTotal = 0;
    private ValueAnimator totalAnimator;
    private OrderDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySystemBarInsets(R.id.root);

        db = new OrderDbHelper(this);

        nameLayout = findViewById(R.id.nameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);
        checkCheese = findViewById(R.id.checkCheese);
        checkEgg = findViewById(R.id.checkEgg);
        checkSambal = findViewById(R.id.checkSambal);
        seekPortion = findViewById(R.id.seekPortion);
        spinnerMenu = findViewById(R.id.spinnerMenu);
        textPortion = findViewById(R.id.textPortion);
        textTotal = findViewById(R.id.textTotal);
        summaryMenuPrice = findViewById(R.id.summaryMenuPrice);
        summaryToppingPrice = findViewById(R.id.summaryToppingPrice);
        summaryPortion = findViewById(R.id.summaryPortion);
        menuRow = findViewById(R.id.menuRow);

        setupMenuPicker();
        setupListeners();
        refreshMenuSelection();
        updateSummary(true);
    }

    private void setupMenuPicker() {
        menuItems.add(new MenuItem(getString(R.string.menu_fried_rice), 15000));
        menuItems.add(new MenuItem(getString(R.string.menu_noodles), 13000));
        menuItems.add(new MenuItem(getString(R.string.menu_crispy_chicken), 14000));
        menuItems.add(new MenuItem(getString(R.string.menu_satay), 16000));
        menuItems.add(new MenuItem(getString(R.string.menu_iced_tea), 5000));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < menuItems.size(); i++) {
            final int position = i;
            View view = inflater.inflate(R.layout.item_menu_choice, menuRow, false);
            ((TextView) view.findViewById(R.id.menuName)).setText(menuItems.get(i).getName());
            ((TextView) view.findViewById(R.id.menuPrice)).setText(Money.rupiah(menuItems.get(i).getPrice()));
            view.setOnClickListener(v -> {
                selectedIndex = position;
                spinnerMenu.setSelection(position);
                refreshMenuSelection();
                updateSummary(false);
            });
            menuRow.addView(view);
            menuViews.add(view);
        }

        List<String> labels = new ArrayList<>();
        for (MenuItem item : menuItems) {
            labels.add(item.getName() + " · " + Money.rupiah(item.getPrice()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMenu.setAdapter(adapter);
        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedIndex) {
                    selectedIndex = position;
                    refreshMenuSelection();
                    updateSummary(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refreshMenuSelection() {
        for (int i = 0; i < menuViews.size(); i++) {
            boolean active = i == selectedIndex;
            menuViews.get(i).setSelected(active);
            menuViews.get(i).animate().alpha(active ? 1f : 0.55f).setDuration(150).start();
        }
    }

    private void setupListeners() {
        View.OnClickListener toppingListener = v -> updateSummary(false);
        checkCheese.setOnClickListener(toppingListener);
        checkEgg.setOnClickListener(toppingListener);
        checkSambal.setOnClickListener(toppingListener);

        seekPortion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSummary(false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        findViewById(R.id.buttonPlus).setOnClickListener(v -> {
            if (seekPortion.getProgress() < seekPortion.getMax()) {
                seekPortion.setProgress(seekPortion.getProgress() + 1);
                textPortion.animate().scaleX(1.08f).scaleY(1.08f).setDuration(90)
                        .withEndAction(() -> textPortion.animate().scaleX(1f).scaleY(1f).setDuration(90).start())
                        .start();
            }
        });
        findViewById(R.id.buttonMinus).setOnClickListener(v -> {
            if (seekPortion.getProgress() > 0) {
                seekPortion.setProgress(seekPortion.getProgress() - 1);
            }
        });

        findViewById(R.id.buttonOrder).setOnClickListener(v -> submitOrder());
        findViewById(R.id.buttonViewOrders).setOnClickListener(v -> open(OrderListActivity.class));
    }

    private int quantity() {
        return seekPortion.getProgress() + 1;
    }

    private int menuPrice() {
        return menuItems.get(Math.max(selectedIndex, 0)).getPrice();
    }

    private int toppingPrice() {
        int price = 0;
        if (checkCheese.isChecked()) {
            price += TOPPING_CHEESE_PRICE;
        }
        if (checkEgg.isChecked()) {
            price += TOPPING_EGG_PRICE;
        }
        if (checkSambal.isChecked()) {
            price += TOPPING_SAMBAL_PRICE;
        }
        return price;
    }

    private void updateSummary(boolean instant) {
        int portion = quantity();
        int total = (menuPrice() + toppingPrice()) * portion;

        summaryMenuPrice.setText(Money.rupiah(menuPrice()));
        summaryToppingPrice.setText(Money.rupiah(toppingPrice()));
        summaryPortion.setText(getResources().getQuantityString(R.plurals.portion_count, portion, portion));
        textPortion.setText(getResources().getQuantityString(R.plurals.portion_count, portion, portion));
        animateTotal(total, instant);
    }

    /** Counts the total up/down instead of snapping to the new value. */
    private void animateTotal(int target, boolean instant) {
        if (totalAnimator != null) {
            ValueAnimator running = totalAnimator;
            totalAnimator = null;
            running.cancel();
        }
        if (instant || target == shownTotal) {
            shownTotal = target;
            textTotal.setText(Money.rupiah(target));
            return;
        }
        totalAnimator = ValueAnimator.ofInt(shownTotal, target);
        totalAnimator.setDuration(320);
        totalAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            shownTotal = value;
            textTotal.setText(Money.rupiah(value));
        });
        totalAnimator.start();
    }

    private String selectedToppings() {
        List<String> toppings = new ArrayList<>();
        if (checkCheese.isChecked()) {
            toppings.add(getString(R.string.topping_cheese_short));
        }
        if (checkEgg.isChecked()) {
            toppings.add(getString(R.string.topping_egg_short));
        }
        if (checkSambal.isChecked()) {
            toppings.add(getString(R.string.topping_sambal_short));
        }
        return toppings.isEmpty()
                ? getString(R.string.no_topping)
                : String.join(", ", toppings);
    }

    private void submitOrder() {
        String name = textOf(inputName);
        String password = textOf(inputPassword);

        nameLayout.setError(null);
        passwordLayout.setError(null);

        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_required));
            inputName.requestFocus();
            scrollTo(inputName);
            return;
        }
        if (password.length() < 4) {
            passwordLayout.setError(getString(R.string.error_password_required));
            inputPassword.requestFocus();
            scrollTo(inputPassword);
            return;
        }

        Order order = new Order(
                0,
                name,
                menuItems.get(Math.max(selectedIndex, 0)).getName(),
                selectedToppings(),
                quantity(),
                (menuPrice() + toppingPrice()) * quantity(),
                System.currentTimeMillis());

        if (db.insert(order) <= 0) {
            return;
        }

        View content = findViewById(R.id.root);
        Snackbar.make(content, R.string.order_saved, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_view, v -> open(OrderListActivity.class))
                .show();
        resetForm();
    }

    private void scrollTo(View view) {
        NestedScrollView parent = findScroll(view);
        if (parent != null) {
            parent.smoothScrollTo(0, Math.max(0, view.getTop() - 40));
        }
    }

    private NestedScrollView findScroll(View view) {
        View current = view;
        while (current != null) {
            if (current instanceof NestedScrollView) {
                return (NestedScrollView) current;
            }
            current = (View) current.getParent();
        }
        return null;
    }

    private void resetForm() {
        inputName.setText("");
        inputPassword.setText("");
        checkCheese.setChecked(false);
        checkEgg.setChecked(false);
        checkSambal.setChecked(false);
        seekPortion.setProgress(0);
        selectedIndex = 0;
        spinnerMenu.setSelection(0);
        refreshMenuSelection();
        inputName.clearFocus();
        inputPassword.clearFocus();
        updateSummary(false);
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        if (totalAnimator != null) {
            totalAnimator.cancel();
        }
        db.close();
        super.onDestroy();
    }
}
