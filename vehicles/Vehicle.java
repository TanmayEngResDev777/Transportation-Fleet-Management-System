package vehicles;

import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed) throws InvalidOperationException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidOperationException("Vehicle ID must be non-empty");
        }
        this.id = id;
        this.model = (model == null ? "Unknown" : model);
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
    }

    public abstract void move(double distance) throws InvalidOperationException, Exception;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);

    public void displayInfo() {
        System.out.println("----- Vehicle Info -----");
        System.out.println("Type: " + this.getClass().getSimpleName());
        System.out.println("ID: " + id);
        System.out.println("Model: " + model);
        System.out.println("Max Speed: " + maxSpeed + " km/h");
        System.out.println("Mileage: " + currentMileage + " km");
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    protected void addMileage(double distance) {
        this.currentMileage += distance;
    }

    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(this.calculateFuelEfficiency(), other.calculateFuelEfficiency());
    }
}
