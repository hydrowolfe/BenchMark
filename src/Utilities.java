import java.util.*;
import java.lang.Math; 
import java.io.*;

public class Utilities
{
    TreeSet<String> Algo_Java_File_Name_List = new TreeSet<String>();

    public String read_write_java_file(String file_name, XML read_xml)
    {
        String java_file_name = file_name + ".java";
        String class_file_name = file_name + ".class";
        Algo_Java_File_Name_List.add(java_file_name);
        Algo_Java_File_Name_List.add(class_file_name);

        try
        {
            FileWriter output = new FileWriter(java_file_name);
            output.write(read_xml.get_algo_code());
            output.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return java_file_name;
    }

    public String find_class_name(File file)
    {
        try
        {
            // identify the first word = "class"
            // capture the next word after
            Scanner in = new Scanner(file);

            while(in.hasNextLine())
            {
                Scanner check = new Scanner(in.nextLine());
                while(check.hasNext())
                {
                    if(check.next().equals("class"))
                        return check.next();   
                }   
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null; 
    }

    public void delete_files()
    {
        // delete .java files created during runtime
        for (String name : this.Algo_Java_File_Name_List)
            new File(name).delete();

    }

}
