package kontrola;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatuBasea.ProduktuaDAO;
import DatuBasea.KategoriaDAO;
import DatuBasea.OsagaiaDAO;
import DatuBasea.EskaeraDAO;
import model.Produktua;
import model.Kategoria;
import model.Osagaia;
import model.ProduktuOsagaia;
import model.Eskaera;
import model.EskaeraItem;

public class LehioNagusiaController {
    // Saioa hasi duen erabiltzailearen izena gordetzeko eremua
    public static String erabiltzaileIzena = "";

    @FXML
    private Label erabiltzaileLabel;

    private ProduktuaDAO produktuaDAO = new ProduktuaDAO();
    private KategoriaDAO kategoriaDAO = new KategoriaDAO();
    private OsagaiaDAO osagaiaDAO = new OsagaiaDAO();
    private EskaeraDAO eskaeraDAO = new EskaeraDAO();

    @FXML
    private FlowPane komandaGrid;

    @FXML
    private FlowPane produktuaGrid;

    @FXML
    private Label orduLabel;

    @FXML
    private ComboBox<Kategoria> kategoriaFiltro;


    private ObservableList<Produktua> produktuak = FXCollections.observableArrayList();
    private Produktua hautatuProduktua = null;
    private Integer hautatuEskaeraId = null;

    private static final DateTimeFormatter ORDU_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter ESKAERA_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String ESTILO_TARJETA_BASE = "-fx-background-color: #ffffff;" +
            "-fx-border-color: #dcdcdc;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);";
    private static final String ESTILO_TARJETA_SELECCIONADA = "-fx-background-color: #e8f4f8;" +
            "-fx-border-color: #3F7AE0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(63,122,224,0.3), 8,0,0,2);" +
            "-fx-padding: 10;";
    private int azkenEskaeraId = 0;
    private final Map<Integer, String> eskaeraEgoerak = new HashMap<>();

