package models;

public class Response {
    private String message;
    private String siteId;

    public Response(String message, String siteId) {
        this.message = message;
        this.siteId = siteId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
