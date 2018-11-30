package model;


import java.util.List;

public class House {
    private List<Flat> flats;
    private HouseType type;
    private String address;

    public House(List<Flat> flats, HouseType type, String adress) {
        this.flats = flats;
        this.type = type;
        this.address = adress;
    }

    public List<Flat> getFlats() {
        return flats;
    }

    public void setFlats(List<Flat> flats) {
        this.flats = flats;
    }

    public HouseType getType() {
        return type;
    }

    public void setType(HouseType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
