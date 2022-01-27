import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** 
 * This is a project management program that is to be used by Poised to
 * keep track of their various building projects. The program takes in 
 * information as specified below.
 * <p>
 * <h3>Project Information</h3>
 * 
 * <li>Project name and number</li>
 * <li>Type of building</li>
 * <li>Project address</li>
 * <li>ERF number</li>
 * <li>Total fee and amount paid to date</li>
 * <li>The deadline</li>
 * 
 * <h3>Customer, Contractor and Architect Information</h3>
 * 
 * <li>Name and Telephone number</li>
 * <li>Email address</li>
 * <li>Physical address</li>
 * 
 * <h3>Functionality includes:</h3>
 * 
 * <li>Capture information about new projects</li>
 * <li>Update information about existing projects</li>
 * <li>Finalize existing projects and generate an invoice</li>
 * <li>See projects that are still in progress</li>
 * <li>See projects that are past their due date</li>
 * <li>See projects that have been finalized</li>
 * 
 * <p>
 * @author Orefile Vincent Mothapa
*/

public class Poised {
	/**
	 * Main Method
	 * @param args - String array arguments
	 */
	public static void main(String[] args) {
		System.out.println("MAIN MENU");
		System.out.println("a - Add New Project");
		System.out.println("b - Add Contact Information");
		System.out.println("c - Get Project Information");
		System.out.println("d - Get Contact Information");
		System.out.println("e - See All Projects");
		System.out.println("f - Finalize Project");
		System.out.println("g - Edit Project Information");
		System.out.println("h - Edit Contact Information");

		//Get user preference
		Scanner object = new Scanner(System.in);
		System.out.print("\nEnter option: ");
		String userInput = object.nextLine();
		
//####################################################################################################
		// If user wants to add a new project
		if (userInput.equalsIgnoreCase("a")) {
			System.out.println("\nYou have chosen to add a new project");
			
			System.out.print("Project Number: ");
			String projectNumber = object.nextLine();
			
			System.out.print("Project Name: ");
			String projectName = object.nextLine();						
			
			System.out.print("Building Type: ");
			String buildingType = object.nextLine();
			
			// If project name is empty, substitute with building type and surname
			if (projectName.equals("")) {
				System.out.print("Enter client surname: ");
				String temp = object.nextLine();
				projectName = buildingType + " " + temp;
			}
			
			System.out.print("Project Address: ");
			String projectAddress = object.nextLine();
			
			System.out.print("ERF Number: ");
			String erfNumber = object.nextLine();
			
			System.out.print("Project Deadline (dd mmm yyyy): ");
			String projectDeadline = object.nextLine();
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d MMM uuuu");
			try {
				LocalDate.parse(projectDeadline, dateFormat);
			} catch (java.time.format.DateTimeParseException e) {
				System.out.println("The date entered is not in the correct format");
				object.close();
				return;
			}
			
			// Ensure that project fee and amount paid below are actual numbers.
			// It will be used in calculations later.
			System.out.print("Project Fee: ");
			float projectFee = 0;
			try{
				projectFee = object.nextFloat();
			} catch (InputMismatchException ex) {
				System.out.println("Number not valid");
				object.close();
				return;
			}
			
			System.out.print("Amount Paid: ");
			float amountPaid = 0;
			try{
				amountPaid = object.nextFloat();
			} catch (InputMismatchException ex) {
				System.out.println("Number not valid.");
				object.close();
				return;
			}
			
			// Create Project variable 
			Project projectDetails = new Project(projectNumber,
									  projectName,
									  buildingType,
									  projectAddress,
									  erfNumber,
									  projectFee,
									  amountPaid,
									  projectDeadline);
			
			// Display the information to be saved
			String details = projectDetails.toString();
			System.out.println("\n" + projectDetails);
			
			// Add to file
			appendFile(details, "Projects.txt");
		}
//####################################################################################################
		// If user wants to add contact information
		else if (userInput.equalsIgnoreCase("b")) {
			System.out.println("\nYou have chosen to add contact information");
			
			// Capture the title of the person (Customer, Architect or Contractor) and project
			// name
			System.out.print("Enter Contact Type: ");
			String contactType = object.nextLine();
			
			System.out.print("Enter Project Number: ");
			String projectNumber = object.nextLine();
			
			System.out.print("Enter Full Name: ");
			String name = object.nextLine();
			
			System.out.print("Enter Contact Number: ");
			String contactNumber = object.nextLine();
			
			System.out.print("Enter Contact Email: ");
			String contactEmail = object.nextLine();
			
			System.out.print("Enter Address: ");
			String address = object.nextLine();
			
			Person personDetails = new Person(
					contactType,
					projectNumber,
					name,
					contactNumber,
					contactEmail,
					address);		
			
			System.out.println("\n" + personDetails);
			String details = personDetails.toString();
			
			// Add to file
			appendFile(details, "Contacts.txt");
		}
//####################################################################################################
		// If user wants to get project information
		else if (userInput.equalsIgnoreCase("c")) {
			System.out.println("\nYou have chosen to get project information");
			System.out.print("Enter the project name or number: ");
			String projectNumber = object.nextLine();
			boolean foundProject = false;
			
			// Look for project in the not yet finalized projects first
			checkForProject(projectNumber, "Projects.txt");
			
			if (foundProject = true) {
				object.close();
				return;
			}
			
			// Look for project in the finalized projects.
			checkForProject(projectNumber, "Completed Projects");
			
	        // If project number is not found
	        if (foundProject == false) {
		    	System.out.println("\nThere is no project with project number " + projectNumber);
	        }
	        object.close();
			return;
		}
//####################################################################################################		
		// If user wants to get contact information
		else if (userInput.equalsIgnoreCase("d")) {
			System.out.println("\nYou have chosen to get contact information");
			System.out.print("Enter their project number: ");
			String projectNumber = object.nextLine();
			boolean foundProjectNumber = false;
			
			try {
		        File myObj = new File("Contacts.txt");
		        Scanner myReader = new Scanner(myObj);
		        int a = 0;
		        
		        while (myReader.hasNextLine()) {
		        	String data = myReader.nextLine();
		        	
		        	// Find the contact corresponding to the project number entered by user
		        	// Check the first line and every 7th line after that for the project number
		        	if (data.length() > 13 && data.substring(17).equals(projectNumber) && 
		        			(a == 0 || a%7 == 0)) {
		        		foundProjectNumber = true;
		        		System.out.println("\n");
		        		for (int i = 0; i < 5; i++) {
		        			System.out.println(myReader.nextLine());
		        			a += 1;
		        		}
		        		
		        	}
		        	a += 1;
		        }        
		        
		        // Close Scanner
		        myReader.close();
			} catch (FileNotFoundException e) {
			      System.out.println("An error occurred. File not found.");
			      object.close();
			      return;
			}
			
	        // If project number is not found
	        if (foundProjectNumber == false) {
		    	System.out.println("\nThere is no project with project number " + projectNumber);
	        }
	        object.close();
			return;
		}
//####################################################################################################		
		// If user wants to see all projects
		else if (userInput.equalsIgnoreCase("e")) {
			System.out.println("\nYou have chosen to see projects\n");
			
			System.out.println("OPTIONS:");
			System.out.println("a - Finalized Projects");
			System.out.println("b - Projects not yet finalized");
			System.out.println("c - Overdue Projects\n");
			System.out.print("Enter selection: ");
			String option = object.nextLine();
			
			// Finalized Projects
			if (option.equalsIgnoreCase("a")) {
				String fileName = "Completed Projects.txt";
				readEntireFile(fileName);
			}
			
			// Projects not yet finalized
			if (option.equalsIgnoreCase("b")) {
				String fileName = "Projects.txt";
				readEntireFile(fileName);
			}
			
			// Projects Overdue.
			// This will require the date today
			if (option.equalsIgnoreCase("c")) {
				System.out.println("Overdue Projects\n");
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d MMM uuuu");
				LocalDate now = LocalDate.now();
				
				try {
					FileReader myFile = new FileReader("Projects.txt");
					Scanner reader = new Scanner(myFile);
					
					boolean foundOverdueProjects = false;
					String array[] = new String[9];
					
					while (reader.hasNext()) {
						for (int i = 0; i < 8; i++) {
							array[i] = reader.nextLine();
						}
						array[8] = " ";
						String date = array[7].substring(16);
						
						// Check if date has passed
						if (LocalDate.parse(date, dateFormat).isBefore(now)){
							foundOverdueProjects = true;
							System.out.println("Proj" + array[0].replaceAll("Proj", ""));
							for (int j = 1; j < 9; j++) {
								System.out.println(array[j]);
							}
							
							// Clear out array to store new values and do the checks again
							array = new String[9];
						}
						if (reader.hasNext()) {
							reader.nextLine();
						}
					}
					reader.close();
					
					if (foundOverdueProjects == false) {
						System.out.println("There are no overdue projects.");
					}
					
				} catch (FileNotFoundException e) {
					System.out.println("An error occured. File not found.");
				}
				object.close();
				return;
			}
			
			else {
				System.out.println("Invalid project type selection");
				object.close();
				return;
			}
		}
//####################################################################################################
		// If user wants to finalize project
		else if (userInput.equalsIgnoreCase("f")) {
			finalizeProject(object);
		}
//####################################################################################################
		// If user wants to edit project
		else if (userInput.equalsIgnoreCase("g")) {
			editProject(object);
		}
//####################################################################################################
		// If user wants to edit a contact
		else if (userInput.equalsIgnoreCase("h")) {
			editContact(object);
		}
//####################################################################################################
		// If the user input is not one of the defined options
		else {
			System.out.println("\nInvalid selection");
			object.close();
			return;
		}
		
		// Close scanner for user options
		object.close();
	}

	
	
	
//####################################################################################################
// METHODS
	/**
	 * This adds details to an already existing text file. If the file does not exist
	 * it is first created and then written to.
	 * 
	 * @param fileContents Information to be written to the file.
	 * @param fileName Name of the file to be written to or created.
	 */
	
