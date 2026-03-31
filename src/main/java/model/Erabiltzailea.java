package model;

public class Erabiltzailea {
    private int id;
    private String erabiltzailea;
    private String pasahitza;
    private String email;
    private int rolaId;
    private boolean ezabatua;
    private boolean chat;

    public Erabiltzailea(String erabiltzailea, String pasahitza) {
        this.erabiltzailea = erabiltzailea;
        this.pasahitza = pasahitza;
    }

    public Erabiltzailea(int id, String erabiltzailea, String email, String pasahitza, int rolaId) {
        this.id = id;
        this.erabiltzailea = erabiltzailea;
        this.email = email;
        this.pasahitza = pasahitza;
        this.rolaId = rolaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getErabiltzailea() {
        return erabiltzailea;
    }

    public void setErabiltzailea(String erabiltzailea) {
        this.erabiltzailea = erabiltzailea;
    }

    public String getPasahitza() {
        return pasahitza;
    }

    public void setPasahitza(String pasahitza) {
        this.pasahitza = pasahitza;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRolaId() {
        return rolaId;
    }

    public void setRolaId(int rolaId) {
        this.rolaId = rolaId;
    }

    public boolean isEzabatua() {
        return ezabatua;
    }

    public void setEzabatua(boolean ezabatua) {
        this.ezabatua = ezabatua;
    }

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }
}
