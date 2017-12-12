package window;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

public class main {
	
	public static void readLine(Window w, String file) throws IOException {
		String line;
		BufferedReader bufferreader = new BufferedReader(new FileReader(file));
        line = bufferreader.readLine();
 
        while (line != null) {     
            line = bufferreader.readLine();
            w.addSensor(line);
        }
	}

	public static void main(String[] args) {
		try {
			String query = "";
			Segmentation seg = new Segmentation();
			seg.init();
			//call ontology à voir
			
			Window w = new Window();
			
			readLine(w,"resources/input.txt");
	//		w.addSensor("Bathroom@0");
	//		w.addSensor("Kitchen@0");
	//		w.addSensor("Milk@0");
			List<Activity> activities = seg.doOntologicalAR(w);
			for(Activity act : activities) {
				System.out.println(act);
			}
			System.out.println("Second iteration");
			w.addSensor("Tea@0");
			activities = seg.doOntologicalAR(w);
			for(Activity act : activities) {
				System.out.println(act);
			}
			
			
			List<String> dataSet = new ArrayList<String>();
			dataSet.add("Bathroom@0");
			dataSet.add("Kitchen@0");
			dataSet.add("Milk@0");
	//		List<Activity> activities = seg.callOntology(dataSet);
			for(Activity act : activities) {
	//			System.out.println(act);
			}
	//		query = seg.getQuery();
	//		System.out.println(query);
	//		query = "hasLocation some Kitchen and hasHotDrinkType some Tea and hasAdding some Milk "
	//				+ "and hasContainer some Cup";
	//		Set<OWLClass> res = seg.DLQuery(query, seg.ontology);
	//		for(OWLClass c : res) {
	//			System.out.println(c.getIRI().getFragment());
	//		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}