package com.georent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="{register.lot.price.invalid}")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private GeoRentUser geoRentUser;

    @OneToOne(mappedBy = "lot",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Description description;

    @OneToOne(mappedBy = "lot",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Coordinates coordinates;

    public void setDescription(Description description) {
        if (description == null) {
            if (this.description != null) {
                this.description.setLot(null);
            }
        } else {
            description.setLot(this);
        }
        this.description = description;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            if (this.coordinates != null) {
                this.coordinates.setLot(null);
            }
        } else {
            coordinates.setLot(this);
        }
        this.coordinates = coordinates;
    }
}
