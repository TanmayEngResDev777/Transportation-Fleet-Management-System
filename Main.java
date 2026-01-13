import fleet.FleetManager;
import vehicles.*;
import interfaces.*;
import exceptions.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_CSV = "fleet.csv";

    public static void main(String[] args) {
        FleetManager manager = new FleetManager();

        // Demo
        try {
            Car car = new Car("C001", "Toyota", 120.0, 4);
            car.refuel(50.0);
            car.boardPassengers(2);

            Truck truck = new Truck("T001", "Volvo", 100.0, 6);
            truck.refuel(200.0);
            truck.loadCargo(2000.0);

            Bus bus = new Bus("B001", "Mercedes", 90.0, 6);
            bus.refuel(300.0);
            bus.boardPassengers(20);
            bus.loadCargo(200.0);

            Airplane plane = new Airplane("A001", "Boeing", 850.0, 10000.0);
            plane.refuel(1000.0);
            plane.boardPassengers(150);
            plane.loadCargo(2000.0);

            CargoShip ship = new CargoShip("S001", "Maersk", 50.0, false);
            ship.refuel(500.0);
            ship.loadCargo(10000.0);

            manager.addVehicle(car);
            manager.addVehicle(truck);
            manager.addVehicle(bus);
            manager.addVehicle(plane);
            manager.addVehicle(ship);

            System.out.println("Demo: starting all journeys for 100 km");
            manager.startAllJourneys(100.0);
            System.out.println(manager.generateReport());
            manager.saveToFile(DEFAULT_CSV);
        } catch (InvalidOperationException | OverloadException e) {
            System.out.println("Demo setup error: " + e.getMessage());
        }

        // CLI
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String line = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice");
                continue;
            }
            switch (choice) {
                case 1 -> doAddVehicle(manager, sc);
                case 2 -> doRemoveVehicle(manager, sc);
                case 3 -> doStartJourney(manager, sc);
                case 4 -> doRefuelAll(manager, sc);
                case 5 -> doPerformMaintenance(manager);
                case 6 -> System.out.println(manager.generateReport());
                case 7 -> manager.saveToFile(DEFAULT_CSV);
                case 8 -> manager.loadFromFile(DEFAULT_CSV);
                case 9 -> doSearchByType(manager, sc);
                case 10 -> doListMaintenance(manager);
                case 11 -> {
                    running = false;
                    System.out.println("Exiting...");
                }
                default -> System.out.println("Invalid option.");
            }
        }

        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Fleet Manager Menu ===");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey");
        System.out.println("4. Refuel All (same amount to each fuelable vehicle)");
        System.out.println("5. Perform Maintenance");
        System.out.println("6. Generate Report");
        System.out.println("7. Save Fleet");
        System.out.println("8. Load Fleet");
        System.out.println("9. Search by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Exit");
        System.out.print("Choose an option: ");
    }

    private static void doAddVehicle(FleetManager manager, Scanner sc) {
        try {
            System.out.print("Enter type (Car/Truck/Bus/Airplane/CargoShip): ");
            String type = sc.nextLine().trim();

            System.out.print("Enter ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Enter model: ");
            String model = sc.nextLine().trim();
            System.out.print("Enter maxSpeed: ");
            double maxSpeed = Double.parseDouble(sc.nextLine().trim());

            Vehicle v;
            switch (type) {
                case "Car" -> {
                    System.out.print("Enter numWheels: ");
                    int wheels = Integer.parseInt(sc.nextLine().trim());
                    v = new Car(id, model, maxSpeed, wheels);
                }
                case "Truck" -> {
                    System.out.print("Enter numWheels: ");
                    int wheels = Integer.parseInt(sc.nextLine().trim());
                    v = new Truck(id, model, maxSpeed, wheels);
                }
                case "Bus" -> {
                    System.out.print("Enter numWheels: ");
                    int wheels = Integer.parseInt(sc.nextLine().trim());
                    v = new Bus(id, model, maxSpeed, wheels);
                }
                case "Airplane" -> {
                    System.out.print("Enter maxAltitude: ");
                    double alt = Double.parseDouble(sc.nextLine().trim());
                    v = new Airplane(id, model, maxSpeed, alt);
                }
                case "CargoShip" -> {
                    System.out.print("Has sail? (true/false): ");
                    boolean hasSail = Boolean.parseBoolean(sc.nextLine().trim());
                    v = new CargoShip(id, model, maxSpeed, hasSail);
                }
                default -> {
                    System.out.println("Unknown type.");
                    return;
                }
            }
            manager.addVehicle(v);
            System.out.println("Added " + type + " with ID " + v.getId());
        } catch (Exception e) {
            System.out.println("Failed to add vehicle: " + e.getMessage());
        }
    }

    private static void doRemoveVehicle(FleetManager manager, Scanner sc) {
        System.out.print("Enter ID to remove: ");
        String id = sc.nextLine().trim();
        try {
            manager.removeVehicle(id);
            System.out.println("Removed " + id);
        } catch (InvalidOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void doStartJourney(FleetManager manager, Scanner sc) {
        try {
            System.out.print("Enter distance: ");
            double d = Double.parseDouble(sc.nextLine().trim());
            manager.startAllJourneys(d);
        } catch (NumberFormatException e) {
            System.out.println("Invalid distance");
        }
    }

    private static void doRefuelAll(FleetManager manager, Scanner sc) {
        try {
            System.out.print("Enter amount to refuel each fuelable vehicle: ");
            double a = Double.parseDouble(sc.nextLine().trim());
            for (Vehicle v : manager.getFleet()) {
                if (v instanceof FuelConsumable) {
                    try {
                        ((FuelConsumable) v).refuel(a);
                        System.out.println("Refueled " + v.getId());
                    } catch (InvalidOperationException e) {
                        System.out.println("Refuel error for " + v.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount");
        }
    }

    private static void doPerformMaintenance(FleetManager manager) {
        manager.maintainAll();
        System.out.println("Performed maintenance where needed.");
    }

    private static void doSearchByType(FleetManager manager, Scanner sc) {
        System.out.print("Enter type to search (Car/Truck/Bus/Airplane/CargoShip): ");
        String t = sc.nextLine().trim();
        try {
            Class<?> cls = Class.forName("vehicles." + t);
            List<Vehicle> res = manager.searchByType(cls);
            if (res.isEmpty()) System.out.println("No vehicles found.");
            for (Vehicle v : res) v.displayInfo();
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid type.");
        }
    }

    private static void doListMaintenance(FleetManager manager) {
        List<Vehicle> needs = manager.getVehiclesNeedingMaintenance();
        if (needs.isEmpty()) {
            System.out.println("No vehicles need maintenance.");
        } else {
            for (Vehicle v : needs) v.displayInfo();
        }
    }
}
