package kontrola;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Eskaera;
import model.EskaeraItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KomandaTxartelaFabrika {
    private static final DateTimeFormatter ESKAERA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static VBox sortu(Eskaera eskaera, KomandaTxartelaListener listener) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_LEFT);
        card.setSpacing(8);
        card.setPrefWidth(360);
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.getStyleClass().add("txartela");

        Label izenburua = new Label("Eskaera #" + eskaera.getId());
        izenburua.getStyleClass().add("izenburua-komanda");

        String mahaiaText = eskaera.getMahaiaZenbakia() != null ? eskaera.getMahaiaZenbakia().toString() : "-";
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

        VBox produktuakBox = new VBox(6);
        if (eskaera.getItems() == null || eskaera.getItems().isEmpty()) {
            Label empty = new Label("- (produktuik gabe)");
            empty.getStyleClass().add("produktu-label-urdina");
            produktuakBox.getChildren().add(empty);
        } else {
            List<EskaeraItem> items = eskaera.getItems();
            for (int i = items.size() - 1; i >= 0; i--) {
                EskaeraItem item = items.get(i);

                Label itemLabel = new Label("- " + item.getIzena() + " x" + item.getKantitatea());
                itemLabel.getStyleClass().add("produktu-label-urdina");

                Label itemEgoeraLbl = new Label(formatEgoera(item.getEgoera()));
                itemEgoeraLbl.getStyleClass().add("item-egoera-label");
                itemEgoeraLbl.getStyleClass().add(styleClassForEgoera(item.getEgoera()));

                HBox itemGoiburua = new HBox(8, itemLabel, itemEgoeraLbl);
                itemGoiburua.setAlignment(Pos.CENTER_LEFT);

                Button bidalitaBtn = new Button("Bidalita");
                bidalitaBtn.getStyleClass().add("bidalita-btn");
                bidalitaBtn.setOnAction(e -> listener.onProduktuaEgoeraAldatu(eskaera.getId(), item.getId(), "bidalita"));

                Button egitenBtn = new Button("Egiten");
                egitenBtn.getStyleClass().add("hasi-btn");
                egitenBtn.setOnAction(e -> listener.onProduktuaEgoeraAldatu(eskaera.getId(), item.getId(), "egiten"));

                Button egindaBtn = new Button("Eginda");
                egindaBtn.getStyleClass().add("prest-btn");
                egindaBtn.setOnAction(e -> listener.onProduktuaEgoeraAldatu(eskaera.getId(), item.getId(), "eginda"));

                HBox ekintzak = new HBox(8, bidalitaBtn, egitenBtn, egindaBtn);
                ekintzak.setAlignment(Pos.CENTER_LEFT);

                VBox itemBox = new VBox(4, itemGoiburua, ekintzak);
                itemBox.getStyleClass().add("eskaera-item-box");
                produktuakBox.getChildren().add(itemBox);
            }
        }

        ScrollPane produktuakScroll = new ScrollPane(produktuakBox);
        produktuakScroll.setFitToWidth(true);
        produktuakScroll.setPrefViewportHeight(180);
        produktuakScroll.setMaxHeight(180);
        produktuakScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        produktuakScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        produktuakScroll.getStyleClass().add("produktuak-scroll");

        Label egoeraLbl = new Label("Egoera eskaera: " + formatEgoera(eskaera.getSukaldeaEgoera()));
        egoeraLbl.getStyleClass().add("egoera-label");
        egoeraLbl.getStyleClass().add(styleClassForEgoera(eskaera.getSukaldeaEgoera()));

        card.setOnMouseClicked(e -> listener.onHautatu(eskaera.getId(), card));
        card.getChildren().addAll(izenburua, mahaiaLbl, orduaLbl, produktuakLbl, produktuakScroll, egoeraLbl);
        return card;
    }

    private static String formatEgoera(String egoera) {
        if (egoera == null) {
            return "Bidalita";
        }
        return switch (egoera) {
            case "bidalita" -> "Bidalita";
            case "egiten" -> "Egiten";
            case "eginda" -> "Eginda";
            default -> egoera;
        };
    }

    private static String styleClassForEgoera(String egoera) {
        if ("egiten".equals(egoera)) {
            return "egoera-label-prestatzen";
        }
        if ("eginda".equals(egoera)) {
            return "egoera-label-prest";
        }
        return "egoera-label";
    }

    public interface KomandaTxartelaListener {
        void onProduktuaEgoeraAldatu(int eskaeraId, int eskaeraProduktuaId, String egoera);
        void onHautatu(int eskaeraId, VBox card);
    }
}
