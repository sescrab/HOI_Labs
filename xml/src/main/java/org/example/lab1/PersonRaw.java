package org.example.lab1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonRaw {
    public String id;
    public String firstName;
    public String lastName;
    public String gender;

    public String spouseId;
    public String spouseName;
    public Set<String> parentsIds = new HashSet<>();
    public Set<String> parentsNames = new HashSet<>();
    public Set<String> siblingsIds = new HashSet<>();
    public Set<String> siblingsNames = new HashSet<>();
    public Set<String> childrenIds = new HashSet<>();
    public Set<String> childrenNames = new HashSet<>();

    public Integer childrenAmount;
    public Integer siblingsAmount;

    public String getName(){
        return firstName + " " + lastName;
    }
    public boolean isMale(){
        if(gender == null){
            throw new RuntimeException("Gender is not specified");
        }
        return gender.equals("M");
    }
    public boolean isSpouse(String id, Map<String, Set<String>> nameToIds){
        if(spouseName != null) {
            for (String personId : nameToIds.get(spouseName)) {
                if (id.equals(personId)) {
                    return true;
                }
            }
        }
        return id.equals(spouseId);
    }
    public boolean isParent(String id, Map<String, Set<String>> nameToIds){
        for(String name : childrenNames) {
            for (String personId : nameToIds.get(name)) {
                if (id.equals(personId)) {
                    return true;
                }
            }
        }
        for(String personId : childrenIds){
            if(id.equals(personId)){
                return true;
            }
        }
        return false;
    }
    public boolean isSibling(String id, Map<String, Set<String>> nameToIds){
        for(String name : siblingsNames) {
            for (String personId : nameToIds.get(name)) {
                if (id.equals(personId)) {
                    return true;
                }
            }
        }
        for(String personId : siblingsIds){
            if(id.equals(personId)){
                return true;
            }
        }
        return false;
    }
    public boolean isChild(String id, Map<String, Set<String>> nameToIds){
        for(String name : parentsNames) {
            for (String personId : nameToIds.get(name)) {
                if (id.equals(personId)) {
                    return true;
                }
            }
        }
        for(String personId : parentsIds){
            if(id.equals(personId)){
                return true;
            }
        }
        return false;
    }

    public Set<String> collectParentIds(Map<String, Set<String>> nameToIds, Map<String, PersonRaw> personsById){
        Set<String> ans = new HashSet<>(parentsIds);
        for(String name : parentsNames){
            for(String id : nameToIds.get(name)){
                PersonRaw person = personsById.get(id);
                if(person.isParent(this.id, nameToIds)){
                    ans.add(id);
                }
            }
        }
        return ans;
    }
    public Set<String> collectSiblingsIds(Map<String, Set<String>> nameToIds, Map<String, PersonRaw> personsById){
        Set<String> ans = new HashSet<>(siblingsIds);
        for(String name : siblingsNames){
            for(String id : nameToIds.get(name)){
                PersonRaw person = personsById.get(id);
                if(person.isSibling(this.id, nameToIds)){
                    ans.add(id);
                }
            }
        }
        return ans;
    }
    public Set<String> collectChildrenIds(Map<String, Set<String>> nameToIds, Map<String, PersonRaw> personsById){
        Set<String> ans = new HashSet<>(childrenIds);
        for(String name : childrenNames){
            for(String id : nameToIds.get(name)){
                PersonRaw person = personsById.get(id);
                if(person.isChild(this.id, nameToIds)){
                    ans.add(id);
                }
            }
        }
        return ans;
    }

    public static boolean hasNoContradiction(PersonRaw a, PersonRaw b, Map<String, Set<String>> nameToIds, Map<String, PersonRaw> personsById){
        if(a.id != null && b.id != null && !a.id.equals(b.id)){return false;}
        if(a.firstName != null && b.firstName != null && !a.firstName.equals(b.firstName)){return false;}
        if(a.lastName != null && b.lastName != null && !a.lastName.equals(b.lastName)){return false;}
        if(a.gender != null && b.gender != null && !a.gender.equals(b.gender)){return false;}
        if(a.spouseId != null && b.spouseId != null && !a.spouseId.equals(b.spouseId)){return false;}
        if(a.spouseName != null && b.spouseName != null && !a.spouseName.equals(b.spouseName)){return false;}
        if(a.childrenAmount != null && b.childrenAmount != null && !a.childrenAmount.equals(b.childrenAmount)){return false;}
        if(a.siblingsAmount != null && b.siblingsAmount != null && !a.siblingsAmount.equals(b.siblingsAmount)){return false;}



        if(b.spouseId != null){
            if(b.spouseId.equals(a.id)) {
                return false;
            }
            PersonRaw spouse = personsById.get(b.spouseId);
            if((spouse.spouseId != null && !spouse.spouseId.equals(a.id))
                    || (spouse.spouseName != null && !spouse.spouseName.equals(a.getName()))){
                return false;
            }
        }
        if(b.spouseName != null){
            boolean possible = false;
            for(String spouseId : nameToIds.get(b.spouseName)){
                PersonRaw spouse = personsById.get(spouseId);
                if(!((spouse.spouseId != null && !spouse.spouseId.equals(a.id))
                        || (spouse.spouseName != null && !spouse.spouseName.equals(a.getName())))){
                    possible = true;
                    break;
                }
            }
            if(!possible){
                return false;
            }
        }

        Integer nextChildrenAmount = a.childrenAmount;
        if(b.childrenAmount != null){ nextChildrenAmount = b.childrenAmount;}
        Integer nextSiblingAmount = a.siblingsAmount;
        if(b.siblingsAmount != null){ nextSiblingAmount = b.siblingsAmount;}

        PersonRaw newPerson = new PersonRaw();
        newPerson.siblingsIds = new HashSet<>(a.siblingsIds);
        newPerson.siblingsIds.addAll(b.siblingsIds);
        newPerson.siblingsNames = new HashSet<>(a.siblingsNames);
        newPerson.siblingsNames.addAll(b.siblingsNames);
        newPerson.childrenIds = new HashSet<>(a.childrenIds);
        newPerson.childrenIds.addAll(b.childrenIds);
        newPerson.childrenNames = new HashSet<>(a.childrenNames);
        newPerson.childrenNames.addAll(b.childrenNames);

        Set<String> childrenIdsCollected = newPerson.collectChildrenIds(nameToIds, personsById);
        Set<String> siblingsIdsCollected = newPerson.collectSiblingsIds(nameToIds, personsById);
        if(nextChildrenAmount != null && nextChildrenAmount < childrenIdsCollected.size()){return false;}
        if(nextSiblingAmount != null && nextSiblingAmount < siblingsIdsCollected.size()){return false;}
        return true;
    }

    public static PersonRaw mergePersons(PersonRaw a, PersonRaw b){
        if(a.id == null && b.id != null){a.id = b.id;}
        if(a.firstName == null && b.firstName != null){a.firstName = b.firstName;}
        if(a.lastName == null && b.lastName != null){a.lastName = b.lastName;}
        if(a.gender == null && b.gender != null){a.gender = b.gender;}
        if(a.spouseId == null && b.spouseId != null){a.spouseId = b.spouseId;}
        if(a.spouseName == null && b.spouseName != null){a.spouseName = b.spouseName;}
        if(a.childrenAmount == null && b.childrenAmount != null){a.childrenAmount = b.childrenAmount;}
        if(a.siblingsAmount == null && b.siblingsAmount != null){a.siblingsAmount = b.siblingsAmount;}
        a.parentsIds.addAll(b.parentsIds);
        a.parentsNames.addAll(b.parentsNames);
        a.siblingsIds.addAll(b.siblingsIds);
        a.siblingsNames.addAll(b.siblingsNames);
        a.childrenIds.addAll(b.childrenIds);
        a.childrenNames.addAll(b.childrenNames);

        return a;
    }
}
