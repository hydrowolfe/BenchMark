import java.util.*;
import java.lang.Math; 
import java.io.*;

public class Control
{
    Process sub_process;
    String java_file_name;
	 // create instance of Utilities class
    Utilities utility = new Utilities();

	// receives input from frontend
    InputStreamReader frontend_input = new InputStreamReader(System.in);
    BufferedReader frontend_reader = new BufferedReader(frontend_input);
    public void run() throws Exception
    {
        outer : while(true)
        {
            try
            {	
				System.out.println("ready");
				
				ArrayList<String> data_files = new ArrayList<String>();
                ArrayList<String> class_name = new ArrayList<String>();
				ArrayList<ArrayList<String> > ops_mode_selected = new ArrayList<ArrayList<String> >();
				ArrayList<String> xmldir = new ArrayList<String>();
				
				String num_of_xmls_str = frontend_reader.readLine();
				if (num_of_xmls_str.equalsIgnoreCase("reset"))   continue;
				else if (num_of_xmls_str.equalsIgnoreCase("exit")) 
				{
					utility.delete_files();
					System.exit(0);
				}
				int num_of_xmls = Integer.parseInt(num_of_xmls_str);
					
				for(int j = 0; j < num_of_xmls; j++)
				{
					// receives XML file path
					String xml_directory = frontend_reader.readLine();
						
					if (xml_directory.equalsIgnoreCase("reset"))    continue outer;
					else if (xml_directory.equalsIgnoreCase("exit"))
					{
						utility.delete_files();
						System.exit(0);
					}

						// read xml file
					File xml_file_obj = new File(xml_directory);
					XML read_xml = new XML(xml_file_obj);

						// read file to detect class name
					String className = utility.find_class_name(xml_file_obj);

					if (className == null)
					{
						String name[] = xml_directory.split("[/\\\\]+");
						throw new ClassNameNotFoundException(name[name.length-1] + ". Invalid or no class name found in code from uploaded XML file!");
					}
						
					class_name.add(className);
					// write .java file
					java_file_name = utility.read_write_java_file(className, read_xml);

						// build command line to compile .java
					String cmd_array[] = new String[]{"javac", java_file_name};
					Process compile = Runtime.getRuntime().exec(cmd_array);
						
						// stream error from compilation
					InputStream error = compile.getErrorStream();
					Scanner error_reader = new Scanner(error);
					if (error_reader.hasNextLine())
						throw new InvalidAlgorithmException(error_reader.nextLine());
					
					ArrayList<String> check = read_xml.get_algo_operation_mode();
					
					if((check.contains("key_gen_time") && check.contains("decrypt_time") && check.size() == 2) || (check.contains("key_gen_ops_count") && check.contains("decrypt_ops_count") && check.size() == 2) || (check.contains("key_gen_time") && check.contains("decrypt_time") && check.contains("key_gen_ops_count") && check.contains("decrypt_ops_count") && check.size() == 4) || (check.contains("decrypt_time") && check.contains("key_gen_ops_count") && check.contains("decrypt_ops_count") && check.size() == 3) || (check.contains("key_gen_time") && check.contains("key_gen_ops_count") && check.contains("decrypt_ops_count") && check.size() == 3) || (check.contains("key_gen_time") && check.contains("decrypt_time") && check.contains("decrypt_ops_count") && check.size() == 3) || (check.contains("key_gen_time") && check.contains("decrypt_time") && check.contains("key_gen_ops_count") && check.size() == 3))
						throw new InvalidAlgorithmException("keygen and decrypt is not a valid combination");
					
					ops_mode_selected.add(check);
					
					xmldir.add(xml_directory);
					
					System.out.println();
				}

                // receive total number of data files
                String num_of_data_file_str = frontend_reader.readLine();
                        
                if (num_of_data_file_str.equalsIgnoreCase("reset"))   continue;
                else if (num_of_data_file_str.equalsIgnoreCase("exit")) 
                {
                    utility.delete_files();
                    System.exit(0);
                }
                        
                // loop according total number of data files to receive data file paths
                int num_of_data_file = Integer.parseInt(num_of_data_file_str);
                for (int i = 0; i < num_of_data_file; i++)
                {
                    // receieve individual data file name
                    String data_directory = frontend_reader.readLine();

                    if (data_directory.equalsIgnoreCase("reset"))   continue outer;
                    if (data_directory.equalsIgnoreCase("exit")) 
                    {
                        utility.delete_files();
                        System.exit(0);
                    }    

                    long file_size = new File(data_directory).length();
                   /* if (file_size == 0)
					{
						String name[] = data_directory.split("[/\\\\]+");
                        throw new InvalidFileSizeException(name[name.length-1] + ". Invalid data file size! Please check file content again!");
					}*/

                    // print file size to frontend console
                    System.out.println(file_size);

                    data_files.add(data_directory);
                }

                for(int k = 0; k < num_of_xmls; k++)
                {   
					final int num = k;
					
					System.out.println(class_name.get(k));
					System.out.println(ops_mode_selected.get(k).size());
                    // execute .class file with data file as arg 
                    for (int i = 0; i < num_of_data_file; i++)
                    {

                        // create command line with data file as argument
                        String cmd_array_run[] = new String[]{"java", class_name.get(k), data_files.get(i)};

                        // execute .class file
                        Thread sub_proc_thread = new Thread(new Runnable() 
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    // execute .class 
                                    sub_process = Runtime.getRuntime().exec(cmd_array_run);

                                    // stream any runtime error
                                    InputStream runtime_error = sub_process.getErrorStream();
                                    Scanner runtime_error_reader = new Scanner(runtime_error);
                                    if (runtime_error_reader.hasNextLine())
                                    {
                                        System.err.println(runtime_error_reader.nextLine());
                                       /* utility.delete_files();
                                        System.exit(0);*/
                                    }
                                    
                                        // stream input from subprocess
                                        InputStream input = sub_process.getInputStream();
                                        Scanner input_reader = new Scanner(input);

                                        // expecting outputs according to ops_mode_selected size
                                        for (int i = 0; i < ops_mode_selected.get(num).size() && !Thread.currentThread().isInterrupted(); i++)
                                        {
                                            if (input_reader.hasNextLine())
                                            {
                                                System.out.println(ops_mode_selected.get(num).get(i));
                                                System.out.println(input_reader.nextLine());
                                            }  
											else
											{
												String name[] = xmldir.get(num).split("[/\\\\]+");
												throw new InvalidAlgorithmException(name[name.length-1] + ". Incorrect number of output, was " + i + ", expecting " + ops_mode_selected.size() + ".");
											}
                                        }   

                                        // if # outputs from source code > # selected operations mode
                                        if (input_reader.hasNextLine())
                                        {
                                            String name[] = xmldir.get(num).split("[/\\\\]+");
                                            int num_output = ops_mode_selected.size() + 1;
											throw new InvalidAlgorithmException(name[name.length-1] + ". Incorrect number of output, was " + num_output + ", expecting " + ops_mode_selected.size() + ".");
                                        }
                                }
                                catch(InvalidAlgorithmException ex_1)
                                {
                                    System.err.println(ex_1.toString());
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }	
                            }
                        });

                        sub_proc_thread.start();
                        String command = "";

                        while (sub_proc_thread.isAlive())
                        {
                            if (frontend_reader.ready())
							{
                                command = frontend_reader.readLine();
								break;
							}
                        }
						if (command.equalsIgnoreCase("exit"))
                        {
                            sub_process.destroy();
                            utility.delete_files();
                            System.exit(0);
                        }
                        else if (command.equalsIgnoreCase("reset"))
                        { 
							sub_proc_thread.interrupt();
							sub_process.destroy();
							sub_proc_thread.join();
                            continue outer;
                        }
                    }
                }
				frontend_reader.readLine();
            }
            catch(NullPointerException e)
            {
                System.err.println(e.toString());
				frontend_reader.readLine();
            }
            catch(ClassNameNotFoundException ex_2)
            {
                System.err.println(ex_2.toString());
				frontend_reader.readLine();
            }
            catch(Exception e)
            {
                e.printStackTrace();
				frontend_reader.readLine();
            }
            
        }
    }

    public static void main (String [] args) throws Exception
    {
        new Control().run();
    }
}

class InvalidAlgorithmException extends Exception
{
    public InvalidAlgorithmException(String error)
    {
        super(error);
    }
}

class ClassNameNotFoundException extends Exception
{
    public ClassNameNotFoundException(String error)
    {
        super(error);
    }
}

class InvalidFileSizeException extends Exception
{
    public InvalidFileSizeException(String error)
    {
        super(error);
    }
}