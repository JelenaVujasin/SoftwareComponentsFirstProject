package org.specification.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
/**
 * Class representing a place in the schedule
 */
public class Place {
    /**
     * Name of the place
     */
    private String name;
    /**
     * Capacity of the place
     */
    private int capacity;
    /**
     * Number of computers in the place
     */
    private int computer;
    /**
     * True if the place has a projector
     */
    private boolean projector;
    /**
     * True if the place has a smart board
     */
    private boolean smartBoard;
    /**
     * Map of the additional data
     */
    private Map<String,String> additional;

    public Place() {
        additional = new HashMap<>();
    }

    public Place(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", capacity=" + capacity +
                ", computer=" + computer +
                ", projector=" + projector +
                ", smartBoard=" + smartBoard +
                ", additional=" + additional +
                '}';
    }
}

