package vehicles;

import interfaces.*;
import exceptions.*;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private final double cargoCapacity = 50000.0;
    private double currentCargo;
    private boolean maintenanceFlag;
    private double fuelLevel;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed, hasSail);
        this.currentCargo = 0.0;
        this.maintenanceFlag = false;
        this.fuelLevel = 0.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        double eff = calculateFuelEfficiency();
        if (eff == 0.0 && hasSail()) {
            addMileage(distance);
            System.out.println("Sailing with cargo... (by sail)");
            return;
        }
        if (eff <= 0.0) throw new InvalidOperationException("Invalid fuel configuration for ship");
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel for sailing");
        fuelLevel -= needed;
        addMileage(distance);
        System.out.println("Sailing with cargo...");
    }

    @Override
    public double calculateFuelEfficiency() {
        if (hasSail()) return 0.0;
        return 4.0;
    }

    // CargoCarrier
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (weight <= 0) return;
        if (currentCargo + weight > cargoCapacity)
            throw new OverloadException("Cargo overload for CargoShip");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight < 0) throw new InvalidOperationException("Invalid unload weight");
        if (weight > currentCargo) throw new InvalidOperationException("Cannot unload more than present");
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
        System.out.println("Cargo ship maintenance performed.");
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()) throw new InvalidOperationException("This ship has sail; refuel not applicable");
        if (amount <= 0) throw new InvalidOperationException("Refuel amount > 0 required");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        if (hasSail()) return 0.0;
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (hasSail()) throw new InsufficientFuelException("Ship uses sail; no fuel consumed");
        double eff = calculateFuelEfficiency();
        double needed = distance / eff;
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel");
        fuelLevel -= needed;
        return needed;
    }
}
