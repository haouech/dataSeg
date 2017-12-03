package window;

import java.util.ArrayList;
import java.util.List;

public class SensorDataSet {
	
	List<String> getSensorData()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("KitchenObj,Cup,Tea,Sugar");
		list.add("KitchenObj,Mug,Coffee,Sugar");
		list.add("KitchenObj,Cup,Tea,Sugar");
		return list;
	}
}
