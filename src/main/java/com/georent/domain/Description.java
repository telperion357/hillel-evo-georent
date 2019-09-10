package com.georent.domain;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Indexed
@Data
@Entity
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    private Lot lot;

    @CollectionTable(name="picture_ids")
    private ArrayList<Long> pictureIds = new ArrayList<>();

    @Field
    @Column(name = "lot_name")
    @NotBlank(message="{register.lot.Name.invalid}")
    private String lotName;

    @Field
    @Column(name = "lot_description")
    @NotBlank(message="{register.lot.Description.invalid}")
    private String lotDescription;
}
