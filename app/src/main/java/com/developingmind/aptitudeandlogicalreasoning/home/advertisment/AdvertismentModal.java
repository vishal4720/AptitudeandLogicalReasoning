package com.developingmind.aptitudeandlogicalreasoning.home.advertisment;

public class AdvertismentModal {
    private String imageUrl,redirctUrl;

    public AdvertismentModal(String imageUrl, String redirctUrl) {
        this.imageUrl = imageUrl;
        this.redirctUrl = redirctUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRedirctUrl() {
        return redirctUrl;
    }

    public void setRedirctUrl(String redirctUrl) {
        this.redirctUrl = redirctUrl;
    }
}
