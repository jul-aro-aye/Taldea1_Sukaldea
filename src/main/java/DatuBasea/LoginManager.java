package DatuBasea;

import Util.Conn;
import model.Erabiltzailea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManager {

    // 'sukalde' rola rola_id = 3 dela suposatzen da
    private static final int SUKALDE_ROLA_ID = 3;
    private String azkenErrorea = "Ezin izan da saioa hasi.";

    public String getAzkenErrorea() {
        return azkenErrorea;
    }

    public boolean login(Erabiltzailea user) {
        String query = "SELECT id, erabiltzailea, pasahitza, rola_id, ezabatua FROM erabiltzaileak WHERE erabiltzailea = ?";

        try (Connection conn = Conn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getErabiltzailea());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("ezabatua")) {
                        azkenErrorea = "Erabiltzaile hau ezabatuta dago eta ezin da saioa hasi.";
                        System.out.println(azkenErrorea);
                        return false;
                    }

                    if (!rs.getString("pasahitza").equals(user.getPasahitza())) {
                        azkenErrorea = "Pasahitza ez da zuzena.";
                        System.out.println(azkenErrorea);
                        return false;
                    }

                    int rolaId = rs.getInt("rola_id");
                    if (rolaId == SUKALDE_ROLA_ID) {
                        user.setId(rs.getInt("id"));
                        user.setRolaId(rolaId);
                        azkenErrorea = "";
                        System.out.println("Login arrakastatsua: " + user.getErabiltzailea());
                        return true;
                    } else {
                        azkenErrorea = "Erabiltzaileak ez du sukaldeko baimenik. Behar den rola: " + SUKALDE_ROLA_ID;
                        System.out.println(azkenErrorea);
                    }
                } else {
                    azkenErrorea = "Erabiltzailea ez da existitzen.";
                    System.out.println(azkenErrorea);
                }
            }
        } catch (SQLException e) {
            azkenErrorea = "Errorea datu-basearekin konektatzean edo kontsulta egitean: " + e.getMessage();
            System.err.println("Login kontsultan errorea: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
