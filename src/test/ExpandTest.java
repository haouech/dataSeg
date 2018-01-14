package test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.pfe.entities.Activity;
import com.pfe.entities.Window;

public class ExpandTest {

	@Test
	public void test() {
		Window window = new Window();
		Activity activity = new Activity("MakeTea");
		assert(activity.getDuration() == 20);
		window.setActive(true);
		window.setExpandable(true);
		window.setEndTime(5);
		activity.setSpecific(true);
		activity.setAsserted(false);
		assert(activity.getEndTime() > window.getEndTime());
		boolean expanded = window.attemptExpand(activity);
		assert(expanded);
		System.out.println("Expanded");
		System.out.println(window.getEndTime());
		assert(window.getEndTime() == 20);
		System.out.println("Correct expansion time");
	}

}
