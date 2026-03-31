package model;

public class EskaeraItem {
    private final String izena;
    private final int kantitatea;

    public EskaeraItem(String izena, int kantitatea) {
        this.izena = izena;
        this.kantitatea = kantitatea;
    }

    public String getIzena() {
        return izena;
    }

    public int getKantitatea() {
        return kantitatea;
    }
}
