package fr.istic.sir.entities;

import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("AddressAndDateSurvey")
public class AddressAndDateSurvey extends Survey {

    private List<Date> dates;

    private List<Address> addresses;

    @JsonManagedReference
    @OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
}
