package com.example.noelroy.contactmanager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noel Roy on 04-06-2016.
 */

/**
 * class for handling database data
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "contactManager";
    static final String TABLE_NAME = "contacts";


    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_PHONE = "phone";
    static final String KEY_EMAIL = "email";
    static final String KEY_ADDRESS = "address";
    static final String KEY_IMAGEURI = "imageUri";



    static final String CREATE_DB_TABLE = "CREATE TABLE " + TABLE_NAME
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_PHONE + " TEXT NOT NULL,"
            + KEY_EMAIL + " TEXT NOT NULL,"
            + KEY_ADDRESS + " TEXT NOT NULL,"
            + KEY_IMAGEURI + " TEXT NOT NULL);";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void createContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,contact.get_name());
        contentValues.put(KEY_PHONE,contact.get_phone());
        contentValues.put(KEY_EMAIL,contact.get_email());
        contentValues.put(KEY_ADDRESS,contact.get_address());
        contentValues.put(KEY_IMAGEURI,contact.get_imageUri().toString());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();

    }

    public Contact getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS,KEY_IMAGEURI},KEY_ID + "=?",new String[] {String.valueOf(id)}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return contact;
    }

    public void deleteContact (Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID + "=?",new String[] {String.valueOf(contact.get_id())});
        db.close();
    }

    public int getContactsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,contact.get_name());
        contentValues.put(KEY_PHONE,contact.get_phone());
        contentValues.put(KEY_EMAIL,contact.get_email());
        contentValues.put(KEY_ADDRESS, contact.get_address());
        contentValues.put(KEY_IMAGEURI, contact.get_imageUri().toString());
        int rowsAffected = db.update(TABLE_NAME, contentValues, KEY_ID + "=?", new String[]{String.valueOf(contact.get_id())});
        db.close();
        return rowsAffected;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                /*
                *Equivalent code given below
                Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
                contacts.add(contact);
                */
                contacts.add(new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    return contacts;
    }
}
