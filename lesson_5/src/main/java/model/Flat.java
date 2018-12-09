package model;

import java.util.List;

public class Flat {
    private int rooms;
    private long price;
    private boolean VIP;
    private List<Tenant> tenants;

    public Flat(int rooms, long price, boolean VIP, List<Tenant> tenants) {
        this.rooms = rooms;
        this.price = price;
        this.VIP = VIP;
        this.tenants = tenants;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isVIP() {
        return VIP;
    }

    public void setVIP(boolean VIP) {
        this.VIP = VIP;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    @Override
    public String toString() {
        return "Flat{" +
                "rooms=" + rooms +
                ", price=" + price +
                ", VIP=" + VIP +
                ", tenants=" + tenants +
                '}';
    }
}
