package vehicles;

import exceptions.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / getMaxSpeed(); // hours
        return baseTime * 1.1; // add 10% for traffic
    }

    public int getNumWheels() {
        return numWheels;
    }
}
