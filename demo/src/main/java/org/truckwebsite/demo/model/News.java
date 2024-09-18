package org.truckwebsite.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private String userId;
    private String id;
    private String titleNews;
    private Date newsDate;
    private String content;
}
