package com.postgres.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel(description = "Class representing a address in the application.")
@Entity
@Table(name = "address")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter @Setter @Builder @Data
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(notes = "Address line 1 of the contact.",
            example = "888 Constantine Ave, #54", required = false, position = 0)
    @Size(max = 50)
    private String address1;

    @ApiModelProperty(notes = "Address line 2 of the contact.",
            example = "San Angeles", required = false, position = 1)
    @Size(max = 50)
    private String address2;

    @ApiModelProperty(notes = "Address line 3 of the contact.",
            example = "Florida", required = false, position = 2)
    @Size(max = 50)
    private String address3;

    @ApiModelProperty(notes = "Postal code of the contact.",
            example = "32106", required = false, position = 3)
    @Size(max = 20)
    private String postalCode;
}
