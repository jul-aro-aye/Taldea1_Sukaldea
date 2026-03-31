package model;

public class Osagaia {
    private int id;
    private String izena;
    private String unitatea;
    private double stockAktuala;

    public Osagaia(int id, String izena, String unitatea, double stockAktuala) {
        this.id = id;
        this.izena = izena;
        this.unitatea = unitatea;
        this.stockAktuala = stockAktuala;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIzena() {
        return izena;
    }

    public void setIzena(String izena) {
        this.izena = izena;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }

    public double getStockAktuala() {
        return stockAktuala;
    }

    public void setStockAktuala(double stockAktuala) {
        this.stockAktuala = stockAktuala;
    }

    @Override
    public String toString() {
        return izena;
    }
}
