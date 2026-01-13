package fleet;

import vehicles.*;
import interfaces.*;
import exceptions.*;

import java.io.*;
import java.util.*;

public class FleetManager {
    private List<Vehicle> fleet;

    public FleetManager() {
        this.fleet = new ArrayList<>();
    }

    public List<Vehicle> getFleet() {
        return fleet;
    }

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle existing : fleet) {
            if (existing.getId().equals(v.getId())) {
                throw new InvalidOperationException("Vehicle ID already exists: " + v.getId());
            }
        }
        fleet.add(v);
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = false;
        Iterator<Vehicle> it = fleet.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();
            if (v.getId().equals(id)) {
                it.remove();
                removed = true;
                break;
            }
        }
        if (!removed) throw new InvalidOperationException("Vehicle ID not found: " + id);
    }

    public void startAllJourneys(double distance) {
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (InsufficientFuelException ife) {
                System.out.println("InsufficientFuel for " + v.getId() + ": " + ife.getMessage());
            } catch (InvalidOperationException ioe) {
                System.out.println("InvalidOperation for " + v.getId() + ": " + ioe.getMessage());
            } catch (Exception e) {
                System.out.println("Error moving " + v.getId() + ": " + e.getMessage());
            }
        }
    }

    public double getTotalFuelConsumption(double distance) {
        double total = 0.0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable f) {
                try {
                    total += f.consumeFuel(distance);
                } catch (InsufficientFuelException e) {
                    System.out.println("Not enough fuel for " + v.getId());
                }
            }
        }
        return total;
    }

    public void maintainAll() {
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m) {
                if (m.needsMaintenance()) {
                    m.performMaintenance();
                }
            }
        }
    }

    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> results = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (type.isInstance(v)) results.add(v);
        }
        return results;
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Fleet Report ===\n");
        sb.append("Total vehicles: ").append(fleet.size()).append("\n");

        Map<String, Integer> counts = new HashMap<>();
        for (Vehicle v : fleet) {
            String t = v.getClass().getSimpleName();
            counts.put(t, counts.getOrDefault(t, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        double totalEff = 0.0;
        int countEff = 0;
        for (Vehicle v : fleet) {
            double eff = v.calculateFuelEfficiency();
            if (eff > 0) {
                totalEff += eff;
                countEff++;
            }
        }
        if (countEff > 0) sb.append("Average fuel efficiency: ").append(totalEff / countEff).append(" km/l\n");

        double totalMileage = 0.0;
        for (Vehicle v : fleet) totalMileage += v.getCurrentMileage();
        sb.append("Total mileage: ").append(totalMileage).append(" km\n");

        sb.append("Vehicles needing maintenance: ").append(getVehiclesNeedingMaintenance().size()).append("\n");

        return sb.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> needs = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m) {
                if (m.needsMaintenance()) needs.add(v);
            }
        }
        return needs;
    }

    public void saveToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Vehicle v : fleet) {
                pw.println(serializeVehicle(v));
            }
            System.out.println("Fleet saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving fleet: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            fleet.clear();
            String line;
            while ((line = br.readLine()) != null) {
                Vehicle v = deserializeVehicle(line);
                if (v != null) fleet.add(v);
            }
            System.out.println("Fleet loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading fleet: " + e.getMessage());
        } catch (InvalidOperationException e) {
            System.out.println("Error creating vehicle during load: " + e.getMessage());
        }
    }

    private String serializeVehicle(Vehicle v) {
        return switch (v) {
            case Car c -> String.join(",",
                    "Car",
                    c.getId(),
                    c.getModel(),
                    String.valueOf(c.getMaxSpeed()),
                    String.valueOf(c.getNumWheels()),
                    String.valueOf(c.getFuelLevel()),
                    String.valueOf(c.getPassengerCapacity()),
                    String.valueOf(c.getCurrentPassengers()),
                    String.valueOf(c.getCurrentMileage()));
            case Truck t -> String.join(",",
                    "Truck",
                    t.getId(),
                    t.getModel(),
                    String.valueOf(t.getMaxSpeed()),
                    String.valueOf(t.getNumWheels()),
                    String.valueOf(t.getFuelLevel()),
                    String.valueOf(t.getCargoCapacity()),
                    String.valueOf(t.getCurrentCargo()),
                    String.valueOf(t.getCurrentMileage()));
            case Bus b -> String.join(",",
                    "Bus",
                    b.getId(),
                    b.getModel(),
                    String.valueOf(b.getMaxSpeed()),
                    String.valueOf(b.getNumWheels()),
                    String.valueOf(b.getFuelLevel()),
                    String.valueOf(b.getPassengerCapacity()),
                    String.valueOf(b.getCurrentPassengers()),
                    String.valueOf(b.getCargoCapacity()),
                    String.valueOf(b.getCurrentCargo()),
                    String.valueOf(b.getCurrentMileage()));
            case Airplane a -> String.join(",",
                    "Airplane",
                    a.getId(),
                    a.getModel(),
                    String.valueOf(a.getMaxSpeed()),
                    String.valueOf(a.getFuelLevel()),
                    String.valueOf(a.getMaxAltitude()),
                    String.valueOf(a.getPassengerCapacity()),
                    String.valueOf(a.getCurrentPassengers()),
                    String.valueOf(a.getCargoCapacity()),
                    String.valueOf(a.getCurrentCargo()),
                    String.valueOf(a.getCurrentMileage()));
            case CargoShip s -> String.join(",",
                    "CargoShip",
                    s.getId(),
                    s.getModel(),
                    String.valueOf(s.getMaxSpeed()),
                    String.valueOf(s.getFuelLevel()),
                    String.valueOf(s.hasSail()),
                    String.valueOf(s.getCargoCapacity()),
                    String.valueOf(s.getCurrentCargo()),
                    String.valueOf(s.getCurrentMileage()));
            default -> "";
        };
    }

    private Vehicle deserializeVehicle(String line) throws InvalidOperationException {
        try {
            String[] p = line.split(",");
            String type = p[0];
            switch (type) {
                case "Car": {
                    String id = p[1];
                    String model = p[2];
                    double maxSpeed = Double.parseDouble(p[3]);
                    int wheels = Integer.parseInt(p[4]);
                    Car c = new Car(id, model, maxSpeed, wheels);
                    double fuelLevel = Double.parseDouble(p[5]);
                    if (fuelLevel > 0) c.refuel(fuelLevel);
                    int passCap = Integer.parseInt(p[6]);
                    int currPass = Integer.parseInt(p[7]);
                    if (currPass > 0) c.boardPassengers(currPass);
                    return c;
                }
                case "Truck": {
                    String id = p[1];
                    String model = p[2];
                    double maxSpeed = Double.parseDouble(p[3]);
                    int wheels = Integer.parseInt(p[4]);
                    Truck t = new Truck(id, model, maxSpeed, wheels);
                    double fuel = Double.parseDouble(p[5]);
                    if (fuel > 0) t.refuel(fuel);
                    double currCargo = Double.parseDouble(p[7]);
                    if (currCargo > 0) t.loadCargo(currCargo);
                    return t;
                }
                case "Bus": {
                    String id = p[1];
                    String model = p[2];
                    double maxSpeed = Double.parseDouble(p[3]);
                    int wheels = Integer.parseInt(p[4]);
                    Bus b = new Bus(id, model, maxSpeed, wheels);
                    double fuel = Double.parseDouble(p[5]);
                    if (fuel > 0) b.refuel(fuel);
                    int currPass = Integer.parseInt(p[7]);
                    if (currPass > 0) b.boardPassengers(currPass);
                    double currCargo = Double.parseDouble(p[9]);
                    if (currCargo > 0) b.loadCargo(currCargo);
                    return b;
                }
                case "Airplane": {
                    String id = p[1];
                    String model = p[2];
                    double maxSpeed = Double.parseDouble(p[3]);
                    double fuel = Double.parseDouble(p[4]);
                    double maxAlt = Double.parseDouble(p[5]);
                    Airplane a = new Airplane(id, model, maxSpeed, maxAlt);
                    if (fuel > 0) a.refuel(fuel);
                    int currPass = Integer.parseInt(p[7]);
                    if (currPass > 0) a.boardPassengers(currPass);
                    double currCargo = Double.parseDouble(p[9]);
                    if (currCargo > 0) a.loadCargo(currCargo);
                    return a;
                }
                case "CargoShip": {
                    String id = p[1];
                    String model = p[2];
                    double maxSpeed = Double.parseDouble(p[3]);
                    double fuel = Double.parseDouble(p[4]);
                    boolean hasSail = Boolean.parseBoolean(p[5]);
                    CargoShip s = new CargoShip(id, model, maxSpeed, hasSail);
                    if (!hasSail && fuel > 0) s.refuel(fuel);
                    double currCargo = Double.parseDouble(p[7]);
                    if (currCargo > 0) s.loadCargo(currCargo);
                    return s;
                }
                default:
                    System.out.println("Unknown type in CSV: " + type);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to parse CSV line: " + line + " -> " + e.getMessage());
            return null;
        }
    }
}
