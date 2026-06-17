package com.example.to_dolist;

/*
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tareas.db";
    private static final int DB_VERSION = 2; // Cambiado a 2 por la nueva columna fecha
    private static final String TABLE_NAME = "tareas";
    private static final String COL_ID = "id";
    private static final String COL_TITULO = "titulo";
    private static final String COL_DESCRIPCION = "descripcion";
    private static final String COL_ESTADO = "estado";
    private static final String COL_FECHA = "fecha";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITULO + " TEXT NOT NULL, " +
                COL_DESCRIPCION + " TEXT, " +
                COL_ESTADO + " INTEGER DEFAULT 0, " +
                COL_FECHA + " TEXT DEFAULT '')";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // INSERT
    public long insertar(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, task.getTitulo());
        values.put(COL_DESCRIPCION, task.getDescripcion());
        values.put(COL_ESTADO, task.getEstado());
        values.put(COL_FECHA, task.getFecha());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // INSERT
    public long insertarTask(String titulo, String descripcion, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, titulo);
        values.put(COL_DESCRIPCION, descripcion);
        values.put(COL_ESTADO, 0);
        values.put(COL_FECHA, fecha);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // READ
    public List<Task> obtenerTareas() {
        List<Task> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ESTADO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA))
                );
                lista.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // READ
    public Task obtenerTaskPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(id)});

        Task task = null;
        if (cursor.moveToFirst()) {
            task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ESTADO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA))
            );
        }
        cursor.close();
        db.close();
        return task;
    }

    // DELETE
    public void eliminar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // UPDATE
    public void actualizarEstado(int id, int estado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO, estado);
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // UPDATE
    public int actualizarTask(int id, String titulo, String descripcion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, titulo);
        values.put(COL_DESCRIPCION, descripcion);
        int resultado = db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado;
    }

    // UPDATE
    public void actualizar(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, task.getTitulo());
        values.put(COL_DESCRIPCION, task.getDescripcion());
        values.put(COL_ESTADO, task.getEstado());
        values.put(COL_FECHA, task.getFecha());
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
    }
}
*/
