package com.jfxtutor.data.curriculum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDef {
    public String id;
    public String description;
    /** Raw assertion string, e.g. containsLabeledInside(text="Submit", parentType=VBox) */
    public String assertion;
}
