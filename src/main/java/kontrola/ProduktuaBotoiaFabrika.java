package kontrola;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import model.Produktua;

public class ProduktuaBotoiaFabrika {
    public interface ProduktuaBotoiaListener {
        void onProduktuaHautatu(Produktua produktua, VBox vBox);
        void onProduktuaEditatu(Produktua produktua);
        void onStockAldatu(Produktua produktua, int aldaketa);
    }

    public static VBox sortu(Produktua produktua, ProduktuaBotoiaListener listener) {
        VBox vBox = new VBox();
        vBox.setPrefSize(220, 200);
        vBox.setSpacing(8);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getStyleClass().add("tpv-txartela");

        Label izena = new Label(produktua.getIzena());
        izena.getStyleClass().add("tpv-izena");
        izena.setMaxWidth(195);

        Label kategoria = new Label(produktua.getKategoriaIzena());
        kategoria.getStyleClass().add("tpv-kategoria");

        Label prezioa = new Label(String.format("%.2f€", produktua.getPrezioa()));
        prezioa.getStyleClass().add("tpv-prezioa");

        Label stockLabel = new Label("Stock: " + produktua.getStocka());
        stockLabel.getStyleClass().add("tpv-stocka");
        stockLabel.setPrefWidth(100);

        HBox botoiak = new HBox(10);
        botoiak.setAlignment(Pos.CENTER);

        Button kenduBtn = new Button("−");
        kenduBtn.setPrefSize(50, 35);
        kenduBtn.getStyleClass().add("tpv-kendu-btn");
        kenduBtn.setOnAction(e -> {
            e.consume();
            listener.onStockAldatu(produktua, -1);
        });

        Button gehituBtn = new Button("+");
        gehituBtn.setPrefSize(50, 35);
        gehituBtn.getStyleClass().add("tpv-gehitu-btn");
        gehituBtn.setOnAction(e -> {
            e.consume();
            listener.onStockAldatu(produktua, 1);
        });

        botoiak.getChildren().addAll(kenduBtn, gehituBtn);

        vBox.getChildren().addAll(izena, kategoria, prezioa, stockLabel, botoiak);

        vBox.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                listener.onProduktuaEditatu(produktua);
            } else if (e.getClickCount() == 1) {
                listener.onProduktuaHautatu(produktua, vBox);
            }
        });

        return vBox;
    }
}
