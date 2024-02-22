package com.aquino.webParser.speed;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity(name = "Covers")
public class Cover {
    
    @Id
    private long coverId;
    
    @OneToOne
    @MapsId
    private SpeedBook book;

    @Lob
    private Blob cover; 
}
