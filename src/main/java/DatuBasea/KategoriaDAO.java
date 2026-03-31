package DatuBasea;

import model.Kategoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Util.Conn;
import java.sql.*;

public class KategoriaDAO {

    public ObservableList<Kategoria> kargatuKategoriak() {
        ObservableList<Kategoria> items = FXCollections.observableArrayList();
        String sql = "SELECT id, izena FROM kategoriak ORDER BY izena";
        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Kategoria(rs.getInt("id"), rs.getString("izena")));
            }
        } catch (SQLException e) {
            System.err.println("Errorea kategoriak kargatzean: " + e.getMessage());
        }
        return items;
    }
}
