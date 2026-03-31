package model;

public class Produktua {
    private int id;
    private String izena;
    private int kategoriaId;
    private String kategoriaIzena;
    private double prezioa;
    private int stocka;

    public Produktua(int id, String izena, String kategoriaIzena, double prezioa, int stocka) {
        this.id = id;
        this.izena = izena;
        this.kategoriaIzena = kategoriaIzena;
        this.prezioa = prezioa;
        this.stocka = stocka;
    }

    public int getId() {
        return id;
    }

    public String getIzena() {
        return izena;
    }

    public void setIzena(String izena) {
        this.izena = izena;
    }

    public String getKategoriaIzena() {
        return kategoriaIzena;
    }

    public void setKategoriaIzena(String kategoriaIzena) {
        this.kategoriaIzena = kategoriaIzena;
    }

    public double getPrezioa() {
        return prezioa;
    }

    public void setPrezioa(double prezioa) {
        this.prezioa = prezioa;
    }

    public int getStocka() {
        return stocka;
    }

    public void setStocka(int stocka) {
        this.stocka = stocka;
    }

    public int getKategoriaId() {
        return kategoriaId;
    }

    public void setKategoriaId(int kategoriaId) {
        this.kategoriaId = kategoriaId;
    }
}
