
import java.io.*;
import java.text.DecimalFormat;


public class Aggregate{

	public static String[] group_columns;
	public static String agg_column = "";
	public static String agg_function = "";
	private static DecimalFormat df2 = new DecimalFormat(".##"); //to print upto 2dp
	
	
	public static String cols_to_be_aggregate(String[][] table, int cols){ // returns the number of columns that has to be aggregated (e.g id: 0)
		String colAggregate = "";
		for(int i=0; i<group_columns.length; i++){
			for(int j=0; j<cols; j++){
				if(group_columns[i].equals(table[0][j])){
					colAggregate = colAggregate + j + ",";
				}
			}
		}
		return colAggregate;
	}
	
	public static void print(String str){ //Prints the string
		System.out.println(str);
	}//end of print
	
	public static void spooky(String[][] table, int cols, int agg_column_num){
		String colAggregateSTR = cols_to_be_aggregate(table, cols);
		String[] colAggregate = colAggregateSTR.split(",");
		boolean[] check = new boolean[table.length]; //boolean array to get rid of duplicates as discussed in the lecture
		for(int i=0; i<check.length; i++){
			check[i] = false;
		}
		
		String curr = "";
		for(int j=0; j<table.length; j++){
			for(int i=0; i<colAggregate.length; i++){
				int column = Integer.parseInt(colAggregate[i]);
				curr = curr + table[j][column] + ",";
			}
			curr = curr.trim();
			curr = curr + "   ";
		}
		
		String[] array = curr.split("   ");
		String current = "";
		String sumSTR = "";
		String count = "";
		String avgSTR = "";
		for(int i=1; i<array.length; i++){
			int counter = 0;
			double sum = 0;
			boolean flag = false;
			double avg = 0;
			if(check[i]==false){
				current = array[i];
				for(int j=i; j<array.length; j++){
					if(current.equals(array[j]) && check[j]==false){
						counter++;
						sum = sum + Double.parseDouble(table[j][agg_column_num]);
						check[j] = true;
					}
				}
				count = count + current + counter + "\n";
				sumSTR = sumSTR + current + df2.format(sum) + "\n";
				avg = sum/counter;
				avgSTR = avgSTR + current + df2.format(avg) + "\n";
			}
		}
		
		String headings = "";
		for(int i=0; i<colAggregate.length; i++){
			headings = headings + table[0][Integer.parseInt(colAggregate[i])] + ",";
		}
		//Formating the output so that it matches the CSV format
		count = headings + "count(" + agg_column + ")" + " \n" + count;
		sumSTR = headings + "sum(" + agg_column + ")" + "\n" + sumSTR;
		avgSTR = headings + "avg(" + agg_column + ")" + "\n" + avgSTR;
			
		if(agg_function.equals("count")){
			print(count);
		}
		else if(agg_function.equals("sum")){
			print(sumSTR);
		}
		else if(agg_function.equals("avg")){
			print(avgSTR);
		}
	}//end of spooky
	
	
	public static void showUsage(){
		System.err.printf("Usage: java Aggregate <function> <aggregation column> <csv file> <group column 1> <group column 2> ...\n");
		System.err.printf("Where <function> is one of \"count\", \"sum\", \"avg\"\n");	
	}//end of showUsage
	

	public static void main(String[] args){
		
		//At least four arguments are needed
		if (args.length < 4){
			showUsage();
			return;
		}
		agg_function = args[0];
		agg_column = args[1];
		String csv_filename = args[2];
		group_columns = new String[args.length - 3];
		for(int i = 3; i < args.length; i++)
			group_columns[i-3] = args[i];
		
		if (!agg_function.equals("count") && !agg_function.equals("sum") && !agg_function.equals("avg")){
			showUsage();
			return;
		}
		
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(csv_filename));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",csv_filename);
			return;
		}
		
		String header_line;
		String str = ""; //Contains the whole file as a string with "@@@" at the end of each line
		try{
			while((header_line = br.readLine()) != null){; //The readLine method returns either the next line of the file or null (if the end of the file has been reached)
				str = str + header_line + "@@@";
			}
		} catch (IOException e){
			System.err.printf("Error reading file\n", csv_filename);
			return;
		}

		//Split str string into an array of string values using a comma
		//as the separator.
		String[] columns = str.split("@@@"); //Every line of the file is an element of the array
		String[] column_names = columns[0].split(",");  
		String[] ele = str.split("@@@|,"); //Every single data of the file is an element of the array
		


		int agg_column_num = 0;
		int k = 0;
		String[][] table = new String[columns.length][column_names.length]; //2d array to make the exact table as in the file
		for(int i=0; i<columns.length; i++){
			for(int j=0; j<column_names.length; j++){
				if(agg_column.equals(column_names[j])){
					agg_column_num = j;
				}
				table[i][j] = ele[k];
				k++;
			}
		}
		spooky(table, column_names.length, agg_column_num); //function that performs all the spooky stuff (avg, count and sum)
	}//end of main
}// end of class

