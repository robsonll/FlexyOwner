package com.example.flexyowner.ModelClasses;

import java.util.List;

public class Business {

    private String id;
    private String name;
    private String url;
    private String address;
    private String phoneNumber;
    private String email;
    private List<BusinessOwner> owners;
    private BusinessConfiguration businessConfiguration;

    public Business() {
    }

    public Business(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Business(String id, String name, String url, String address, String phoneNumber, String email, List<BusinessOwner> owners, BusinessConfiguration businessConfiguration) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.owners = owners;
        this.businessConfiguration = businessConfiguration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BusinessOwner> getOwners() {
        return owners;
    }

    public void setOwners(List<BusinessOwner> owners) {
        this.owners = owners;
    }

    public BusinessConfiguration getBusinessConfiguration() {
        return businessConfiguration;
    }

    public void setBusinessConfiguration(BusinessConfiguration businessConfiguration) {
        this.businessConfiguration = businessConfiguration;
    }
}
