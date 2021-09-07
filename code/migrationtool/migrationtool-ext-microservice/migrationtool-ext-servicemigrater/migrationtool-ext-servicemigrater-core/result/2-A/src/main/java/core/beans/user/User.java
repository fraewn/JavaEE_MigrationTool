package core.beans.user;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
@DiscriminatorValue(value = "USER")
public class User {

    @Id
    private String id;

    private String firstName;

    @ManyToOne
    @JoinColumn(name = "id_group")
    private UserGroup group;

    private String lastName;

    @Column(name = "EMAIL")
    private String mail;

    private String password;

    @Column(name = "PASSWORD_SALT")
    private String salt;

    private String userExtID;

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the group
     */
    public UserGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroup group) {
        this.group = group;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the userExtID
     */
    public String getUserExtID() {
        return userExtID;
    }

    /**
     * @param userExtID the userExtID to set
     */
    public void setUserExtID(String userExtID) {
        this.userExtID = userExtID;
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
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
