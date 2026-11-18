package com.oswaldvalois.canteenorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Order form. Contains every required input type: text, password, spinner,
 * checkboxes, seekbar and a submit button. Saves to SQLite, then offers to
 * open the order list through an explicit Intent.
 */
public class MainActivity extends BaseActivity {

    private static final int TOPPING_CHEESE_PRICE = 3000;
    private static final int TOPPING_EGG_PRICE = 2000;
    private static final int TOPPING_SAMBAL_PRICE = 1000;

    private final List<MenuItem> menuItems = new ArrayList<>();

    private TextInputLayout nameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText inputName;
    private TextInputEditText inputPassword;
    private Spinner spinnerMenu;
    private CheckBox checkCheese;
    private CheckBox checkEgg;
    private CheckBox checkSambal;
    private SeekBar seekPortion;
    private TextView textPortion;
    private TextView textTotal;

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
        spinnerMenu = findViewById(R.id.spinnerMenu);
        checkCheese = findViewById(R.id.checkCheese);
        checkEgg = findViewById(R.id.checkEgg);
        checkSambal = findViewById(R.id.checkSambal);
        seekPortion = findViewById(R.id.seekPortion);
        textPortion = findViewById(R.id.textPortion);
        textTotal = findViewById(R.id.textTotal);

        setupMenuSpinner();
        setupListeners();
        updateTotal();
    }

    private void setupMenuSpinner() {
        menuItems.add(new MenuItem("Nasi Goreng Spesial", 15000));
        menuItems.add(new MenuItem("Mie Ayam Bakso", 13000));
        menuItems.add(new MenuItem("Ayam Geprek + Nasi", 14000));
        menuItems.add(new MenuItem("Sate Ayam", 16000));
        menuItems.add(new MenuItem("Es Teh Manis", 5000));

        ArrayAdapter<MenuItem> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, menuItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMenu.setAdapter(adapter);
    }

    private void setupListeners() {
        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateTotal();
            }
        });

        View.OnClickListener toppingListener = v -> updateTotal();
        checkCheese.setOnClickListener(toppingListener);
        checkEgg.setOnClickListener(toppingListener);
        checkSambal.setOnClickListener(toppingListener);

        seekPortion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTotal();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        findViewById(R.id.buttonOrder).setOnClickListener(v -> submitOrder());
        findViewById(R.id.buttonViewOrders).setOnClickListener(v -> openOrderList());
    }

    private int quantity() {
        return seekPortion.getProgress() + 1;
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

    private int totalPrice() {
        int position = Math.max(spinnerMenu.getSelectedItemPosition(), 0);
        int menuPrice = menuItems.get(position).getPrice();
        return (menuPrice + toppingPrice()) * quantity();
    }

    private void updateTotal() {
        textPortion.setText(getString(R.string.portion_value, quantity()));
        textTotal.setText(Money.rupiah(totalPrice()));
    }

    private String selectedToppings() {
        List<String> toppings = new ArrayList<>();
        if (checkCheese.isChecked()) {
            toppings.add("Keju");
        }
        if (checkEgg.isChecked()) {
            toppings.add("Telur");
        }
        if (checkSambal.isChecked()) {
            toppings.add("Sambal");
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
            return;
        }
        if (password.length() < 4) {
            passwordLayout.setError(getString(R.string.error_password_required));
            inputPassword.requestFocus();
            return;
        }

        int position = Math.max(spinnerMenu.getSelectedItemPosition(), 0);
        Order order = new Order(
                0,
                name,
                menuItems.get(position).getName(),
                selectedToppings(),
                quantity(),
                totalPrice(),
                System.currentTimeMillis());

        long newId = db.insert(order);
        if (newId <= 0) {
            return;
        }

        Snackbar.make(findViewById(android.R.id.content), R.string.order_saved,
                        Snackbar.LENGTH_LONG)
                .setAction(R.string.action_view, v -> openOrderList())
                .show();
        resetForm();
    }

    private void resetForm() {
        inputName.setText("");
        inputPassword.setText("");
        checkCheese.setChecked(false);
        checkEgg.setChecked(false);
        checkSambal.setChecked(false);
        seekPortion.setProgress(0);
        spinnerMenu.setSelection(0);
        inputName.clearFocus();
        inputPassword.clearFocus();
        updateTotal();
    }

    private void openOrderList() {
        startActivity(new Intent(this, OrderListActivity.class));
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
