package com.aquino.webParser.speed;

import java.sql.Clob;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity(name = "Descriptions")
public class Description {

    @Id
    private long descriptionId;

    @OneToOne
    @MapsId
    private SpeedBook book;
    
    @Lob
    private Clob description; 
}