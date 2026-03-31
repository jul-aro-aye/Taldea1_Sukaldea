package DatuBasea;

import Util.Conn;
import model.Eskaera;
import model.EskaeraItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EskaeraDAO {

    public List<Eskaera> kargatuEskaerak() {
        List<Eskaera> eskaerak = new ArrayList<>();
        String sql = "SELECT e.id, m.zenbakia AS mahaia_zenb, e.sortze_data, e.sukaldea_egoera " +
                 "FROM eskaerak e " +
                 "LEFT JOIN mahaiak m ON e.mahaia_id = m.id " +
                 "WHERE e.egoera <> 'itxita' " +
                 "ORDER BY e.sortze_data DESC LIMIT 50";

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Integer mahaiaZenb = rs.getObject("mahaia_zenb") != null ? rs.getInt("mahaia_zenb") : null;
                Timestamp ts = rs.getTimestamp("sortze_data");
                LocalDateTime sortzeData = ts != null ? ts.toLocalDateTime() : null;
                String sukaldeaEgoera = rs.getString("sukaldea_egoera");
                List<EskaeraItem> items = kargatuEskaerarenProduktua(id);
                eskaerak.add(new Eskaera(id, mahaiaZenb, sortzeData, items, sukaldeaEgoera));
            }
        } catch (SQLException e) {
            System.err.println("Errorea eskaerak kargatzean: " + e.getMessage());
        }
        return eskaerak;
    }

    public List<EskaeraItem> kargatuEskaerarenProduktua(int eskaeraId) {
        List<EskaeraItem> items = new ArrayList<>();
        String sql = "SELECT p.izena, ep.kantitatea " +
                     "FROM eskaera_produktuak ep " +
                     "JOIN produktuak p ON ep.produktua_id = p.id " +
                     "WHERE ep.eskaera_id = ?";

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, eskaeraId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new EskaeraItem(
                            rs.getString("izena"),
                            rs.getInt("kantitatea")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errorea eskaeraren produktuak kargatzean: " + e.getMessage());
        }
        return items;
    }

    public int lortuAzkenEskaeraId() {
        String sql = "SELECT id FROM eskaerak ORDER BY id DESC LIMIT 1";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Errorea azken eskaera lortzean: " + e.getMessage());
        }
        return 0;
    }

    public void ezabatuEskaera(int eskaeraId) {
        String selectMahaiaSql = "SELECT mahaia_id FROM eskaerak WHERE id = ?";
        String updateMahaiaSql = "UPDATE mahaiak SET egoera = 'libre' WHERE id = ?";
        String deleteEskaeraSql = "DELETE FROM eskaerak WHERE id = ?";

        try (Connection c = Conn.getConnection()) {
            c.setAutoCommit(false);

            Integer mahaiaId = null;
            try (PreparedStatement ps = c.prepareStatement(selectMahaiaSql)) {
                ps.setInt(1, eskaeraId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("mahaia_id");
                        if (!rs.wasNull()) {
                            mahaiaId = id;
                        }
                    }
                }
            }

            if (mahaiaId != null) {
                try (PreparedStatement ps = c.prepareStatement(updateMahaiaSql)) {
                    ps.setInt(1, mahaiaId);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = c.prepareStatement(deleteEskaeraSql)) {
                ps.setInt(1, eskaeraId);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            System.err.println("Errorea eskaera ezabatzean: " + e.getMessage());
        }
    }

    public void eguneratuSukaldeaEgoera(int eskaeraId, String sukaldeaEgoera) {
        String sql = "UPDATE eskaerak SET sukaldea_egoera = ? WHERE id = ?";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sukaldeaEgoera);
            ps.setInt(2, eskaeraId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errorea sukaldea_egoera eguneratzean: " + e.getMessage());
        }
    }

}
