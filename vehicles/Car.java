package vehicles;

import interfaces.*;
import exceptions.*;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 5;
    private int currentPassengers;
    private boolean maintenanceFlag;

    public Car(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0.0;
        this.currentPassengers = 0;
        this.maintenanceFlag = false;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > fuelLevel) throw new InsufficientFuelException("Not enough fuel for the trip");
        fuelLevel -= fuelNeeded;
        addMileage(distance);
        System.out.println("Driving on road...");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be > 0");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double needed = distance / calculateFuelEfficiency();
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel");
        fuelLevel -= needed;
        return needed;
    }

    // PassengerCarrier
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (count <= 0) return;
        if (currentPassengers + count > passengerCapacity)
            throw new OverloadException("Passenger overload for Car");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count < 0) throw new InvalidOperationException("Invalid passenger count");
        if (count > currentPassengers) throw new InvalidOperationException("Not enough passengers to disembark");
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    // Maintainable
    @Override
    public void scheduleMaintenance() {
        maintenanceFlag = true;
    }

    @Override
    public boolean needsMaintenance() {
        return maintenanceFlag || getCurrentMileage() > 10000.0;
    }

    @Override
    public void performMaintenance() {
        maintenanceFlag = false;
        System.out.println("Car maintenance performed.");
    }
}
