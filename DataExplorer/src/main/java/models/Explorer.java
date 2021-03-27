package models;

public class Explorer {

    private int id;
    private String parentSite;
    private String name;
    private String host;
    private String siteId;

    public Explorer(int id, String parentSite, String name, String host, String siteId) {
        this.id = id;
        this.parentSite = parentSite;
        this.name = name;
        this.host = host;
        this.siteId = siteId;
    }

    public Explorer() {

    }

    public Explorer(String parentSite, String name) {
        this.parentSite = parentSite;
        this.name = name;
    }

    public Explorer(int id, String parentSite, String name) {
        this.id = id;
        this.parentSite = parentSite;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentSite() {
        return this.parentSite;
    }

    public void setParentSite(String parentSite) {
        this.parentSite = parentSite;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
