package vehicles;

import interfaces.*;
import exceptions.*;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 50;
    private int currentPassengers;
    private final double cargoCapacity = 500.0; // kg
    private double currentCargo;
    private boolean maintenanceFlag;

    public Bus(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0.0;
        this.currentPassengers = 0;
        this.currentCargo = 0.0;
        this.maintenanceFlag = false;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double eff = calculateFuelEfficiency();
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel for the bus journey");
        fuelLevel -= needed;
        addMileage(distance);
        System.out.println("Transporting passengers and cargo...");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 10.0;
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double eff = calculateFuelEfficiency();
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Not enough fuel");
        fuelLevel -= needed;
        return needed;
    }

    // PassengerCarrier
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (count <= 0) return;
        if (currentPassengers + count > passengerCapacity)
            throw new OverloadException("Passenger overload for Bus");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count < 0) throw new InvalidOperationException("Invalid passenger count");
        if (count > currentPassengers) throw new InvalidOperationException("Cannot disembark more than onboard");
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

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (weight <= 0) return;
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Cargo overload for Bus");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight < 0) throw new InvalidOperationException("Invalid unload weight");
        if (weight > currentCargo) throw new InvalidOperationException("Cannot unload more than current cargo");
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }

    @Override
    public double getCurrentCargo() {
        return currentCargo;
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
        System.out.println("Bus maintenance performed.");
    }
}
