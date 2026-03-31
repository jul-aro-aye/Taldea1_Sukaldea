package DatuBasea;

import model.Produktua;
import model.Kategoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Util.Conn;
import java.sql.*;

public class ProduktuaDAO {

    public ObservableList<Produktua> kargatuProduktuak() {
        ObservableList<Produktua> produktuak = FXCollections.observableArrayList();
        String sql = "SELECT p.id, p.izena, p.prezioa, p.stock_aktuala, k.izena AS kategoria_izena FROM produktuak p JOIN kategoriak k ON p.kategoria_id = k.id ORDER BY p.id DESC LIMIT 200";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                produktuak.add(new Produktua(
                        rs.getInt("id"),
                        rs.getString("izena"),
                        rs.getString("kategoria_izena"),
                        rs.getDouble("prezioa"),
                        rs.getInt("stock_aktuala")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Errorea produktuak kargatzean: " + e.getMessage());
        }
        return produktuak;
    }

    public void gehituProduktua(String izena, Kategoria kat, double prezioa, int stock) {
        String sql = "INSERT INTO produktuak (izena, kategoria_id, prezioa, stock_aktuala) VALUES (?,?,?,?)";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, izena);
            ps.setInt(2, kat.getId());
            ps.setDouble(3, prezioa);
            ps.setInt(4, stock);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Errorea produktua gehitzean: " + e.getMessage());
        }
    }

    public void eguneratuProduktua(int id, String izena, Kategoria kat) {
        String sql = "UPDATE produktuak SET izena=?, kategoria_id=? WHERE id=?";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, izena);
            ps.setInt(2, kat.getId());
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Errorea produktua eguneratzean: " + e.getMessage());
        }
    }

    public void eguneratuStocka(int id, int stocka) {
        String sql = "UPDATE produktuak SET stock_aktuala=? WHERE id=?";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stocka);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Errorea stock eguneratzean: " + e.getMessage());
        }
    }

    public void ezabatuProduktua(int id) {
        String sql = "DELETE FROM produktuak WHERE id=?";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Errorea produktua ezabatzean: " + e.getMessage());
        }
    }
}
