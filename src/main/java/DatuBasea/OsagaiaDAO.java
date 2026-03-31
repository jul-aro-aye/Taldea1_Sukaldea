package DatuBasea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Osagaia;
import model.ProduktuOsagaia;
import Util.Conn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OsagaiaDAO {

    // Osagaia guztiak kargatu eskuragarri
    public ObservableList<Osagaia> kargatuOsagaiak() {
        ObservableList<Osagaia> osagaiak = FXCollections.observableArrayList();
        String query = "SELECT id, izena, unitatea, stock_aktuala FROM osagaiak ORDER BY izena";

        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Osagaia osagaia = new Osagaia(
                        rs.getInt("id"),
                        rs.getString("izena"),
                        rs.getString("unitatea"),
                        rs.getDouble("stock_aktuala")
                );
                osagaiak.add(osagaia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return osagaiak;
    }

    // Produktu baten osagaiak kargatu
    public ObservableList<ProduktuOsagaia> kargatuProduktuarenOsagaiak(int produktuaId) {
        ObservableList<ProduktuOsagaia> osagaiak = FXCollections.observableArrayList();
        String query = "SELECT po.produktua_id, po.osagaia_id, o.izena, po.kantitatea, po.unitatea " +
                       "FROM produktu_osagaiak po " +
                       "INNER JOIN osagaiak o ON po.osagaia_id = o.id " +
                       "WHERE po.produktua_id = ?";

        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, produktuaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProduktuOsagaia po = new ProduktuOsagaia(
                        rs.getInt("produktua_id"),
                        rs.getInt("osagaia_id"),
                        rs.getString("izena"),
                        rs.getDouble("kantitatea"),
                        rs.getString("unitatea")
                );
                osagaiak.add(po);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return osagaiak;
    }

    // Osagaia produktuari gehitu
    public void gehituOsagaiaProduktuari(int produktuaId, int osagaiaId, double kantitatea, String unitatea) {
        String query = "INSERT INTO produktu_osagaiak (produktua_id, osagaia_id, kantitatea, unitatea) " +
                       "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, produktuaId);
            stmt.setInt(2, osagaiaId);
            stmt.setDouble(3, kantitatea);
            stmt.setString(4, unitatea);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Osagaia produktutik kendu
    public void kenduOsagaiaProduktuatik(int produktuaId, int osagaiaId) {
        String query = "DELETE FROM produktu_osagaiak WHERE produktua_id = ? AND osagaia_id = ?";

        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, produktuaId);
            stmt.setInt(2, osagaiaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Osagaiaren kantitatea eguneratu produktuan
    public void eguneratuOsagaiaKantitatea(int produktuaId, int osagaiaId, double kantitatea) {
        String query = "UPDATE produktu_osagaiak SET kantitatea = ? WHERE produktua_id = ? AND osagaia_id = ?";

        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, kantitatea);
            stmt.setInt(2, produktuaId);
            stmt.setInt(3, osagaiaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Stock-a eguneratu osagai batean
    public void eguneratuOsagaiaStocka(int osagaiaId, double berria) {
        String query = "UPDATE osagaiak SET stock_aktuala = ? WHERE id = ?";
        try (Connection conn = Conn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, berria);
            stmt.setInt(2, osagaiaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
