package net.potatoing.csvManager;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.opencsv.CSVReader;

public class Csv extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	JTable table;
	
	String fileName;
	char seperator = ';';
	
	String[][] original;
	
	public Csv(String fileName) {
		this(fileName, ';');
	}
	
	public Csv(String fileName, char seperator) {
		this.fileName = fileName;
		this.seperator = seperator;
		
		setLayout(new FlowLayout());
		
		String[] columNames = {"B", "C"};

		String[][] data = readTo2DArray();
		original = data;
		
		table = new JTable(data, columNames);
		table.setPreferredScrollableViewportSize(new Dimension(600, 400));
		table.setFillsViewportHeight(true);
		table.addKeyListener(new KeyAdapter() {
	        public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode() == KeyEvent.VK_ENTER) {

	                int row = table.getSelectedRow();
	                int column = table.getSelectedColumn();

	                // resul is the new value to insert in the DB
	                String result = table.getValueAt(row, column).toString();

	                String[] lines = readFileIntoArray();
	                String[] s = lines[row].split(";", 0);
	                String newLine = null;
	                if(column == 0)
	                	newLine = s[0] + ";" + result + ";" + s[s.length-1];
	                if(column == 1)
	                	newLine = s[0] + ";" + s[1] + ";" + result;
	                lines[row] = newLine;
	                writeArrayToFile(lines);
	            }
	        }
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}
	
	public String[][] readTo2DArray() {
		try {
			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
			lnr.skip(Long.MAX_VALUE);
			int linesNum = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
			// Finally, the LineNumberReader object should be closed to prevent resource leak
			lnr.close();
			
			String[][] lines = new String[linesNum][2];
			int i = 0;
			CSVReader reader = new CSVReader(new FileReader(fileName), seperator);
			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				//System.out.println(nextLine[1] + "|" + nextLine[2]);
				lines[i][0] = nextLine[1];
				lines[i][1] = nextLine[2];
				i++;
			}
			reader.close();
			return lines;
		} catch (Exception e) {
			System.out.println("fuck");
			return null;
		}
	}
	
	public String[] readFileIntoArray() {
		try {
		Path filePath = new File(fileName).toPath();
		Charset charset = Charset.defaultCharset();        
		List<String> stringList = Files.readAllLines(filePath, charset);
		return stringList.toArray(new String[]{});
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("fuck");
			return null;
		}
	}
	
	public void writeArrayToFile(String[] array) {
		FileWriter f = null;
		try {
			f = new FileWriter(fileName);
			for(int i = 0; i < array.length; i++) {
				f.write(array[i] + System.getProperty("line.separator"));
			}
			f.close();
		} catch(IOException e ) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//new Csv("C:\\Users\\Nelala\\Desktop\\apname.csv").readToTable();
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Please select \".csv\" file..");
		String selected;
		if(fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION)
			selected = fc.getSelectedFile().getAbsolutePath();
		else
			selected = "C:\\Users\\Nelala\\Desktop\\csv\\strings-upravene (puvodni format).csv";
		Csv gui = new Csv(selected, ';');
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(640, 480);
		gui.setTitle("CSV");
		gui.setResizable(false);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
	}
}
