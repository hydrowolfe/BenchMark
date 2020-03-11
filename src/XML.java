import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*; 
import java.time.*;
import java.nio.file.*;
import java.nio.file.Files;
import javafx.util.*;

public class XML
{
    NodeList xml_list; 

    String ops_mode[] = new String[]
    {"key_gen_ops_count", "encrypt_ops_count", "decrypt_ops_count", "key_gen_time", "encrypt_time", "decrypt_time"};


    public XML(File xml_file)
    {  
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder  = dbFactory.newDocumentBuilder();
			dBuilder.setErrorHandler(new ErrorHandler()
			{
				public void warning(SAXParseException e)
				{
					System.err.println(e.getMessage());
				}
				
				public void error(SAXParseException e)
				{
					System.err.println(e.getMessage());
				}
				
				public void fatalError(SAXParseException e)
				{
					System.err.println(e.getMessage());
				}
			});
            Document doc = dBuilder.parse(xml_file);
            doc.getDocumentElement().normalize();
            
            this.xml_list = doc.getElementsByTagName("public_key_algorithm");

            if (xml_list.getLength() == 0)
                throw new ElementNotFoundException("Element \"public_key_algorithm\" not found! Please check XML file again");
        }
        catch(SAXParseException e_1)
        {
            System.err.println(e_1.getMessage());

        }
        catch(IOException e_2)
        {
            System.err.println(e_2.getMessage());
        }
        catch(ElementNotFoundException e_3)
        {
            System.err.println(e_3.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        
    }

    public String get_algo_code()
    { 
        String algorithm = "";

        try
            {
                Node xml_node = this.xml_list.item(0);
                Element e = (Element) xml_node;

                algorithm = e.getElementsByTagName("code").item(0).getTextContent();
            }
            catch(NullPointerException ex)
            {
                System.err.println("Element \"code\" not found! Please check XML file again");
            }

        return algorithm;
    }

    public ArrayList<String> get_algo_operation_mode()
    {
        ArrayList <String> ops_mode_selected = new ArrayList<>();
        String mode = "";
       
        try
        {
            Node xml_node = this.xml_list.item(0);
            Element e = (Element)xml_node;

            for (String s : ops_mode)
            {
                mode = s;
                String mode_implement = e.getElementsByTagName(s).item(0).getTextContent();
                if (mode_implement.equalsIgnoreCase("true"))
                     ops_mode_selected.add(s);
            }

        }
        catch(NullPointerException ex)
        {
            System.err.println("Element <" + mode + "> not found! Please check XML file again");
        }

        return ops_mode_selected;

    }

}

class ElementNotFoundException extends Exception
{
    public ElementNotFoundException(String error)
    {
        super(error);
    }
}
