package com.example.flexyowner.ModelClasses;

public class Product {

    private String id;
    private String businessId;
    private String name;
    private String description;
    private Long price;
    private String image;
    private String type;


    public Product(){}

    public Product(String name, Long price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product(String id, String businessId, String name, String description, Long price, String image, String type) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.type = type;
    }

    public Product(String id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
