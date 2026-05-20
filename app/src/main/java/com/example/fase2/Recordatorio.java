package com.example.fase2;

public class Recordatorio {
    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_COMPLETADO = "completado";

    private long id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String hora;
    private String categoria;
    private String prioridad;
    private String estado;
    private String latitud;
    private String longitud;
    private String ubicacion;
    private String adjuntoRuta;
    private String adjuntoNombre;

    public Recordatorio() {
        estado = ESTADO_PENDIENTE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getAdjuntoRuta() {
        return adjuntoRuta;
    }

    public void setAdjuntoRuta(String adjuntoRuta) {
        this.adjuntoRuta = adjuntoRuta;
    }

    public String getAdjuntoNombre() {
        return adjuntoNombre;
    }

    public void setAdjuntoNombre(String adjuntoNombre) {
        this.adjuntoNombre = adjuntoNombre;
    }
}
