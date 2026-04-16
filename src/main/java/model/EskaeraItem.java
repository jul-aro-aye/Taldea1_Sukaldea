package model;

public class EskaeraItem {
    private final int id;
    private final String izena;
    private final int kantitatea;
    private final String egoera;

    public EskaeraItem(int id, String izena, int kantitatea, String egoera) {
        this.id = id;
        this.izena = izena;
        this.kantitatea = kantitatea;
        this.egoera = egoera;
    }

    public int getId() {
        return id;
    }

    public String getIzena() {
        return izena;
    }

    public int getKantitatea() {
        return kantitatea;
    }

    public String getEgoera() {
        return egoera;
    }
}
