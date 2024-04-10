package baitapxml;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Student {
    private int id;
    private String name;
    private String address;
    private String dateOfBirth;

    public Student(int id, String name, String address, String dateOfBirth) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}

class AgeCalculator extends Thread {
    private Student student;
    private int age;
    private int sum;

    public AgeCalculator(Student student) {
        this.student = student;
    }

    public void run() {
        String[] parts = student.getDateOfBirth().split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        // Calculate age
        // Assume current date is 2024-04-10
        age = 2024 - year;
        if (month > 4 || (month == 4 && day > 10)) {
            age--;
        }

        // Calculate sum of digits
        sum = 0;
        String dateOfBirthDigits = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);
        for (char c : dateOfBirthDigits.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
    }

    public int getAge() {
        return age;
    }

    public int getSum() {
        return sum;
    }
}

class PrimeChecker extends Thread {
    private AgeCalculator ageCalculator;
    private boolean isPrime;

    public PrimeChecker(AgeCalculator ageCalculator) {
        this.ageCalculator = ageCalculator;
    }

    public void run() {
        int sum = ageCalculator.getSum();
        isPrime = isPrime(sum);
    }

    private boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean getIsPrime() {
        return isPrime;
    }
}

public class Main {
    public static void main(String[] args) {
        // Step 1: Read students from XML file
        List<Student> students = readStudentsFromFile("./src/students.xml");

        // Step 2: Calculate age and sum of digits
        List<AgeCalculator> ageCalculators = new ArrayList<>();
        for (Student student : students) {
            AgeCalculator ageCalculator = new AgeCalculator(student);
            ageCalculator.start();
            ageCalculators.add(ageCalculator);
        }

        // Step 3: Check if sum of digits is prime
        List<PrimeChecker> primeCheckers = new ArrayList<>();
        for (AgeCalculator ageCalculator : ageCalculators) {
            PrimeChecker primeChecker = new PrimeChecker(ageCalculator);
            primeChecker.start();
            primeCheckers.add(primeChecker);
        }

        // Step 4: Write results to XML file
        writeResultsToXml(students, ageCalculators, primeCheckers, "./src/result.xml");
    }

    private static List<Student> readStudentsFromFile(String fileName) {
        List<Student> students = new ArrayList<>();
        try {
            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList classNodeList = doc.getElementsByTagName("Class");

            for (int i = 0; i < classNodeList.getLength(); i++) {
                Element classElement = (Element) classNodeList.item(i);
                NodeList studentNodeList = classElement.getElementsByTagName("Student");

                for (int j = 0; j < studentNodeList.getLength(); j++) {
                    Element studentElement = (Element) studentNodeList.item(j);
                    int id = Integer.parseInt(studentElement.getElementsByTagName("id").item(0).getTextContent());
                    String name = studentElement.getElementsByTagName("name").item(0).getTextContent();
                    String address = studentElement.getElementsByTagName("address").item(0).getTextContent();
                    String dateOfBirth = studentElement.getElementsByTagName("dateOfBirth").item(0).getTextContent();
                    Student student = new Student(id, name, address, dateOfBirth);
                    students.add(student);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return students;
    }


    private static void writeResultsToXml(List<Student> students, List<AgeCalculator> ageCalculators,
                                          List<PrimeChecker> primeCheckers, String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Results");
            doc.appendChild(rootElement);

            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                AgeCalculator ageCalculator = ageCalculators.get(i);
                PrimeChecker primeChecker = primeCheckers.get(i);

                Element studentElement = doc.createElement("Student");
                rootElement.appendChild(studentElement);

                Element idElement = doc.createElement("id");
                idElement.appendChild(doc.createTextNode(String.valueOf(student.getId())));
                studentElement.appendChild(idElement);

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(student.getName()));
                studentElement.appendChild(nameElement);

                Element addressElement = doc.createElement("address");
                addressElement.appendChild(doc.createTextNode(student.getAddress()));
                studentElement.appendChild(addressElement);

                Element ageElement = doc.createElement("age");
                ageElement.appendChild(doc.createTextNode(String.valueOf(ageCalculator.getAge())));
                studentElement.appendChild(ageElement);

                Element sumElement = doc.createElement("sum");
                sumElement.appendChild(doc.createTextNode(String.valueOf(ageCalculator.getSum())));
                studentElement.appendChild(sumElement);

                Element isPrimeElement = doc.createElement("isPrime");
                isPrimeElement.appendChild(doc.createTextNode(String.valueOf(primeChecker.getIsPrime())));
                studentElement.appendChild(isPrimeElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);

            System.out.println("Results saved to " + fileName);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
