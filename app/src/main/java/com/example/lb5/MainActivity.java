package com.example.lb5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements OnClickListener {
    final String LOG_TAG = "myLogs";
    Button btnAdd, btnRead, btnClear, btnSort;
    EditText editFirstName,editSecondName, editEmail, editAdress;
    RadioGroup rgSort;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnSort = (Button) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);

        rgSort = (RadioGroup) findViewById(R.id.rgSort);

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editSecondName = (EditText) findViewById(R.id.editSecondName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editAdress = (EditText) findViewById(R.id.editAdress);
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {
        ContentValues cv = new ContentValues();
        String firstname = editFirstName.getText().toString();
        String secondname = editSecondName.getText().toString();
        String email = editEmail.getText().toString();
        String adress = editAdress.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String orderBy = null;
        Cursor c = null;
        switch (v.getId()) {
            case R.id.btnAdd:
                Log.d(LOG_TAG, "----Insert in my table -----");
                cv.put("firstname", firstname);
                cv.put("secondname", secondname);
                cv.put("email", email);
                cv.put("adress", adress);
                long rowID = db.insert("mytable", null, cv);
                Log.d(LOG_TAG, "row inserted, id=" + rowID);
                break;
            case R.id.btnRead:
                Log.d(LOG_TAG, "----Rows in my table-----");
                c = db.query("mytable", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    int idColIndex = c.getColumnIndex("id");
                    int firstnameColIndex = c.getColumnIndex("firstname");
                    int secondnameColIndex = c.getColumnIndex("secondname");
                    int emailColIndex = c.getColumnIndex("email");
                    int adressColIndex = c.getColumnIndex("adress");
                    do {
                        Log.d(LOG_TAG,
                                "ID = " + c.getInt(idColIndex) +
                                        "fistname = " + c.getString(firstnameColIndex) +
                                        "secondname = " + c.getString(secondnameColIndex) +
                                        "email = " + c.getString(emailColIndex) +
                                        "adress = " + c.getString(adressColIndex));
                    } while (c.moveToNext());
                } else
                    Log.d(LOG_TAG, "0 rows");
                c.close();
                break;
            case R.id.btnSort:
                // сортировка
                switch (rgSort.getCheckedRadioButtonId()) {
                    case R.id.rName:
                        Log.d(LOG_TAG, "--- Sort by firstname  ---");
                        orderBy = "firstname";
                        break;
                    case R.id.rThirdname:
                        Log.d(LOG_TAG, "--- Sort by secondname ---");
                        orderBy = "secondname";
                        break;
                    case R.id.rEmail:
                        Log.d(LOG_TAG, "--- Sort by email ---");
                        orderBy = "email";
                        break;
                }
                c = db.query("mytable", null, null, null, null, null, orderBy);
                break;

                case R.id.btnClear:
                Log.d(LOG_TAG, "----Clear mytable:----");
                int clearCount = db.delete("mytable", null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                break;
        }
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = "
                                + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);

                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "firstname text,"
                    + "secondname text,"
                    + "email text,"
                    + "adress text"+ ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}