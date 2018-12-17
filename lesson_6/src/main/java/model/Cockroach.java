package model;

public class Cockroach {
    private int speed;
    private String name;

    public Cockroach(int speed, String name) {
        this.speed = speed;
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Cockroach{" +
                "speed=" + speed +
                ", name='" + name + '\'' +
                '}';
    }
}
