package com.wipro.vrs.main;

import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.Scanner;

import com.wipro.vrs.entity.Vehicle;
import com.wipro.vrs.entity.Customer;
import com.wipro.vrs.entity.Rental;
import com.wipro.vrs.service.VehicleRentalService;
import com.wipro.vrs.util.InvalidCustomerException;
import com.wipro.vrs.util.VehicleNotAvailableException;
import com.wipro.vrs.util.RentalOperationException;

public class TestVehicleRental {

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("      WELCOME TO VEHICLE RENTAL SYSTEM     ");
        System.out.println("==========================================\n");

        Scanner input = new Scanner(System.in);

        ArrayList<Vehicle> vehicles = new ArrayList<>();

       
        vehicles.add(new Vehicle("V001", "Swift", "CAR", true, 1500.0));
        vehicles.add(new Vehicle("V002", "Activa", "BIKE", true, 500.0));
        vehicles.add(new Vehicle("V003", "Innova", "SUV", false, 2500.0)); 
        vehicles.add(new Vehicle("P001", "BMW X5", "PREMIUM", true, 6000.0));
        vehicles.add(new Vehicle("P002", "Mercedes C-Class", "PREMIUM", true, 6500.0));
        vehicles.add(new Vehicle("P003", "Audi A6", "PREMIUM", true, 6200.0));
   
        ArrayList<Customer> customers = new ArrayList<>();
        customers.add(new Customer("C001", "Rahul Sharma", "DL12345", "REGULAR", 0));
        customers.add(new Customer("C002", "ABC Corp", "NA_CORP", "CORPORATE", 0));

   
        ArrayList<Rental> rentals = new ArrayList<>();
        VehicleRentalService service = new VehicleRentalService(vehicles, customers, rentals);

        try {
            System.out.println("Enter Customer ID to Rent a Vehicle (Ex:: C001):: ");
            String cid = input.nextLine();

         
            service.validateCustomer(cid);

            System.out.println("Enter Vehicle ID to Rent:: ");
            System.out.println("Ex:: V001 (Swift), P001 (BMW)");
            String vid = input.nextLine();

            
            service.checkVehicleAvailability(vid);

            System.out.println("Enter Number of Days for Rental:: ");
            int days = input.nextInt();

          
            Rental rental = service.startRental(cid, vid, days);
            System.out.println("\n------------------------------------------");
            System.out.println("RENTAL SUCCESSFUL!");
            System.out.println("Rental ID: " + rental.getRentalId());
            System.out.println("------------------------------------------\n");

            LocalDate returnDate = LocalDate.now().plusDays(days + 1);

            service.endRental(rental.getRentalId(), returnDate);

           

        } catch (InvalidCustomerException | VehicleNotAvailableException | RentalOperationException ex) {
            System.out.println("\nERROR: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.toString());
        }

    }
}
