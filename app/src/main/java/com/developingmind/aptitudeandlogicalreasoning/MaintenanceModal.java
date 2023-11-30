package com.developingmind.aptitudeandlogicalreasoning;

import com.google.firebase.Timestamp;

public class MaintenanceModal {
    private Boolean status;
    private String message;
    private Timestamp till;
    private String privacyPolicy;


    public MaintenanceModal(){}

    public MaintenanceModal(Boolean status, String message, Timestamp validTill,String privacyPolicy) {
        this.status = status;
        this.message = message;
        this.till = validTill;
        this.privacyPolicy = privacyPolicy;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTill() {
        return till;
    }

    public void setTill(Timestamp validTill) {
        this.till = validTill;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }
}
