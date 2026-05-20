package com.example.fase2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordatorioDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "semeolvidano.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_RECORDATORIOS = "recordatorios";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_FECHA = "fecha";
    public static final String COL_HORA = "hora";
    public static final String COL_CATEGORIA = "categoria";
    public static final String COL_PRIORIDAD = "prioridad";
    public static final String COL_ESTADO = "estado";
    public static final String COL_LATITUD = "latitud";
    public static final String COL_LONGITUD = "longitud";
    public static final String COL_UBICACION = "ubicacion";
    public static final String COL_ADJUNTO_RUTA = "adjunto_ruta";
    public static final String COL_ADJUNTO_NOMBRE = "adjunto_nombre";

    public RecordatorioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RECORDATORIOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITULO + " TEXT NOT NULL, " +
                COL_DESCRIPCION + " TEXT, " +
                COL_FECHA + " TEXT NOT NULL, " +
                COL_HORA + " TEXT NOT NULL, " +
                COL_CATEGORIA + " TEXT, " +
                COL_PRIORIDAD + " TEXT, " +
                COL_ESTADO + " TEXT NOT NULL, " +
                COL_LATITUD + " TEXT, " +
                COL_LONGITUD + " TEXT, " +
                COL_UBICACION + " TEXT, " +
                COL_ADJUNTO_RUTA + " TEXT, " +
                COL_ADJUNTO_NOMBRE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_RECORDATORIOS + " ADD COLUMN " + COL_UBICACION + " TEXT");
        }
    }

    public long crearRecordatorio(Recordatorio recordatorio) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_RECORDATORIOS, null, toContentValues(recordatorio));
    }

    public int actualizarRecordatorio(Recordatorio recordatorio) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_RECORDATORIOS, toContentValues(recordatorio), COL_ID + " = ?",
                new String[]{String.valueOf(recordatorio.getId())});
    }

    public int eliminarRecordatorio(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_RECORDATORIOS, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int marcarCompletado(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ESTADO, Recordatorio.ESTADO_COMPLETADO);
        return db.update(TABLE_RECORDATORIOS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Recordatorio obtenerPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_RECORDATORIOS, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    public List<Recordatorio> listarTodos() {
        return listar(null, null, COL_FECHA + " ASC, " + COL_HORA + " ASC");
    }

    public List<Recordatorio> listarPorFecha(String fecha) {
        return listar(COL_FECHA + " = ?", new String[]{fecha}, COL_HORA + " ASC");
    }

    public List<Recordatorio> listarPendientesDeHoy() {
        String hoy = fechaHoy();
        return listar(COL_ESTADO + " = ? AND " + COL_FECHA + " = ?",
                new String[]{Recordatorio.ESTADO_PENDIENTE, hoy}, COL_HORA + " ASC");
    }

    public List<Recordatorio> listarProximosPendientes() {
        String hoy = fechaHoy();
        return listar(COL_ESTADO + " = ? AND " + COL_FECHA + " > ?",
                new String[]{Recordatorio.ESTADO_PENDIENTE, hoy},
                COL_FECHA + " ASC, " + COL_HORA + " ASC");
    }

    public List<Recordatorio> listarVencidos() {
        String hoy = fechaHoy();
        return listar(COL_ESTADO + " = ? AND " + COL_FECHA + " < ?",
                new String[]{Recordatorio.ESTADO_PENDIENTE, hoy},
                COL_FECHA + " ASC, " + COL_HORA + " ASC");
    }

    public List<Recordatorio> listarCompletados() {
        return listar(COL_ESTADO + " = ?", new String[]{Recordatorio.ESTADO_COMPLETADO},
                COL_FECHA + " DESC, " + COL_HORA + " DESC");
    }

    public int contarVencidos() {
        return contar(COL_ESTADO + " = ? AND " + COL_FECHA + " < ?",
                new String[]{Recordatorio.ESTADO_PENDIENTE, fechaHoy()});
    }

    public int contarCompletados() {
        return contar(COL_ESTADO + " = ?", new String[]{Recordatorio.ESTADO_COMPLETADO});
    }

    private List<Recordatorio> listar(String selection, String[] selectionArgs, String orderBy) {
        List<Recordatorio> recordatorios = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_RECORDATORIOS, null, selection, selectionArgs, null, null, orderBy)) {
            while (cursor.moveToNext()) {
                recordatorios.add(fromCursor(cursor));
            }
        }
        return recordatorios;
    }

    private int contar(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_RECORDATORIOS, new String[]{"COUNT(*)"}, selection,
                selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private ContentValues toContentValues(Recordatorio recordatorio) {
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, recordatorio.getTitulo());
        values.put(COL_DESCRIPCION, recordatorio.getDescripcion());
        values.put(COL_FECHA, recordatorio.getFecha());
        values.put(COL_HORA, recordatorio.getHora());
        values.put(COL_CATEGORIA, recordatorio.getCategoria());
        values.put(COL_PRIORIDAD, recordatorio.getPrioridad());
        values.put(COL_ESTADO, recordatorio.getEstado());
        values.put(COL_LATITUD, recordatorio.getLatitud());
        values.put(COL_LONGITUD, recordatorio.getLongitud());
        values.put(COL_UBICACION, recordatorio.getUbicacion());
        values.put(COL_ADJUNTO_RUTA, recordatorio.getAdjuntoRuta());
        values.put(COL_ADJUNTO_NOMBRE, recordatorio.getAdjuntoNombre());
        return values;
    }

    private Recordatorio fromCursor(Cursor cursor) {
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        recordatorio.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO)));
        recordatorio.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION)));
        recordatorio.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA)));
        recordatorio.setHora(cursor.getString(cursor.getColumnIndexOrThrow(COL_HORA)));
        recordatorio.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORIA)));
        recordatorio.setPrioridad(cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORIDAD)));
        recordatorio.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO)));
        recordatorio.setLatitud(cursor.getString(cursor.getColumnIndexOrThrow(COL_LATITUD)));
        recordatorio.setLongitud(cursor.getString(cursor.getColumnIndexOrThrow(COL_LONGITUD)));
        recordatorio.setUbicacion(cursor.getString(cursor.getColumnIndexOrThrow(COL_UBICACION)));
        recordatorio.setAdjuntoRuta(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADJUNTO_RUTA)));
        recordatorio.setAdjuntoNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADJUNTO_NOMBRE)));
        return recordatorio;
    }

    public int limpiarCompletados() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_RECORDATORIOS, COL_ESTADO + " = ?", new String[]{Recordatorio.ESTADO_COMPLETADO});
    }

    private String fechaHoy() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
