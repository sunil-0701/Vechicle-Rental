package com.wipro.vrs.entity;
public class Customer {

    private String customerId;
    private String name;
    private String licenseNumber;
    private String customerType;
    private int activeRentals;

    public Customer(String customerId, String name, String licenseNumber,String customerType, int activeRentals) {
    	
        this.customerId = customerId;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.customerType = customerType;
        this.activeRentals = activeRentals;
    }

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public int getActiveRentals() {
		return activeRentals;
	}

	public void setActiveRentals(int activeRentals) {
		this.activeRentals = activeRentals;
	}
    
}