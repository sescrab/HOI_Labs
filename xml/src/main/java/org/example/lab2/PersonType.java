package org.example.lab2;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PersonType {

    @XmlAttribute(required = true)
    public String id;

    @XmlElement(required = true)
    public String firstName;

    @XmlElement(required = true)
    public String lastName;

    @XmlElement(required = true)
    public Gender gender;

    public Integer childrenAmount;
    public Integer siblingsAmount;

    public String spouseRef;
    public String fatherRef;
    public String motherRef;

    public Brothers brothers;
    public Sisters sisters;
    public Sons sons;
    public Daughters daughters;

    public static class Brothers {
        public List<String> brotherRef = new ArrayList<>();
    }

    public static class Sisters {
        public List<String> sisterRef = new ArrayList<>();
    }

    public static class Sons {
        public List<String> sonRef = new ArrayList<>();
    }

    public static class Daughters {
        public List<String> daughterRef = new ArrayList<>();
    }

    @XmlEnum
    public enum Gender {
        @XmlEnumValue("M") MALE,
        @XmlEnumValue("F") FEMALE,
        @XmlEnumValue("UNKNOWN") UNKNOWN
    }

    public void SetGender(String gender){
        if(gender.equals("M")){
            this.gender = Gender.MALE;
        }
        else if(gender.equals("F")){
            this.gender = Gender.FEMALE;
        }
        else{
            this.gender = Gender.UNKNOWN;
        }
    }
}