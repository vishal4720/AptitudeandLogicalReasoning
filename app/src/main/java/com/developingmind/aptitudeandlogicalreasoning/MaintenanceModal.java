package com.developingmind.aptitudeandlogicalreasoning;

import com.google.firebase.Timestamp;

public class MaintenanceModal {
    private Boolean status;
    private String message;
    private Timestamp till;

    public MaintenanceModal(){}

    public MaintenanceModal(Boolean status, String message, Timestamp validTill) {
        this.status = status;
        this.message = message;
        this.till = validTill;
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


}
