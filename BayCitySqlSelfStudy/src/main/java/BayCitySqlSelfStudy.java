package com.baycity.sqltracker;

import java.sql.*;
import java.util.Scanner;

public class BayCitySqlSelfStudy {

    private static final String DB_URL = "jdbc:sqlserver://skills4it.database.windows.net:1433;" +
            "database=Courses;" +
            "user=gtareader@skills4it;" +
            "password=StrongPass!2025;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "loginTimeout=30;";

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Bay City SQL CLI ===");
            System.out.println("1. Suspect Scanner (WHERE)");
            System.out.println("2. Vehicle Watchlist (JOIN + WHERE)");
            System.out.println("3. Reward Tracker (GROUP BY + SUM + ORDER BY)");
            System.out.println("4. Elite Agent Filter (GROUP BY + HAVING)");
            System.out.println("5. Exit");
            System.out.print("Select mission: ");

            int choice = inputScanner.nextInt();
            switch (choice) {
                case 1 -> runSuspectScanner();
                case 2 -> runVehicleWatchlist();
                case 3 -> runRewardTracker();
                case 4 -> runEliteAgentFilter();
                case 5 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void runSuspectScanner() {
        String query = "SELECT Name, Alias, WantedLevel FROM GTA.Citizens WHERE WantedLevel >= ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, 2);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Suspects with WantedLevel >= 2 ---");
            while (rs.next()) {
                System.out.printf("%-20s %-20s Wanted Level: %d\n",
                        rs.getString("Name"), rs.getString("Alias"), rs.getInt("WantedLevel"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runVehicleWatchlist() {
        String query = "SELECT c.Name, v.Type, v.Brand " +
                "FROM GTA.Citizens c JOIN GTA.Vehicles v ON c.ID = v.CitizenID " +
                "WHERE v.IsStolen = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Stolen Vehicles and Their Owners ---");
            while (rs.next()) {
                System.out.printf("%-20s %-15s %-15s\n",
                        rs.getString("Name"), rs.getString("Type"), rs.getString("Brand"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runRewardTracker() {
        String query = "SELECT c.Name, SUM(m.Reward) AS TotalEarnings " +
                "FROM GTA.Citizens c JOIN GTA.Missions m ON c.ID = m.CitizenID " +
                "GROUP BY c.Name ORDER BY TotalEarnings DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Reward Tracker ---");
            while (rs.next()) {
                System.out.printf("%-20s $%.2f\n",
                        rs.getString("Name"), rs.getDouble("TotalEarnings"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runEliteAgentFilter() {
        String query = "SELECT c.Name, COUNT(*) AS MissionCount, SUM(m.Reward) AS TotalEarnings " +
                "FROM GTA.Citizens c JOIN GTA.Missions m ON c.ID = m.CitizenID " +
                "GROUP BY c.Name HAVING COUNT(*) >= 2 AND SUM(m.Reward) >= 4000";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Elite Agents ---");
            while (rs.next()) {
                System.out.printf("%-20s Missions: %d, Earnings: $%.2f\n",
                        rs.getString("Name"), rs.getInt("MissionCount"), rs.getDouble("TotalEarnings"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

