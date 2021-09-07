package core.beans.user;

import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "USER_GROUP")
public class UserGroup {

    @Id
    private String id;

    private String groupExtID;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "GROUP_PERMISSIONS", joinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_perm", referencedColumnName = "id"))
    private List<Permissions> permissions;

    /**
     * @return the groupExtID
     */
    public String getGroupExtID() {
        return groupExtID;
    }

    /**
     * @param groupExtID the groupExtID to set
     */
    public void setGroupExtID(String groupExtID) {
        this.groupExtID = groupExtID;
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
     * @return the permissions
     */
    public List<Permissions> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(List<Permissions> permissions) {
        this.permissions = permissions;
    }
}