	// Method to write to file.
	private static void appendFile(String fileContents, String fileName) {
		
		// Create a file to save project information
		try {
			File myObj = new File(fileName);
			if (myObj.createNewFile()) {
				try {
					FileWriter myFile = new FileWriter(fileName);
				    myFile.write(fileContents + "\n");
				    myFile.close();
				    System.out.println("\nInformation Successfully captured.");
				  } catch (IOException e) {
					  System.out.println("An error occured writing to file.");
				      return;
				  }
			}
				
			else if (myObj.length() == 0) {
					overwriteFile(fileContents, fileName);
					FileWriter myFile = new FileWriter(fileName);
				    myFile.write(fileContents + "\n");
				    myFile.close();
				
			} else {
				// If the file already exists then append to it
				try {
					FileWriter myFile = new FileWriter(fileName, true);
				    myFile.write("\n" + fileContents + "\n");
				    myFile.close();
				    System.out.println("\nInformation Successfully captured.");
				  } catch (IOException e) {
					  System.out.println("An error occurred writing to file.");
				      return;
				  }
			}
		} catch (IOException e) {
			System.out.println("An error occured writing to file.");
			return;
		}
	}
	
//####################################################################################################
	/**
	 * This overwrites the information in the already existing file.
	 * 
	 * @param fileContents Information to be written to the file.
	 * @param fileName Name of the file to be written to.
	 */
	// Method to over-right file contents
	private static void overwriteFile(String fileContents, String fileName) {
		// Overwrite Project file contents with the updated information including
		// the project that has been finalized.
		try {
			FileWriter myFile = new FileWriter(fileName);
		    myFile.write(fileContents);
		    myFile.close();
		    System.out.println("\nDone!");
		  } catch (IOException e) {
			  System.out.println("An error occurred writing to file.");
		      return;
		  }
	}

//####################################################################################################
	/**
	 * This edits contact information. You can either edit the email address or 
	 * the telephone number.
	 * 
	 * @param object User input when selecting method.
	 */
	// Method to edit contact information
	private static void editContact(Scanner object) {
		System.out.println("\nYou have chosen to edit a contact");
		System.out.print("Enter their project number: ");
		String projectNumber = object.nextLine();
		System.out.print("Enter the contact type (Architect, Contractor or Customer): ");
		String contactType = object.nextLine();
		
		System.out.println("OPTIONS:");
		System.out.println("a - Contact Number");
		System.out.println("b - Email Address\n");
		System.out.print("Enter selection: ");
		String option = object.nextLine();
		
		if (option.equalsIgnoreCase("a")) {
			try {
			    File myObj = new File("Contacts.txt");
			    Scanner myReader = new Scanner(myObj);
			    String array[] = new String[7];
			    String fileContents = "";
			    boolean foundContact = false;
			    
			    while (myReader.hasNextLine()) {
			    	for (int i = 0; i < 6; i++) {
						array[i] = myReader.nextLine();
					}
					array[6] = "\n";
			    	
			    	// Edit the project corresponding to the project number and contact type entered by user
			    	if (array[0].substring(17).equals(projectNumber) && 
			    			(array[1].substring(15).equalsIgnoreCase(contactType))) {
			    		foundContact = true;
			    		System.out.print("\nEnter new contact number: ");
		        		int newNumber = 0;
		    			try{
		    				newNumber = object.nextInt();
		    			} catch (InputMismatchException ex) {
		    				System.out.println("Not a valid number.");
		    				myReader.close();
		    				return;
		    			}
		    			
		    			// Replace old deadline with the new deadline
		    			array[3] = "Contact Number\t: " + newNumber;
			    	}
			    	
			    	for (int i = 0; i < 6; i++) {
		    			fileContents += array[i] + "\n";
		    		}
			    	
			    	if (myReader.hasNext()) {
			    		fileContents += array[6];
			    		myReader.nextLine();
			    	}
			    	
			    	// Clear out array to store new values and do the checks again
					array = new String[7];
			    }
			    
			    if (foundContact == false) {
					System.out.println(contactType + " with project number " + projectNumber + " not found.");
					myReader.close();
					return;
				}
			    myReader.close();
			    overwriteFile(fileContents, "Contacts.txt");
		
			} catch (FileNotFoundException e) {
			      System.out.println("An error occurred. File not found\n");
			      return;
			}
		}
		
//#########################################
		else if (option.equalsIgnoreCase("b")) {
			try {
			    File myObj = new File("Contacts.txt");
			    Scanner myReader = new Scanner(myObj);
			    String array[] = new String[7];
			    String fileContents = "";
			    boolean foundContact = false;
			    
			    while (myReader.hasNextLine()) {
			    	for (int i = 0; i < 6; i++) {
						array[i] = myReader.nextLine();
					}
					array[6] = "\n";
			    	
			    	// Edit the project corresponding to the project number and contact type entered by user
			    	if (array[0].substring(17).equals(projectNumber) && 
			    			(array[1].substring(15).equalsIgnoreCase(contactType))) {
			    		foundContact = true;
			    		System.out.print("\nEnter new email address: ");
			    		String newEmail = object.nextLine();
		    			
		    			// Replace old email address with the new
		    			array[4] = "Contact Email\t: " + newEmail;
			    	}
			    	
			    	for (int i = 0; i < 6; i++) {
		    			fileContents += array[i] + "\n";
		    		}
			    	
			    	if (myReader.hasNext()) {
			    		fileContents += array[6];
			    		myReader.nextLine();
			    	}
			    	
			    	// Clear out array to store new values and do the checks again
					array = new String[7];
			    }
			    
			    if (foundContact == false) {
					System.out.println(contactType + " with project number " + projectNumber + " not found.");
					myReader.close();
					return;
				}
			    myReader.close();
			    overwriteFile(fileContents, "Contacts.txt");
		
			} catch (FileNotFoundException e) {
			      System.out.println("An error occurred. File not found\n");
			      return;
			}
		}

//#########################################
		else {
			System.out.print("Invalid selection");
			return;
		}
	}
	
//####################################################################################################
	/**
	 * This edits either the due date or the amount paid of a project that has
	 * not yet been finalized.
	 * 
	 * @param object User input when selecting method.
	 */
	// Method to edit project information.
	private static void editProject(Scanner object) {
		System.out.println("\nYou have chosen to edit a project");
		System.out.print("Enter the project name or number: ");
		String projectNumber = object.nextLine();
		
		// Display options for editing
		System.out.println("OPTIONS:");
		System.out.println("a - Due Date");
		System.out.println("b - Amount Paid\n");
		System.out.print("Enter selection: ");
		String option = object.nextLine();
		
		// EDIT DUE DATE
		if (option.equalsIgnoreCase("a")) {				
			String fileContents = "";
			
			boolean foundProject = false;
			try {
			    File myObj = new File("Projects.txt");
			    Scanner myReader = new Scanner(myObj);
			    String array[] = new String[9];
			    
			    while (myReader.hasNextLine()) {
			    	for (int i = 0; i < 8; i++) {
						array[i] = myReader.nextLine();
					}
					array[8] = "\n";
			    	
			    	// Edit the project corresponding to the project name or number entered by user
			    	if (array[0].substring(14).equals(projectNumber) || 
			    			(array[1].substring(12).equalsIgnoreCase(projectNumber))) {
			    		foundProject = true;
			    		System.out.print("\nEnter new project deadline (dd mmm yyyy): ");
		        		String dueDate = object.nextLine();
		    			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d MMM uuuu");
		    			try {
		    				LocalDate.parse(dueDate, dateFormat);
		    			} catch (java.time.format.DateTimeParseException e) {
		    				System.out.println("The date entered is not in the correct format");
		    				myReader.close();
		    				return;
		    			}
		    			
		    			// Replace old deadline with the new deadline
		    			array[7] = "Proj Deadline\t: " + dueDate;
			    	}
			    	
			    	for (int i = 0; i < 8; i++) {
		    			fileContents += array[i] + "\n";
		    		}
			    	
			    	if (myReader.hasNext()) {
			    		fileContents += array[8];
			    		myReader.nextLine();
			    	}
			    	
			    	// Clear out array to store new values and do the checks again
					array = new String[9];
					
			    }
			    if (foundProject == false) {
					System.out.println("\nProject not found.");
					myReader.close();
					return;
				}
			    myReader.close();
			    overwriteFile(fileContents, "Projects.txt");
		
			} catch (FileNotFoundException e) {
			      System.out.println("An error occurred. File not found\n");
			      return;
			}
		}	
//###############################
		// EDIT AMOUNT PAID
		if (option.equalsIgnoreCase("b")) {				
			String fileContents = "";
			boolean foundProject = false;
			try {
			    File myObj = new File("Projects.txt");
			    Scanner myReader = new Scanner(myObj);
			    String array[] = new String[9];
			    
			    while (myReader.hasNextLine()) {
			    	for (int i = 0; i < 8; i++) {
						array[i] = myReader.nextLine();
					}
					array[8] = "\n";
			    	
			    	// Edit the project corresponding to the project name or number entered by user
			    	if (array[0].substring(14).equals(projectNumber) || 
			    			(array[1].substring(12).equalsIgnoreCase(projectNumber))) {
			    		foundProject = true;
			    		System.out.print("\nEnter new amount: ");
			    		float projectFee = 0;
						try{
							projectFee = object.nextFloat();
						} catch (InputMismatchException ex) {
							System.out.println("Number not valid.");
							myReader.close();
							return;
						}
		    			
		    			// Replace old deadline with the new deadline
		    			array[6] = "Amount Paid\t: R" + projectFee;
			    	}
			    	
			    	for (int i = 0; i < 8; i++) {
		    			fileContents += array[i] + "\n";
		    		}
			    	
			    	if (myReader.hasNext()) {
			    		fileContents += array[8];
			    		myReader.nextLine();
			    	}
			    	
			    	// Clear out array to store new values and do the checks again
					array = new String[9];
			    }
			    
			    if (foundProject == false) {
					System.out.println("\nProject not found");
					myReader.close();
					return;
				}
			    myReader.close();
			    overwriteFile(fileContents, "Projects.txt");
		
			} catch (FileNotFoundException e) {
			      System.out.println("An error occurred. File not found\n");
			      return;
			}
		}
			
//##########################################
		else {
			System.out.println("Invalid selection");
			return;
		}
	}
//####################################################################################################
	/**
	 * This finalizes a project and returns an invoice if the customer still owes.
	 * 
	 * @param object User input when selecting method.
	 */
	// Method to finalize project
	private static void finalizeProject(Scanner object) {
		System.out.println("\nYou have chosen to finalize a project");
		System.out.print("Enter the project number: ");
		String projectNumber = object.nextLine();
		String fileContents = "";
		String projectData = "";
		double moneyOwed = 0;
		boolean foundProject = false;
		
		// Read from file
		try {
		    File myObj = new File("Projects.txt");
		    Scanner myReader = new Scanner(myObj);
		    String array[] = new String[10];
		    
		    while (myReader.hasNextLine()) {
		    	for (int i = 0; i < 8; i++) {
					array[i] = myReader.nextLine();
				}
				array[8] = " ";
				
				if (array[0].substring(14).equals(projectNumber)) {
					foundProject = true;
					
					// Calculate money owed
					double projectFee = Double.valueOf(array[5].substring(12));
					double amountPaid = Double.valueOf(array[6].substring(15));
					moneyOwed = projectFee - amountPaid;
					
					projectData = array[0];
					for (int i = 1; i < 8; i++) {
						projectData += "\n" + array[i];
					}
					
					if (myReader.hasNext()) {
						myReader.nextLine();
					} else {
						try {
							StringBuilder temp = new StringBuilder(fileContents);
							temp.delete(fileContents.length() - 1, fileContents.length());
							
							fileContents = temp.toString();
						} catch (StringIndexOutOfBoundsException e) {
							continue;
						}
						
					}
				} else {
					// Info to be written back into the file
					for (int i = 0; i < 8; i++) {
						fileContents += array[i] + "\n";
					}

					if (myReader.hasNext()) {
						fileContents += "\n";
						myReader.nextLine();
					}
				}
				// Clear out array to store new values and do the checks again
				array = new String[10];
		    }
		    myReader.close();
			
		} catch (FileNotFoundException e) {
			  
		}
		
		LocalDate now = LocalDate.now();
		String formattedDate = now.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
		
		if (foundProject == true) {
			projectData += "\nDate Finalized\t: " + formattedDate;
		} else {
			System.out.println("\nProject not found with project number " + projectNumber);
			return;
		}
		
		
		// GENERATE INVOICE	
		
		if (moneyOwed > 0) {
			Scanner line = new Scanner(System.in);
			//getContactInfo(projectNumber);
			//System.out.println("\n");
			System.out.println("\nCUSTOMER INVOICE\n");
			if (getContactInfo(projectNumber)) {
				System.out.println("\n" + projectData);
				System.out.println("\nMoney owed\t: R" + moneyOwed);
				
			} else {
				System.out.println("Customer with project number " + projectNumber + " not found. ");
				System.out.println("Enter the customer information manually.");
				System.out.print("\nName: ");
				String name = line.nextLine();
				
				System.out.print("Contact number or email: ");
				String contact = line.nextLine();
				
				System.out.println("\nName: " + name);
				System.out.println("Contact Details: " + contact);
				System.out.println("Money owed: R" + moneyOwed);
			}
			
			line.close();
		}
		
		appendFile(projectData, "Completed Projects.txt");
		overwriteFile(fileContents, "Projects.txt");
	}	
	
//####################################################################################################
	/**
	 * This prints the contents of a file.
	 * 
	 * @param fileName Name of the file to be written to or created.
	 */
	// Method to read an entire .txt file and print it
	private static void readEntireFile(String fileName) {
		try {
			FileReader myFile = new FileReader(fileName);
			Scanner reader = new Scanner(myFile);
			while (reader.hasNext()) {
				System.out.println(reader.nextLine());
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("An error occured. File not found.");
			return;
		}
	}

//####################################################################################################
	/**
	 * This prints the project information for a specific project.
	 * 
	 * @param projectNumber The unique project number.
	 * @param fileName Name of the file to be checked for project information.
	 * @return Returns True if the project if found.
	 */
	// This method is to find information for a specific project
	// I put it as a method because it can check 2 files for the project info
	private static boolean checkForProject(String projectNumber, String fileName) {
		boolean foundProject = false;
		try {
		    File myObj = new File(fileName);
		    Scanner myReader = new Scanner(myObj);
		    String array[] = new String[9];
		    
		    while (myReader.hasNextLine()) {
		    	for (int i = 0; i < 8; i++) {
					array[i] = myReader.nextLine();
				}
				array[8] = " ";
		    	
		    	// Edit the project corresponding to the project number entered by user
		    	if (array[0].substring(14).equals(projectNumber) || 
		    			(array[1].substring(12).equalsIgnoreCase(projectNumber))) {
		    		foundProject = true;
		    		System.out.println("\n");
		    		for (int i = 0; i < 9; i++) {
		    			System.out.println(array[i]);
		    		}
		    	}
		    	// Clear out array to store new values and do the checks again
				array = new String[9];
				if (myReader.hasNext()) {
					myReader.nextLine();
				}
		    }
		    myReader.close();
	
		} catch (FileNotFoundException e) {
		      System.out.println("An error occurred. File not found");
		}
		
		return foundProject;
	}
	
//####################################################################################################	
	/**
	 * This prints the contact information if the contact was initially saved.
	 * 
	 * @param projectNumber The unique project number.
	 * @return foundContact
	 */
	private static boolean getContactInfo(String projectNumber) {
		boolean foundContact = false;
		
		try {
		    File myObj = new File("Contacts.txt");
		    Scanner myReader = new Scanner(myObj);
		    String array[] = new String[7];
		    
		    while (myReader.hasNextLine()) {
		    	for (int i = 0; i < 6; i++) {
					array[i] = myReader.nextLine();
				}
				array[6] = "\n";
		    	
		    	// Edit the project corresponding to the project number and contact type entered by user
		    	if (array[0].substring(10).equals(projectNumber) && 
		    			(array[1].substring(15).equalsIgnoreCase("customer"))) {
		    		foundContact = true;
		    		System.out.println("Project" + array[0]);
		    		for (int i = 1; i < 6; i++) {
		    			System.out.println(array[i]);
		    		}
		    	}
		    	
		    	if (myReader.hasNext()) {
		    		myReader.next();
		    	}
		    }
		    
		    myReader.close();
		} catch (FileNotFoundException e) {
		      return foundContact;
		}
		return foundContact;
	}
}







