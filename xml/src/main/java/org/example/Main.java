package org.example;

import org.example.lab1.Person;
import org.example.lab1.PersonsParser;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Person> personsById = lab1();
            lab2(personsById);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Person> lab1() throws XMLStreamException, FileNotFoundException {
        String inputPath = "people.xml";
        PersonsParser parser = new PersonsParser();
        Map<String, Person> personsById = parser.convert(inputPath);

        consistencyCheck(personsById);

        return personsById;
    }

    private static void lab2(Map<String, Person> personsById){

    }

    private static void consistencyCheck(Map<String, Person> personsById){

        int er0 = 0, er1 = 0, er2 = 0, er3 = 0;
        for(Person person : personsById.values()){
            if(person.childrenAmount == null){
                er0++;
            }
            else if(person.childrenAmount != person.sonsIds.size() + person.daughtersIds.size()){
                er2++;
            }
            if(person.siblingsAmount == null){
                er1++;
            }
            else if(person.siblingsAmount != person.brothersIds.size() + person.sistersIds.size()){
                er3++;
            }
        }

        System.out.println("Total persons: " + personsById.size());
        System.out.println("Persons without childrenAmount: " + er0);
        System.out.println("Persons without siblingsAmount: " + er1);
        System.out.println("Persons with wrong amount of children: " + er2);
        System.out.println("Persons with wrong amount of siblings: " + er3);

    }
}