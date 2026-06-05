package org.example;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.example.lab1.Person;
import org.example.lab1.PersonsParser;
import org.example.lab2.People;
import org.example.lab2.PersonType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.validation.SchemaFactory;
import java.io.File;
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
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
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

    private static void lab2(Map<String, Person> personsById) throws JAXBException, SAXException {
        People people = new People();
        for (Person p : personsById.values()) {
            PersonType personType = lab1ToLab2PersonClassConvert(p);
            people.persons.add(personType);
        }

        writeToXmlWithSchema(people, "people_result.xml", "people_scheme.xsd");
        System.out.println("Xml writing done!");
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

    private static PersonType lab1ToLab2PersonClassConvert(Person person){
        PersonType res = new PersonType();
        res.id = person.id;
        res.firstName = person.firstName;
        res.lastName = person.lastName;
        res.SetGender(person.gender);
        res.childrenAmount = person.childrenAmount;
        res.siblingsAmount = person.siblingsAmount;

        if (person.spouseId != null){
            res.spouseRef = person.spouseId;
        }
        if (person.motherId != null){
            res.motherRef = person.motherId;
        }
        if (person.fatherId != null){
            res.fatherRef = person.fatherId;
        }

        if (!person.brothersIds.isEmpty()){
            PersonType.Brothers brothers = new PersonType.Brothers();
            brothers.brotherRef.addAll(person.brothersIds);
            res.brothers = brothers;
        }
        if (!person.sistersIds.isEmpty()){
            PersonType.Sisters sisters = new PersonType.Sisters();
            sisters.sisterRef.addAll(person.sistersIds);
            res.sisters = sisters;
        }
        if (!person.sonsIds.isEmpty()){
            PersonType.Sons sons = new PersonType.Sons();
            sons.sonRef.addAll(person.sonsIds);
            res.sons = sons;
        }
        if (!person.daughtersIds.isEmpty()){
            PersonType.Daughters daughters = new PersonType.Daughters();
            daughters.daughterRef.addAll(person.daughtersIds);
            res.daughters = daughters;
        }

        return res;
    }
    private static void writeToXmlWithSchema(People people, String outputPath, String schemaPath) throws JAXBException, SAXException {
        JAXBContext jc = JAXBContext.newInstance(People.class);
        Marshaller writer = jc.createMarshaller();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        writer.setSchema(schemaFactory.newSchema(new File(schemaPath)));
        writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        writer.marshal(people, new File(outputPath));
    }
}