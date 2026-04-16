package DatuBasea;

import Util.Conn;
import model.Eskaera;
import model.EskaeraItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EskaeraDAO {
    private static final String SUKALDEA_BIDALITA = "bidalita";
    private static final String SUKALDEA_EGITEN = "egiten";
    private static final String SUKALDEA_EGINDA = "eginda";

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
        String sql = "SELECT ep.id, p.izena, ep.kantitatea, ep.egoera " +
                "FROM eskaera_produktuak ep " +
                "JOIN produktuak p ON ep.produktua_id = p.id " +
                "WHERE ep.eskaera_id = ? " +
                "ORDER BY ep.id DESC";

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, eskaeraId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new EskaeraItem(
                            rs.getInt("id"),
                            rs.getString("izena"),
                            rs.getInt("kantitatea"),
                            rs.getString("egoera")
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

    public String eguneratuEskaeraProduktuaEgoera(int eskaeraProduktuaId, String egoera) {
        String lortuEskaeraSql = "SELECT eskaera_id FROM eskaera_produktuak WHERE id = ?";
        String eguneratuProduktuaSql = "UPDATE eskaera_produktuak SET egoera = ? WHERE id = ?";

        try (Connection c = Conn.getConnection()) {
            c.setAutoCommit(false);

            Integer eskaeraId = null;
            try (PreparedStatement ps = c.prepareStatement(lortuEskaeraSql)) {
                ps.setInt(1, eskaeraProduktuaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        eskaeraId = rs.getInt("eskaera_id");
                    }
                }
            }

            if (eskaeraId == null) {
                c.rollback();
                return null;
            }

            try (PreparedStatement ps = c.prepareStatement(eguneratuProduktuaSql)) {
                ps.setString(1, egoera);
                ps.setInt(2, eskaeraProduktuaId);
                ps.executeUpdate();
            }

            String eskaeraEgoera = berrkalkulatuEskaerarenSukaldeaEgoera(c, eskaeraId);
            c.commit();
            return eskaeraEgoera;
        } catch (SQLException e) {
            System.err.println("Errorea eskaerako produktuaren egoera eguneratzean: " + e.getMessage());
            return null;
        }
    }

    private String berrkalkulatuEskaerarenSukaldeaEgoera(Connection c, int eskaeraId) throws SQLException {
        String egoerakSql = "SELECT egoera FROM eskaera_produktuak WHERE eskaera_id = ?";
        String updateSql = "UPDATE eskaerak SET sukaldea_egoera = ? WHERE id = ?";

        boolean badagoProdukturik = false;
        boolean guztiakEginda = true;
        boolean badagoEgiten = false;

        try (PreparedStatement ps = c.prepareStatement(egoerakSql)) {
            ps.setInt(1, eskaeraId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    badagoProdukturik = true;
                    String unekoEgoera = rs.getString("egoera");
                    if (!SUKALDEA_EGINDA.equals(unekoEgoera)) {
                        guztiakEginda = false;
                    }
                    if (SUKALDEA_EGITEN.equals(unekoEgoera)) {
                        badagoEgiten = true;
                    }
                }
            }
        }

        String eskaeraEgoera;
        if (badagoProdukturik && guztiakEginda) {
            eskaeraEgoera = SUKALDEA_EGINDA;
        } else if (badagoEgiten) {
            eskaeraEgoera = SUKALDEA_EGITEN;
        } else {
            eskaeraEgoera = SUKALDEA_BIDALITA;
        }

        try (PreparedStatement ps = c.prepareStatement(updateSql)) {
            ps.setString(1, eskaeraEgoera);
            ps.setInt(2, eskaeraId);
            ps.executeUpdate();
        }

        return eskaeraEgoera;
    }
}
