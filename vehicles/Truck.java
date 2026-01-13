package vehicles;

import interfaces.*;
import exceptions.*;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final double cargoCapacity = 5000.0; // kg
    private double currentCargo;
    private boolean maintenanceFlag;

    public Truck(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0.0;
        this.currentCargo = 0.0;
        this.maintenanceFlag = false;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double eff = calculateFuelEfficiency();
        if (eff <= 0) throw new InvalidOperationException("Invalid fuel efficiency");
        double fuelNeeded = distance / eff;
        if (fuelNeeded > fuelLevel) throw new InsufficientFuelException("Insufficient fuel for the trip");
        fuelLevel -= fuelNeeded;
        addMileage(distance);
        System.out.println("Hauling cargo...");
    }

    @Override
    public double calculateFuelEfficiency() {
        double base = 8.0;
        if (currentCargo > 0.5 * cargoCapacity) {
            return base * 0.9;
        }
        return base;
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount > 0 required");
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
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel");
        fuelLevel -= needed;
        return needed;
    }

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (weight <= 0) return;
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Cargo overload for Truck");
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
        System.out.println("Truck maintenance performed.");
    }
}
