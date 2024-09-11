package org.specification.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfigMapping {
    /**
     * Index of the column in the CSV file
     */
    private Integer index;
    /**
     * Name of the column in the CSV file
     */
    private String custom;
    /**
     * Name of the column in the CSV file
     */
    private String original;

    public ConfigMapping(Integer index, String custom, String original) {
        this.index = index;
        this.custom = custom;
        this.original = original;
    }


}
