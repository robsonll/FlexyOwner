package com.example.flexyowner.ModelClasses;

import java.util.List;

public class BusinessConfiguration {

    private String backgroundImage;
    private Boolean showAddOns;
    private Boolean showDrinks;
    private List<Message> messageList;
    private List<PaymentMethod> acceptedPaymentMethods;

    public BusinessConfiguration() {
    }

    public BusinessConfiguration(String backgroundImage, Boolean showAddOns, Boolean showDrinks, List<Message> messageList, List<PaymentMethod> acceptedPaymentMethods) {
        this.backgroundImage = backgroundImage;
        this.showAddOns = showAddOns;
        this.showDrinks = showDrinks;
        this.messageList = messageList;
        this.acceptedPaymentMethods = acceptedPaymentMethods;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Boolean getShowAddOns() {
        return showAddOns;
    }

    public void setShowAddOns(Boolean showAddOns) {
        this.showAddOns = showAddOns;
    }

    public Boolean getShowDrinks() {
        return showDrinks;
    }

    public void setShowDrinks(Boolean showDrinks) {
        this.showDrinks = showDrinks;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<PaymentMethod> getAcceptedPaymentMethods() {
        return acceptedPaymentMethods;
    }

    public void setAcceptedPaymentMethods(List<PaymentMethod> acceptedPaymentMethods) {
        this.acceptedPaymentMethods = acceptedPaymentMethods;
    }
}
