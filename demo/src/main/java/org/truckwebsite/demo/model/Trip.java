package org.truckwebsite.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {

    private String userId;
    private String id;
    private String departureCity;
    private String destinationCity;
    private String tripDate;
    private String loadType;
    private int capacity;
    private String phone;
}
