package model;

public class ProduktuOsagaia {
    private int produktuaId;
    private int osagaiaId;
    private String osagaiaIzena;
    private double kantitatea;
    private String unitatea;

    public ProduktuOsagaia(int produktuaId, int osagaiaId, String osagaiaIzena, double kantitatea, String unitatea) {
        this.produktuaId = produktuaId;
        this.osagaiaId = osagaiaId;
        this.osagaiaIzena = osagaiaIzena;
        this.kantitatea = kantitatea;
        this.unitatea = unitatea;
    }

    public int getProduktuaId() {
        return produktuaId;
    }

    public void setProduktuaId(int produktuaId) {
        this.produktuaId = produktuaId;
    }

    public int getOsagaiaId() {
        return osagaiaId;
    }

    public void setOsagaiaId(int osagaiaId) {
        this.osagaiaId = osagaiaId;
    }

    public String getOsagaiaIzena() {
        return osagaiaIzena;
    }

    public void setOsagaiaIzena(String osagaiaIzena) {
        this.osagaiaIzena = osagaiaIzena;
    }

    public double getKantitatea() {
        return kantitatea;
    }

    public void setKantitatea(double kantitatea) {
        this.kantitatea = kantitatea;
    }

    public String getUnitatea() {
        return unitatea;
    }

    public void setUnitatea(String unitatea) {
        this.unitatea = unitatea;
    }
}
