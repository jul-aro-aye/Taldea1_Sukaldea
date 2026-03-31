
package kontrola;

import DatuBasea.LoginManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Erabiltzailea;

public class LoginController {
    @FXML
    private TextField txtErabiltzailea;
    @FXML
    private PasswordField txtPasahitza;

    @FXML
    private void saioaHasi() {
        String erabiltzailea = txtErabiltzailea.getText().trim();
        String pasahitza = txtPasahitza.getText().trim();

        if (erabiltzailea.isEmpty() || pasahitza.isEmpty()) {
            alertaErakutsi("Errorea", "Erabiltzailea eta pasahitza sartu behar dituzu", Alert.AlertType.ERROR);
            return;
        }

        Erabiltzailea user = new Erabiltzailea(erabiltzailea, pasahitza);
        LoginManager loginManager = new LoginManager();

        if (loginManager.login(user)) {
            alertaErakutsi("Saioa ondo hasi da", "Ongi etorri, " + erabiltzailea + "!", Alert.AlertType.INFORMATION);
            leihoNagusiaIreki();
        } else {
            alertaErakutsi("Errorea", loginManager.getAzkenErrorea(), Alert.AlertType.ERROR);
        }
    }

    private void alertaErakutsi(String izenburua, String mezua, Alert.AlertType mota) {
        Alert alert = new Alert(mota);
        alert.setTitle(izenburua);
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }

    private void leihoNagusiaIreki() {
        try {
            // Saioa hasi duen erabiltzailearen izena kontrolatzaile nagusian gorde
            LehioNagusiaController.erabiltzaileIzena = txtErabiltzailea.getText().trim();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sukaldea/fxml/LehioNagusia.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/org/example/sukaldea/css/estilos.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("JAUS - Leiho Nagusia");
            stage.setScene(scene);

            stage.setMaximized(true);
            stage.setResizable(false);

            stage.show();

            Stage loginStage = (Stage) txtErabiltzailea.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            System.err.println("Error irekitzen leiho nagusia: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
