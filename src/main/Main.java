package main;

import com.pfe.SegmentationManager;
import com.pfe.entities.Window;

public class Main {

	public static void main(String[] args) {
		SegmentationManager sm = new SegmentationManager();
//		DataManager dm = DataManager.getInstance();
//		Window w = new Window();
//		dm.readLine(0,w);
//		dm.readLine(2,w);
//		dm.readLine(3,w);
//		dm.readLine(4,w);
//		dm.readLine(5,w);
//		System.out.println(w.getSet().get(0));
//		System.out.println(w.getSet().get(1));
		
		sm.recognizeADL();
		for (String s : Window.finalList) {
			System.out.println(s);
		}
	}

}
