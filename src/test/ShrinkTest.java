package test;

import org.junit.Test;

import com.pfe.entities.Activity;
import com.pfe.entities.Window;

public class ShrinkTest {

	@Test
	public void testCorrectActivity() {
		Window window = new Window();
		Activity activity = new Activity("MakeTea");
		assert(activity.getDuration() == 20);
		window.setActive(true);
		window.setShrinkable(true);
		window.setEndTime(100);
		activity.setAsserted(true);
		boolean shrinked = window.attemptShrink(activity, 50);
		assert(shrinked);
		System.out.println("Shrinked");
		assert(window.getEndTime() == 50);
		System.out.println("Correct shrink time");
	}
	
	@Test
	public void testExhaustedActivity() {
		Window window = new Window();
		Activity activity = new Activity("MakeTea");
		assert(activity.getDuration() == 20);
		window.setActive(true);
		window.setShrinkable(true);
		window.setEndTime(100);
		activity.setAsserted(false);
		activity.setDuration(5);
		boolean shrinked = window.attemptShrink(activity, 8);
		assert(shrinked);
		assert(window.getEndTime() == 8);
	}

}
