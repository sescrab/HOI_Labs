package org.example.lab1;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Person {
    public String id;
    public String firstName;
    public String lastName;
    public String gender;
    public Integer childrenAmount;
    public Integer siblingsAmount;

    public String spouseId;
    public String fatherId;
    public String motherId;
    public Set<String> brothersIds = new HashSet<>();
    public Set<String> sistersIds = new HashSet<>();
    public Set<String> sonsIds = new HashSet<>();
    public Set<String> daughtersIds = new HashSet<>();

    public Person(PersonRaw rawData, Map<String, Set<String>> nameToIds, Map<String, PersonRaw> personsById){
        this.id = rawData.id;
        this.firstName = rawData.firstName;
        this.lastName = rawData.lastName;
        this.gender = rawData.gender;
        this.childrenAmount = rawData.childrenAmount;
        this.siblingsAmount = rawData.siblingsAmount;

        if(rawData.spouseName != null) {
            for (String id : nameToIds.get(rawData.spouseName)) {
                if (personsById.get(id).isSpouse(rawData.id, nameToIds)) {
                    this.spouseId = id;
                }
            }
        }
        if(rawData.spouseId != null) {this.spouseId = rawData.spouseId;}

        for(String id : rawData.collectParentIds(nameToIds, personsById)){
            if(personsById.get(id).isMale()){
                this.fatherId = id;
            }
            else{
                this.motherId = id;
            }
        }

        for(String id : rawData.collectSiblingsIds(nameToIds, personsById)){
            if(personsById.get(id).isMale()){
                this.brothersIds.add(id);
            }
            else{
                this.sistersIds.add(id);
            }
        }

        for(String id : rawData.collectChildrenIds(nameToIds, personsById)){
            if(personsById.get(id).isMale()){
                this.sonsIds.add(id);
            }
            else{
                this.daughtersIds.add(id);
            }
        }
    }
}
