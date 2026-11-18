package com.oswaldvalois.canteenorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** SQLite persistence for canteen orders. */
public class OrderDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kantin.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_ORDERS = "orders";
    private static final String COL_ID = "id";
    private static final String COL_CUSTOMER = "customer";
    private static final String COL_MENU = "menu";
    private static final String COL_TOPPINGS = "toppings";
    private static final String COL_QTY = "qty";
    private static final String COL_TOTAL = "total";
    private static final String COL_CREATED_AT = "created_at";

    public OrderDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CUSTOMER + " TEXT NOT NULL, "
                + COL_MENU + " TEXT NOT NULL, "
                + COL_TOPPINGS + " TEXT NOT NULL, "
                + COL_QTY + " INTEGER NOT NULL, "
                + COL_TOTAL + " INTEGER NOT NULL, "
                + COL_CREATED_AT + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    /** Inserts an order and returns its new row id. */
    public long insert(Order order) {
        ContentValues values = new ContentValues();
        values.put(COL_CUSTOMER, order.getCustomer());
        values.put(COL_MENU, order.getMenu());
        values.put(COL_TOPPINGS, order.getToppings());
        values.put(COL_QTY, order.getQuantity());
        values.put(COL_TOTAL, order.getTotal());
        values.put(COL_CREATED_AT, order.getCreatedAt());
        return getWritableDatabase().insert(TABLE_ORDERS, null, values);
    }

    /** Newest orders first. */
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_ORDERS, null, null, null, null, null,
                COL_CREATED_AT + " DESC, " + COL_ID + " DESC")) {
            while (cursor.moveToNext()) {
                orders.add(readOrder(cursor));
            }
        }
        return orders;
    }

    public Order getById(long id) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_ORDERS, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return readOrder(cursor);
            }
        }
        return null;
    }

    public void delete(long id) {
        getWritableDatabase().delete(TABLE_ORDERS, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    private Order readOrder(Cursor cursor) {
        return new Order(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOMER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TOPPINGS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_QTY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
    }
}
