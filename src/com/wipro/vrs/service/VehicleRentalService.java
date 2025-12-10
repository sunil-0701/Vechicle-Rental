package com.wipro.vrs.service;

import java.util.*;
import com.wipro.vrs.entity.*;
import com.wipro.vrs.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class VehicleRentalService {

	private ArrayList<Vehicle> vehicles;
	private ArrayList<Customer> customers;
	private ArrayList<Rental> rentals;

	public VehicleRentalService(ArrayList<Vehicle> vehicles, ArrayList<Customer> customers, ArrayList<Rental> rentals) {

		this.vehicles = vehicles;
		this.customers = customers;
		this.rentals = rentals;
	}

	public boolean validateCustomer(String customerId) throws InvalidCustomerException {

		if (customerId == null || customerId.trim().isEmpty()) {
			throw new InvalidCustomerException("Customer ID cannot be empty.");
		}

		for (Customer c : customers) {
			if (c.getCustomerId().equals(customerId)) {
				return true;
			}
		}

		throw new InvalidCustomerException("Customer ID does not exist");
	}

	
	public boolean checkVehicleAvailability(String vehicleId) throws VehicleNotAvailableException {

		if (vehicleId == null || vehicleId.trim().isEmpty()) {
			throw new VehicleNotAvailableException("Vehicle ID cannot be empty.");
		}

		for (Vehicle v : vehicles) {
			if (v.getVehicleId().equals(vehicleId)) {

				if (!v.isAvailable()) {
					throw new VehicleNotAvailableException("Selected Vehicle is Unavailable.");
				}
				return true;
			}
		}

		throw new VehicleNotAvailableException("Vehicle does not exist or Invalid Vehicle ID.");
	}

	public Rental startRental(String customerId, String vehicleId, int days)
			throws InvalidCustomerException, VehicleNotAvailableException, RentalOperationException {

		validateCustomer(customerId);
		checkVehicleAvailability(vehicleId);

		if (days <= 0) {
			throw new RentalOperationException("Rental duration must be greater than zero.");
		}

	
		Customer theCustomer = null;
		for (Customer c : customers) {
			if (c.getCustomerId().equals(customerId)) {
				theCustomer = c;
				break;
			}
		}

		
		Vehicle theVehicle = null;
		for (Vehicle v : vehicles) {
			if (v.getVehicleId().equals(vehicleId)) {
				theVehicle = v;
				break;
			}
		}

		LocalDate startDate = LocalDate.now();
		LocalDate expectedEndDate = startDate.plusDays(days - 1); 

		// Generate rental ID
		String rentalId = "R" + (rentals.size() + 1);

		Rental rental = new Rental(rentalId, customerId, vehicleId, startDate, expectedEndDate);

		// Update system state
		theVehicle.setAvailable(false);
		theCustomer.setActiveRentals(theCustomer.getActiveRentals() + 1);
		rentals.add(rental);

		System.out.println("\n------------------------------------------");
		System.out.println("Rental started for customer:: " + theCustomer.getName());
		System.out.println( "Vehicle:: "+ theVehicle.getModel());
		System.out.println("Rental ID:: " + rentalId);
		System.out.println("------------------------------------------\n");

		return rental;
	}

	
	public double endRental(String rentalId, LocalDate actualEndDate) throws RentalOperationException {

		if (rentalId == null || rentalId.trim().isEmpty()) {
			throw new RentalOperationException("Rental ID cannot be empty.");
		}

		if (actualEndDate == null) {
			throw new RentalOperationException("Actual return date cannot be null.");
		}

		
		Rental found = null;
		for (Rental r : rentals) {
			if (r.getRentalId().equals(rentalId)) {
				found = r;
				break;
			}
		}

		if (found == null) {
			throw new RentalOperationException("Rental does not exist or Rental ID is Invalid.");
		}

		if (found.isClosed()) {
			throw new RentalOperationException("Rental is already closed.");
		}

		LocalDate start = found.getStartDate();
		LocalDate expected = found.getExpectedEndDate();
		LocalDate actual = actualEndDate;

		found.setActualEndDate(actualEndDate);
		found.setClosed(true);

		
		Vehicle rentedVehicle = null;
		for (Vehicle v : vehicles) {
			if (v.getVehicleId().equals(found.getVehicleId())) {
				rentedVehicle = v;
				break;
			}
		}
		
		rentedVehicle.setAvailable(true);

		
		Customer cust = null;
		for (Customer c : customers) {
			if (c.getCustomerId().equals(found.getCustomerId())) {
				cust = c;
				c.setActiveRentals(Math.max(0, c.getActiveRentals() - 1));
				break;
			}
		}

		
		String customerType = cust.getCustomerType();
		double baseRate = rentedVehicle.getBaseDailyRate();

		
		double total = calculateCharge(found, customerType, baseRate);

		found.setTotalCharge(total);


		long rentalDays = ChronoUnit.DAYS.between(start, actual) + 1;
		long lateDays = 0;
		if (actual.isAfter(expected)) {
			lateDays = ChronoUnit.DAYS.between(expected, actual);
		}

		double baseCharge = rentalDays * baseRate;
		double lateFee = lateDays * (0.25 * baseRate);
		double discount = (customerType.equalsIgnoreCase("CORPORATE")) ? 0.15 * (baseCharge + lateFee) : 0;

		System.out.println("\n==========================================");
		System.out.println("                RENTAL BILL               ");
		System.out.println("==========================================");
		System.out.println("Customer ID      :: " + cust.getCustomerId());
		System.out.println("Vehicle ID       :: " + rentedVehicle.getVehicleId());
		System.out.println("Vehicle Model    :: " + rentedVehicle.getModel());
		System.out.println("------------------------------------------");
		System.out.println("Start Date       :: " + start);
		System.out.println("Expected Return  :: " + expected);
		System.out.println("Actual Return    :: " + actual);
		System.out.println("------------------------------------------");
		System.out.println("Days Rented      :: " + rentalDays);
		System.out.println("Late Days        :: " + lateDays);
		System.out.println("Base Rate (Rs.)  :: " + baseRate);
		System.out.println("Base Charge (Rs.):: " + baseCharge);
		System.out.println("Late Fee (Rs.)   :: " + lateFee);
		if (discount > 0) {
			System.out.println("Discount (10%)   :: - " + discount);
		}
		System.out.println("------------------------------------------");
		System.out.println("TOTAL CHARGE (Rs):: " + total);
		System.out.println("==========================================\n");

		return total;
	}

	private double calculateCharge(Rental rental, String customerType, double baseDailyRate) throws RentalOperationException {

		LocalDate start = rental.getStartDate();
		LocalDate expected = rental.getExpectedEndDate();
		LocalDate actual = rental.getActualEndDate();

		if (actual.isBefore(start)) {
			throw new RentalOperationException("Actual return date cannot be before start date.");
		}

		long rentalDays = ChronoUnit.DAYS.between(start, actual) + 1;
		double baseCharge = rentalDays * baseDailyRate;

		long lateDays = 0;
		if (actual.isAfter(expected)) {
			lateDays = ChronoUnit.DAYS.between(expected, actual);
		}

		double lateFee = lateDays * (0.25 * baseDailyRate);

		double subtotal = baseCharge + lateFee;

		double discount = 0;
		if (customerType.equalsIgnoreCase("CORPORATE")) {
			discount = 0.15 * subtotal;
		}

		double total = subtotal - discount;

		return Math.round(total * 100.0) / 100.0;
	}
}
