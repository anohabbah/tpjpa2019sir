package fr.istic.sir.entities;

import org.codehaus.jackson.annotate.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "addresses")
public class Address implements Serializable {
    private long id;

    private String location;

    private Survey survey;

    private List<User> voters;

    public Address() {
    }

    public Address(String location) {
        this.location = location;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "address_user",
            joinColumns = @JoinColumn(name = "address_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "email"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"address_id", "user_id"})
    )
    public List<User> getVoters() {
        return voters;
    }

    public void setVoters(List<User> voters) {
        this.voters = voters;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}
