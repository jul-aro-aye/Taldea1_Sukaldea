package kontrola;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import model.Eskaera;
import model.EskaeraItem;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class KomandaTxartelaFabrika {
    private static final DateTimeFormatter ESKAERA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static VBox sortu(Eskaera eskaera, Map<Integer, String> eskaeraEgoerak, KomandaTxartelaListener listener) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_LEFT);
        card.setSpacing(8);
        card.setPrefWidth(360);
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.getStyleClass().add("txartela");

        Label izenburua = new Label("Eskaera #" + eskaera.getId());
        izenburua.getStyleClass().add("izenburua-komanda");

        String mahaiaText = eskaera.getMahaiaZenbakia() != null ? eskaera.getMahaiaZenbakia().toString() : "—";
        Label mahaiaLbl = new Label("Mahaia: " + mahaiaText);
        mahaiaLbl.getStyleClass().add("mahaia-label-urdina");

        String orduaText = "--:--";
        LocalDateTime sortze = eskaera.getSortzeData();
        if (sortze != null) {
            orduaText = sortze.format(ESKAERA_FMT);
        }
        Label orduaLbl = new Label("Ordua: " + orduaText);
        orduaLbl.getStyleClass().add("ordua-label");

        Label produktuakLbl = new Label("Produktuak:");
        produktuakLbl.getStyleClass().add("produktuak-label");

        VBox produktuakBox = new VBox(2);
        if (eskaera.getItems() == null || eskaera.getItems().isEmpty()) {
            Label empty = new Label("• (produktuik gabe)");
            empty.getStyleClass().add("produktu-label-urdina");
            produktuakBox.getChildren().add(empty);
        } else {
            List<EskaeraItem> items = eskaera.getItems();
            for (int i = items.size() - 1; i >= 0; i--) {
                EskaeraItem item = items.get(i);
                Label p = new Label("• " + item.getIzena() + " x" + item.getKantitatea());
                p.getStyleClass().add("produktu-label-urdina");
                produktuakBox.getChildren().add(p);
            }
        }

        ScrollPane produktuakScroll = new ScrollPane(produktuakBox);
        produktuakScroll.setFitToWidth(true);
        produktuakScroll.setPrefViewportHeight(120);
        produktuakScroll.setMaxHeight(120);
        produktuakScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        produktuakScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        produktuakScroll.getStyleClass().add("produktuak-scroll");

        String egoera = eskaeraEgoerak.getOrDefault(eskaera.getId(), "Zain");
        Label egoeraLbl = new Label("Egoera: " + egoera);
        egoeraLbl.getStyleClass().add("egoera-label");

        Button hasiBtn = new Button("Hasi");
        hasiBtn.getStyleClass().add("hasi-btn");
        hasiBtn.setOnAction(e -> listener.onEgoeraAldatu(eskaera.getId(), "Prestatzen", egoeraLbl));

        Button prestBtn = new Button("Prest");
        prestBtn.getStyleClass().add("prest-btn");
        prestBtn.setOnAction(e -> listener.onEgoeraAldatu(eskaera.getId(), "Prest", egoeraLbl));

        HBox ekintzak = new HBox(8, hasiBtn, prestBtn);
        ekintzak.setAlignment(Pos.CENTER_LEFT);

        card.setOnMouseClicked(e -> listener.onHautatu(eskaera.getId(), card));

        card.getChildren().addAll(izenburua, mahaiaLbl, orduaLbl, produktuakLbl, produktuakScroll, egoeraLbl, ekintzak);
        return card;
    }

    public interface KomandaTxartelaListener {
        void onEgoeraAldatu(int eskaeraId, String egoera, Label egoeraLbl);
        void onHautatu(int eskaeraId, VBox card);
    }
}
