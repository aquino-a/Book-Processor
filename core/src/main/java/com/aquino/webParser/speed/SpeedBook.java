package com.aquino.webParser.speed;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "Book")
public class SpeedBook {
    
    @Id
    private long speedBookId;
}
