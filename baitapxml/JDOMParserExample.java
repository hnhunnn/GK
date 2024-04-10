package baitapxml;

import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class JDOMParserExample {
    public static void main(String[] args) {
        try {
            // Tạo một SAXBuilder để phân tích XML
            SAXBuilder saxBuilder = new SAXBuilder();
            
            // Đọc tài liệu XML từ tệp
            File inputFile = new File("data.xml");
            Document document = saxBuilder.build(inputFile);
            //System.out.println(document);

            // Lấy phần tử gốc (root) của tài liệu XML
            Element rootElement = document.getRootElement();
           // System.out.println(rootElement);
            // Lấy danh sách các phần tử con của phần tử gốc
            List<Element> itemList = rootElement.getChildren("item");

            // Duyệt qua danh sách các phần tử con và in thông tin của chúng
            for (Element item : itemList) {
                System.out.println("Name : " + item.getChildText("name"));
                System.out.println("Price : " + item.getChildText("price"));
            }
         
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
