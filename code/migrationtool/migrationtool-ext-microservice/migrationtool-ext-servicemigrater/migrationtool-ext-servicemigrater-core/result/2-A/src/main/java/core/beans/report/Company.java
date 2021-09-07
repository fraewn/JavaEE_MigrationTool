package core.beans.report;

import bean.enums.BusinessType;
import bean.enums.Locations;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Company")
public class Company {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private BusinessType business;

    private Date foundation;

    @Enumerated(EnumType.STRING)
    private Locations location;

    private String name;

    /**
     * @return the foundation
     */
    public Date getFoundation() {
        return foundation;
    }

    /**
     * @param foundation the foundation to set
     */
    public void setFoundation(Date foundation) {
        this.foundation = foundation;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the location
     */
    public Locations getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Locations location) {
        this.location = location;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the business
     */
    public BusinessType getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(BusinessType business) {
        this.business = business;
    }
}
