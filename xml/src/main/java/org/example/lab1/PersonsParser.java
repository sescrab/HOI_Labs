package org.example.lab1;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class PersonsParser {
    XMLStreamReader reader;

    public Map<String, Person> convert(String path) throws XMLStreamException, FileNotFoundException {
        ArrayList<PersonRaw> personsRawData = parse(path);

        Map<String, PersonRaw> personsById = new HashMap<>();
        Map<String, Set<PersonRaw>> personsByName = new HashMap<>();
        Map<String, Set<String>> nameToIds = new HashMap<>();
        for(PersonRaw person : personsRawData){
            if(person.id != null){
                personsById.merge(person.id, person, PersonRaw::mergePersons);
            }
            else{
                if(!personsByName.containsKey(person.getName())){
                    personsByName.put(person.getName(), new HashSet<>());
                }
                personsByName.get(person.getName()).add(person);
            }
        }
        for(PersonRaw person : personsById.values()){
            if(!nameToIds.containsKey(person.getName())){
                nameToIds.put(person.getName(), new HashSet<>());
            }
            (nameToIds.get(person.getName())).add(person.id);
        }

        for (PersonRaw person : personsById.values()) {
            if (personsByName.containsKey(person.getName())) {
                for (PersonRaw personFound : personsByName.get(person.getName())) {
                    personsById.merge(person.id, personFound, PersonRaw::mergePersons);
                }
            }
        }

        Map<String, Person> res = new HashMap<>();
        for(PersonRaw person : personsById.values()){
            res.put(person.id, new Person(person, nameToIds, personsById));
        }
        return res;
    }
    private ArrayList<PersonRaw> parse(String path) throws FileNotFoundException, XMLStreamException {
        ArrayList<PersonRaw> ans = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createXMLStreamReader(new FileInputStream(new File(path)));
        PersonRaw currentPerson = null;
        String currentElement = null;

        for (;reader.hasNext(); reader.next()) {
            int eventType = reader.getEventType();
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                currentElement = reader.getLocalName();
                if ("person".equals(currentElement)) {
                    currentPerson = new PersonRaw();
                    String id = getAttrByName("id");
                    if (id != null) {
                        currentPerson.id = id;
                    }
                    String name = getAttrByName("name");
                    if(name != null){
                        String[] fullname = name.split("\\s+");
                        if(fullname.length >= 1){
                            currentPerson.firstName = fullname[0];
                        }
                        if(fullname.length >= 2){
                            currentPerson.lastName = fullname[1];
                        }
                    }
                } else if ("id".equals(currentElement)) {
                    String id = getAttrByName("value");
                    if (id != null) {
                        currentPerson.id = id;
                    }
                } else if ("firstname".equals(currentElement)) {
                    String firstname = getAttrByName("value");
                    if(firstname != null){
                        currentPerson.firstName = firstname;
                    }
                } else if ("surname".equals(currentElement)) {
                    String lastname = getAttrByName("value");
                    if(lastname != null){
                        currentPerson.lastName = lastname;
                    }
                } else if ("gender".equals(currentElement)) {
                    String gender = getAttrByName("value");
                    if(gender != null){
                        if(gender.equals("male")){
                            currentPerson.gender = "M";
                        }
                        else if(gender.equals("female")){
                            currentPerson.gender = "F";
                        }
                    }
                } else if ("wife".equals(currentElement) || "husband".equals(currentElement)) {
                    String spouseId = getAttrByName("value");
                    if(spouseId != null){
                        currentPerson.spouseId = spouseId;
                    }
                } else if ("spouce".equals(currentElement)){
                    String spouseName = getAttrByName("value");
                    if(spouseName != null && !spouseName.equalsIgnoreCase("NONE")){
                        currentPerson.spouseName = processName(spouseName);
                    }
                } else if ("parent".equals(currentElement)) {
                    String parentId = getAttrByName("value");
                    if(parentId != null && !parentId.equalsIgnoreCase("UNKNOWN")){
                        currentPerson.parentsIds.add(parentId);
                    }
                } else if ("siblings".equals(currentElement)) {
                    String siblingsStr = getAttrByName("val");
                    if(siblingsStr != null){
                        for(String id : siblingsStr.split("\\s+")){
                            currentPerson.siblingsIds.add(id);
                        }
                    }
                }  else if ("son".equals(currentElement) || "daughter".equals(currentElement)) {
                    String id = getAttrByName("id");
                    if(id != null){
                        currentPerson.childrenIds.add(id);
                    }
                } else if ("children-number".equals(currentElement)) {
                    String value = getAttrByName("value");
                    if(value != null){
                        currentPerson.childrenAmount = Integer.parseInt(value);
                    }
                } else if ("siblings-number".equals(currentElement)) {
                    String value = getAttrByName("value");
                    if(value != null){
                        currentPerson.siblingsAmount = Integer.parseInt(value);
                    }
                }
            } else if (eventType == XMLStreamConstants.CHARACTERS) {
                String text = reader.getText().trim();
                if ("firstname".equals(currentElement) || "first".equals(currentElement)) {
                    currentPerson.firstName = text;
                } else if ("family-name".equals(currentElement) || "family".equals(currentElement)) {
                    currentPerson.lastName = text;
                } else if ("gender".equals(currentElement)){
                    currentPerson.gender = text;
                } else if ("father".equals(currentElement) || "mother".equals(currentElement)){
                    currentPerson.parentsNames.add(processName(text));
                } else if ("parent".equals(currentElement)){
                    //Встречается только UNKNOWN, нечего делать в этом случае
                } else if ("brother".equals(currentElement) || "sister".equals(currentElement)){
                    currentPerson.siblingsNames.add(processName(text));
                } else if ("child".equals(currentElement)){
                    currentPerson.childrenNames.add(processName(text));
                }
            } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                currentElement = reader.getLocalName();
                if ("person".equals(currentElement)) {
                    ans.add(currentPerson);
                    currentPerson = null;
                }
                currentElement = null;
            }
        }

        reader.close();
        return ans;
    }

    private String getAttrByName(String name){
        String value = reader.getAttributeValue(null, name);
        if(value != null){
            return value.trim();
        }
        return value;
    }

    private String processName(String name){
        StringBuilder res = new StringBuilder();
        for (String word : name.split("\\s+")) {
            res.append(word.trim());
            res.append(" ");
        }
        return res.toString().trim();
    }

}
