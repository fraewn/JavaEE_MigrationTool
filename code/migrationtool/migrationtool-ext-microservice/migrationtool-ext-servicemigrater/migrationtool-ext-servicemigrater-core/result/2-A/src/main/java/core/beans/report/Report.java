package core.beans.report;

import core.beans.user.User;
import java.util.Date;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Report")
public class Report {

    @Id
    private String id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "id_company")
    private Company company;

    private Date createDate;

    @ManyToMany
    @JoinTable(name = "LIKES", joinColumns = @JoinColumn(name = "ID_REPORT"), inverseJoinColumns = @JoinColumn(name = "ID_LIKER"))
    private Set<User> liker;

    private int likes;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "id_reporter")
    private User reporter;

    private String title;

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
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the likes
     */
    public int getLikes() {
        return likes;
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @return the liker
     */
    public Set<User> getLiker() {
        return liker;
    }

    /**
     * @param liker the liker to set
     */
    public void setLiker(Set<User> liker) {
        this.liker = liker;
    }

    /**
     * @return the reporter
     */
    public User getReporter() {
        return reporter;
    }

    /**
     * @param reporter the reporter to set
     */
    public void setReporter(User reporter) {
        this.reporter = reporter;
    }
}
