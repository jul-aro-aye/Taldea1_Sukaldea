package model;

import java.time.LocalDateTime;
import java.util.List;

public class Eskaera {
    private final int id;
    private final Integer mahaiaZenbakia;
    private final LocalDateTime sortzeData;
    private final List<EskaeraItem> items;
    private final String sukaldeaEgoera;

    public Eskaera(int id, Integer mahaiaZenbakia, LocalDateTime sortzeData, List<EskaeraItem> items, String sukaldeaEgoera) {
        this.id = id;
        this.mahaiaZenbakia = mahaiaZenbakia;
        this.sortzeData = sortzeData;
        this.items = items;
        this.sukaldeaEgoera = sukaldeaEgoera;
    }

    public int getId() {
        return id;
    }

    public Integer getMahaiaZenbakia() {
        return mahaiaZenbakia;
    }

    public LocalDateTime getSortzeData() {
        return sortzeData;
    }

    public List<EskaeraItem> getItems() {
        return items;
    }

    public String getSukaldeaEgoera() {
        return sukaldeaEgoera;
    }
}
