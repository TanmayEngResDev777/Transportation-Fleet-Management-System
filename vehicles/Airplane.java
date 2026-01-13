package vehicles;

import interfaces.*;
import exceptions.*;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 200;
    private int currentPassengers;
    private final double cargoCapacity = 10000.0;
    private double currentCargo;
    private boolean maintenanceFlag;

    public Airplane(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed, maxAltitude);
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
        if (needed > fuelLevel) throw new InsufficientFuelException("Insufficient fuel for flight");
        fuelLevel -= needed;
        addMileage(distance);
        System.out.println("Flying at " + getMaxAltitude() + " meters...");
    }

    @Override
    public double calculateFuelEfficiency() {
        return 5.0;
    }

    // FuelConsumable
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel must be > 0");
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

    // PassengerCarrier
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (count <= 0) return;
        if (currentPassengers + count > passengerCapacity)
            throw new OverloadException("Passenger overload for Airplane");
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
            throw new OverloadException("Cargo overload for Airplane");
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
        System.out.println("Airplane maintenance performed.");
    }
}
