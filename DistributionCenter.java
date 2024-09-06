package Logistic;

public class DistributionCenter {
    private double x;
    private double y;

    // Constructor
    public DistributionCenter(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter cho tọa độ
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Phương thức tính khoảng cách tới một vị trí khách hàng
    public double calculateDistance(double customerX, double customerY) {
        return Math.sqrt(Math.pow(this.x - customerX, 2) + Math.pow(this.y - customerY, 2));
    }
}