    @FXML
    private void initialize() {
                // Si hay usuario logueado, mostrarlo
                if (erabiltzaileLabel != null && erabiltzaileIzena != null && !erabiltzaileIzena.isEmpty()) {
                    erabiltzaileLabel.setText("Saioa hasi du: " + erabiltzaileIzena);
                }
        // Eskaerak kargatu (DB)
        kargatuEskaerak();
        azkenEskaeraId = eskaeraDAO.lortuAzkenEskaeraId();
        Timeline eskaeraTl = new Timeline(new KeyFrame(Duration.seconds(5), e -> eguneratuEskaerak()));
        eskaeraTl.setCycleCount(Timeline.INDEFINITE);
        eskaeraTl.play();

        // Ordua goiburuan (digital)
        if (orduLabel != null) {
            eguneratuOrdua();
            Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> eguneratuOrdua()));
            tl.setCycleCount(Timeline.INDEFINITE);
            tl.play();
        }

        // Kategoriak kargatu filtroan
        if (kategoriaFiltro != null) {
            ObservableList<Kategoria> kategoriak = kategoriaDAO.kargatuKategoriak();
            kategoriaFiltro.setItems(kategoriak);
        }


        // Produktuak kargatu TPV grid-ean
        kargatuProduktuak();
    }

    // Refactor: Usar KomandaTxartelaFabrika para crear tarjetas de pedidos
    private VBox sortuKomandaKarta(Eskaera eskaera) {
        return KomandaTxartelaFabrika.sortu(eskaera, eskaeraEgoerak, new KomandaTxartelaFabrika.KomandaTxartelaListener() {
            @Override
            public void onEgoeraAldatu(int eskaeraId, String egoera, Label egoeraLbl) {
                eguneratuEgoeraSukaldea(eskaeraId, egoera, egoeraLbl);
                if ("Prest".equals(egoera)) {
                    erakutsiNotifikazioa("Eskaera #" + eskaeraId + " prest");
                }
            }
            @Override
            public void onHautatu(int eskaeraId, VBox card) {
                hautatuEskaera(eskaeraId, card);
            }
        });
    }

    private void hautatuEskaera(int eskaeraId, VBox card) {
        if (komandaGrid == null) {
            return;
        }
        for (var child : komandaGrid.getChildren()) {
            if (child instanceof VBox) {
                VBox vbox = (VBox) child;
                vbox.getStyleClass().remove("txartela-hautatua");
                if (!vbox.getStyleClass().contains("txartela")) {
                    vbox.getStyleClass().add("txartela");
                }
            }
        }
        hautatuEskaeraId = eskaeraId;
        card.getStyleClass().remove("txartela");
        if (!card.getStyleClass().contains("txartela-hautatua")) {
            card.getStyleClass().add("txartela-hautatua");
        }
    }

    @FXML
    private void ezabatuEskaera() {
        if (hautatuEskaeraId == null) {
            System.err.println("Ez da eskaerarik hautatu ezabatzeko");
            return;
        }
        eskaeraDAO.ezabatuEskaera(hautatuEskaeraId);
        hautatuEskaeraId = null;
        kargatuEskaerak();
    }

    private void eguneratuOrdua() {
        LocalTime ordua = LocalTime.now();
        if (orduLabel != null) {
            orduLabel.setText(ordua.format(ORDU_FMT));
        }
    }

    private void kargatuEskaerak() {
        if (komandaGrid == null) {
            return;
        }
        List<Eskaera> eskaerak = eskaeraDAO.kargatuEskaerak();
        hautatuEskaeraId = null;
        komandaGrid.getChildren().clear();
        for (Eskaera eskaera : eskaerak) {
            String uiEgoera = mapSukaldeaEgoeraUI(eskaera.getSukaldeaEgoera());
            if (uiEgoera != null) {
                eskaeraEgoerak.put(eskaera.getId(), uiEgoera);
            }
            komandaGrid.getChildren().add(sortuKomandaKarta(eskaera));
        }
    }

    private void eguneratuEskaerak() {
        int azkena = eskaeraDAO.lortuAzkenEskaeraId();
        if (azkena > azkenEskaeraId) {
            erakutsiNotifikazioa("Eskaera berria #" + azkena);
            azkenEskaeraId = azkena;
        }
        kargatuEskaerak();
    }

    private void eguneratuEgoeraSukaldea(int eskaeraId, String egoera, Label egoeraLbl) {
        String dbEgoera = mapSukaldeaEgoeraDB(egoera);
        if (dbEgoera != null) {
            eskaeraDAO.eguneratuSukaldeaEgoera(eskaeraId, dbEgoera);
        }
        eskaeraEgoerak.put(eskaeraId, egoera);
        if (egoeraLbl != null) {
            egoeraLbl.setText("Egoera: " + egoera);
            eguneratuEgoeraEstiloa(egoeraLbl, egoera);
        }
    }

    private String mapSukaldeaEgoeraUI(String dbEgoera) {
        if (dbEgoera == null) {
            return "Zain";
        }
        return switch (dbEgoera) {
            case "zain" -> "Zain";
            case "hasi" -> "Prestatzen";
            case "prest" -> "Prest";
            default -> "Zain";
        };
    }

    private String mapSukaldeaEgoeraDB(String uiEgoera) {
        if (uiEgoera == null) {
            return null;
        }
        return switch (uiEgoera) {
            case "Zain" -> "zain";
            case "Prestatzen" -> "hasi";
            case "Prest" -> "prest";
            default -> null;
        };
    }


    private void eguneratuEgoeraEstiloa(Label egoeraLbl, String egoera) {
        egoeraLbl.getStyleClass().removeAll("egoera-label", "egoera-label-prestatzen", "egoera-label-prest");
        if ("Prestatzen".equals(egoera)) {
            egoeraLbl.getStyleClass().add("egoera-label-prestatzen");
        } else if ("Prest".equals(egoera)) {
            egoeraLbl.getStyleClass().add("egoera-label-prest");
        } else {
            egoeraLbl.getStyleClass().add("egoera-label");
        }
    }

    private void erakutsiNotifikazioa(String mezua) {
        if (komandaGrid == null || komandaGrid.getScene() == null) {
            return;
        }
        Popup popup = new Popup();
        Label label = new Label(mezua);
        label.getStyleClass().add("notifikazio-label");
        label.setMinWidth(Region.USE_PREF_SIZE);
        popup.getContent().add(label);

        Stage stage = (Stage) komandaGrid.getScene().getWindow();
        popup.show(stage);

        Platform.runLater(() -> {
            label.applyCss();
            label.layout();
            double x = stage.getX() + stage.getWidth() - label.getWidth() - 20;
            double y = stage.getY() + 60;
            popup.setX(x);
            popup.setY(y);
        });

        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(3), e -> popup.hide()));
        tl.setCycleCount(1);
        tl.play();
    }

    private void kargatuProduktuak() {
        produktuak.clear();
        produktuak.addAll(produktuaDAO.kargatuProduktuak());
        
        // Filtroa aplikatu kategoria hautatuta badago
        ObservableList<Produktua> produktuakFiltratuta = FXCollections.observableArrayList();
        if (kategoriaFiltro != null && kategoriaFiltro.getValue() != null) {
            String kategoria = kategoriaFiltro.getValue().getIzena();
            for (Produktua p : produktuak) {
                if (p.getKategoriaIzena().equals(kategoria)) {
                    produktuakFiltratuta.add(p);
                }
            }
        } else {
            produktuakFiltratuta.addAll(produktuak);
        }
        
        // TPV botoiak sortu
        produktuaGrid.getChildren().clear();
        for (Produktua p : produktuakFiltratuta) {
            VBox boton = sortuBotoiTPV(p);
            produktuaGrid.getChildren().add(boton);
        }
    }

    @FXML
    private void aplikatuFiltroa() {
        kargatuProduktuak();
    }

    // Refactor: Usar ProduktuaBotoiaFabrika para crear botones de producto
    private VBox sortuBotoiTPV(Produktua produktua) {
        return ProduktuaBotoiaFabrika.sortu(produktua, new ProduktuaBotoiaFabrika.ProduktuaBotoiaListener() {
            @Override
            public void onProduktuaHautatu(Produktua p, VBox vBox) {
                hautatuProduktua(p, vBox);
            }
            @Override
            public void onProduktuaEditatu(Produktua p) {
                irekiEdizioModalTPV(p);
            }
            @Override
            public void onStockAldatu(Produktua p, int aldaketa) {
                aldatuStocka(p, aldaketa);
            }
        });
    }

    private void aldatuStocka(Produktua produktua, int cambio) {
        int nuevoStock = produktua.getStocka() + cambio;
        if (nuevoStock < 0) nuevoStock = 0;

        // Si se quiere subir el stock (cambio > 0)
        if (cambio > 0) {
            ObservableList<ProduktuOsagaia> osagaiak = osagaiaDAO.kargatuProduktuarenOsagaiak(produktua.getId());
            if (osagaiak.isEmpty()) {
                erakutsiNotifikazioa("Ezin da stocka igo: produktuak ez du osagairik");
                return;
            }
            // Comprobar stock de cada osagaia
            for (ProduktuOsagaia po : osagaiak) {
                Osagaia osagaia = null;
                for (Osagaia o : osagaiaDAO.kargatuOsagaiak()) {
                    if (o.getId() == po.getOsagaiaId()) {
                        osagaia = o;
                        break;
                    }
                }
                if (osagaia == null) {
                    erakutsiNotifikazioa("Osagai hau ez da existitzen: " + po.getOsagaiaIzena());
                    return;
                }
                double beharrezkoKant = po.getKantitatea() * cambio;
                if (osagaia.getStockAktuala() < beharrezkoKant) {
                    erakutsiNotifikazioa("Ez dago nahikoa '" + po.getOsagaiaIzena() + "' stocka");
                    return;
                }
            }
            // Si hay suficiente stock, restar a los osagaiak
            for (ProduktuOsagaia po : osagaiak) {
                Osagaia osagaia = null;
                for (Osagaia o : osagaiaDAO.kargatuOsagaiak()) {
                    if (o.getId() == po.getOsagaiaId()) {
                        osagaia = o;
                        break;
                    }
                }
                if (osagaia != null) {
                    double berria = osagaia.getStockAktuala() - po.getKantitatea() * cambio;
                    osagaia.setStockAktuala(berria);
                    // Actualizar en la base de datos
                    osagaiaDAO.eguneratuOsagaiaStocka(osagaia.getId(), berria);
                }
            }
        } else if (cambio < 0) {
            // Si se quiere bajar el stock, no hace falta comprobar osagaiak
            if (produktua.getStocka() + cambio < 0) {
                erakutsiNotifikazioa("Ezin da stocka jaitsi gehiago");
                return;
            }
        }
        produktuaDAO.eguneratuStocka(produktua.getId(), nuevoStock);
        kargatuProduktuak();
    }

    private void hautatuProduktua(Produktua p, VBox vBox) {
        // Restaurar estilo del anterior
        if (hautatuProduktua != null) {
            for (var child : produktuaGrid.getChildren()) {
                if (child instanceof VBox) {
                    ((VBox) child).setStyle(estiloTxartela(10));
                }
            }
        }
        
        // Marcar el nuevo seleccionado
        hautatuProduktua = p;
        vBox.setStyle(ESTILO_TARJETA_SELECCIONADA);
    }

    private String estiloTxartela(int padding) {
        return ESTILO_TARJETA_BASE + "-fx-padding: " + padding + ";";
    }

    private ObservableList<Kategoria> kargatuKategoriak() {
        return kategoriaDAO.kargatuKategoriak();
    }

    private void gehituProduktua(String izena, Kategoria kat, double prezioa, int stock) {
        produktuaDAO.gehituProduktua(izena, kat, prezioa, stock);
    }

    private void eguneratuProduktua(int id, String izena, Kategoria kat) {
        produktuaDAO.eguneratuProduktua(id, izena, kat);
    }

    @FXML
    private void irekiProduktuaModal() {
        try {
            Stage owner = (Stage) produktuaGrid.getScene().getWindow();

            TextField izenaField = new TextField();
            izenaField.setPromptText("Izena");

            ComboBox<Kategoria> katBox = new ComboBox<>();
            katBox.setPromptText("Kategoria");
            katBox.setItems(kargatuKategoriak());

            TextField prezioField = new TextField();
            prezioField.setPromptText("Prezioa (€)");

            Spinner<Integer> stockSp = new Spinner<>();
            stockSp.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0));
            stockSp.setEditable(true);

            Button onartu = new Button("Gehitu");
            onartu.setStyle("-fx-background-color: #3F7AE0; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 16;");
            onartu.setDefaultButton(true);

            Button utzi = new Button("Utzi");

            VBox root = new VBox(10,
                    new Label("Produktu berria"),
                    izenaField,
                    katBox,
                    prezioField,
                    new HBox(10, new Label("Stocka:"), stockSp),
                    new HBox(10, onartu, utzi)
            );
            root.setStyle("-fx-padding: 15; -fx-background-color: white;");

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Produktu berria");
            dialog.setScene(new Scene(root, 300, 220));

            onartu.setOnAction(ev -> {
                String izena = izenaField.getText().trim();
                Kategoria kat = katBox.getValue();
                String prezioStr = prezioField.getText().trim();
                int stock = stockSp.getValue();
                if (izena.isEmpty() || kat == null || prezioStr.isEmpty()) {
                    erakutsiNotifikazioa("Izena, kategoria eta prezioa derrigorrezkoak dira");
                    return;
                }
                double prezioa;
                try {
                    prezioa = Double.parseDouble(prezioStr.replace(",", "."));
                } catch (NumberFormatException ex) {
                    erakutsiNotifikazioa("Prezioa zenbaki balioduna izan behar da");
                    return;
                }
                gehituProduktua(izena, kat, prezioa, stock);
                kargatuProduktuak();
                erakutsiNotifikazioa("Produktua ondo gehitu da");
                dialog.close();
            });

            utzi.setOnAction(ev -> dialog.close());

            dialog.showAndWait();
        } catch (Exception e) {
            System.err.println("Errorea modal irekitzean: " + e.getMessage());
        }
    }

    @FXML
    private void irekiEdizioModal() {
        if (hautatuProduktua == null) {
            System.err.println("Ez da produkturik hautatu editatzeko");
            return;
        }
        irekiEdizioModalTPV(hautatuProduktua);
    }

    private void irekiEdizioModalTPV(Produktua produktua) {
        try {
            Stage owner = (Stage) produktuaGrid.getScene().getWindow();

            TextField izenaField = new TextField();
            izenaField.setText(produktua.getIzena());
            izenaField.setPromptText("Produktuaren izena");

            ComboBox<Kategoria> katBox = new ComboBox<>();
            katBox.setItems(kargatuKategoriak());
            // Hautatu kategoria aktuala
            for (Kategoria kat : katBox.getItems()) {
                if (kat.getIzena().equals(produktua.getKategoriaIzena())) {
                    katBox.setValue(kat);
                    break;
                }
            }

            // --- OSAGAIAK KUDEAKETA ---
            Label osagaiakLabel = new Label("Osagaiak:");
            osagaiakLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

            // Oraingo osagaien zerrenda
            ListView<String> osagaiakList = new ListView<>();
            osagaiakList.setPrefHeight(150);
            osagaiakList.setPlaceholder(new Label("Ez dago osagairik"));
            ObservableList<ProduktuOsagaia> produktuOsagaiak = osagaiaDAO.kargatuProduktuarenOsagaiak(produktua.getId());
            ObservableList<String> osagaiakDisplay = FXCollections.observableArrayList();
            for (ProduktuOsagaia po : produktuOsagaiak) {
                osagaiakDisplay.add(po.getOsagaiaIzena() + " - " + po.getKantitatea() + " " + po.getUnitatea());
            }
            osagaiakList.setItems(osagaiakDisplay);

            // Osagaiak gehitzeko formularioa
            ComboBox<Osagaia> osagaiaCombo = new ComboBox<>();
            osagaiaCombo.setPromptText("Hautatu osagaia");
            osagaiaCombo.setItems(osagaiaDAO.kargatuOsagaiak());
            osagaiaCombo.setPrefWidth(200);

            Spinner<Double> kantitateaSpinner = new Spinner<>(0.0, 100000.0, 1.0, 0.5);
            kantitateaSpinner.setEditable(true);
            kantitateaSpinner.setPrefWidth(90);

            ComboBox<String> unitateaCombo = new ComboBox<>();
            unitateaCombo.setPromptText("Unitatea");
            unitateaCombo.setEditable(true);
            unitateaCombo.setItems(FXCollections.observableArrayList("g", "kg", "ml", "l", "unit"));
            unitateaCombo.setValue("g");
            unitateaCombo.setPrefWidth(90);

            Button gehituOsagaia = new Button("Gehitu");
            gehituOsagaia.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3; -fx-padding: 4 10;");

            Button kenduOsagaia = new Button("Kendu hautatua");
            kenduOsagaia.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3; -fx-padding: 4 10;");

            Label osagaiaHint = new Label("Aukeratu osagaia, kantitatea eta unitatea, eta sakatu Gehitu");
            osagaiaHint.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11;");

            HBox osagaiaRow1 = new HBox(8, new Label("Osagaia:"), osagaiaCombo);
            osagaiaRow1.setAlignment(Pos.CENTER_LEFT);
            HBox osagaiaRow2 = new HBox(8, new Label("Kantitatea:"), kantitateaSpinner, unitateaCombo, gehituOsagaia, kenduOsagaia);
            osagaiaRow2.setAlignment(Pos.CENTER_LEFT);

            VBox osagaiaForm = new VBox(6, osagaiaRow1, osagaiaRow2, osagaiaHint);

            gehituOsagaia.setOnAction(ev -> {
                Osagaia osagaia = osagaiaCombo.getValue();
                String kantitateaStr = kantitateaSpinner.getEditor().getText().trim();
                String unitatea = unitateaCombo.getValue();
                if (unitatea == null || unitatea.trim().isEmpty()) {
                    unitatea = unitateaCombo.getEditor().getText().trim();
                }
                
                if (osagaia == null || kantitateaStr.isEmpty() || unitatea == null || unitatea.isEmpty()) {
                    System.err.println("Osagaia, kantitatea eta unitatea beharrezkoak dira");
                    return;
                }
                
                try {
                    double kantitatea = Double.parseDouble(kantitateaStr.replace(",", "."));
                    osagaiaDAO.gehituOsagaiaProduktuari(produktua.getId(), osagaia.getId(), kantitatea, unitatea);
                    
                    // Zerrenda eguneratu
                    osagaiakDisplay.add(osagaia.getIzena() + " - " + kantitatea + " " + unitatea);
                    produktuOsagaiak.add(new ProduktuOsagaia(produktua.getId(), osagaia.getId(), osagaia.getIzena(), kantitatea, unitatea));
                    
                    kantitateaSpinner.getEditor().setText("1");
                    osagaiaCombo.setValue(null);
                } catch (NumberFormatException ex) {
                    System.err.println("Kantitatea zenbaki balioduna izan behar da");
                }
            });

            kenduOsagaia.setOnAction(ev -> {
                int selectedIndex = osagaiakList.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    ProduktuOsagaia po = produktuOsagaiak.get(selectedIndex);
                    osagaiaDAO.kenduOsagaiaProduktuatik(produktua.getId(), po.getOsagaiaId());
                    osagaiakDisplay.remove(selectedIndex);
                    produktuOsagaiak.remove(selectedIndex);
                }
            });

            // Botoi nagusiak
            Button gorde = new Button("Gorde");
            gorde.setStyle("-fx-background-color: #3F7AE0; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 16;");
            gorde.setDefaultButton(true);

            Button utzi = new Button("Utzi");

            HBox botoiak = new HBox(10, gorde, utzi);
            botoiak.setAlignment(Pos.CENTER);

            VBox root = new VBox(10,
                    new Label("Produktua editatu"),
                    new Label("Izena:"),
                    izenaField,
                    new Label("Kategoria:"),
                    katBox,
                    osagaiakLabel,
                    osagaiakList,
                    osagaiaForm,
                    botoiak
            );
            root.setStyle("-fx-padding: 15; -fx-background-color: white;");

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: white;");

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Produktua editatu");
            dialog.setScene(new Scene(scrollPane, 500, 550));

            gorde.setOnAction(ev -> {
                String izena = izenaField.getText().trim();
                Kategoria kat = katBox.getValue();
                if (izena.isEmpty() || kat == null) {
                    System.err.println("Izena eta kategoria beharrezkoak dira");
                    return;
                }
                eguneratuProduktua(produktua.getId(), izena, kat);
                kargatuProduktuak();
                dialog.close();
            });

            utzi.setOnAction(ev -> dialog.close());

            dialog.showAndWait();
        } catch (Exception e) {
            System.err.println("Errorea edizio modala irekitzean: " + e.getMessage());
        }
    }

    @FXML
    private void eguneratuStocka() {
        // Este método ya no se usa en TPV, pero lo dejamos por compatibilidad
    }

    @FXML
    private void ezabatuProduktua() {
        if (hautatuProduktua != null) {
            ezabatuProduktuaTPV(hautatuProduktua);
        } else {
            System.err.println("Ez da produkturik hautatu ezabatzeko");
        }
    }

    private void ezabatuProduktuaTPV(Produktua produktua) {
        try {
            produktuaDAO.ezabatuProduktua(produktua.getId());
            hautatuProduktua = null;
            kargatuProduktuak();
        } catch (Exception e) {
            System.err.println("Errorea produktua ezabatzean: " + e.getMessage());
        }
    }

    @FXML
    private void itxiSaioa(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            Stage currentStage = (Stage) btn.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/sukaldea/fxml/Login-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/org/example/sukaldea/css/estilos.css").toExternalForm());

            Stage loginStage = new Stage();
            loginStage.setTitle("JAUS - Saioa Hasi");
            loginStage.setScene(scene);
            loginStage.show();

            if (currentStage != null) {
                currentStage.close();
            }

        } catch (Exception e) {
            System.err.println("Errorea saioa itxitean: " + e.getMessage());
            e.printStackTrace();
        }
    }
}




