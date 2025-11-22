package accessories.plugins.time;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import freemind.main.Resources;
import tests.freemind.FreeMindMainMock;

public class JTripleCalendarTest {

	@Test
	void testJTripleCalendar() {
		Resources.createInstance(new FreeMindMainMock());
		final JFrame frame = new JFrame("JTripleCalendar");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JTripleCalendar jcalendar = new JTripleCalendar(4, null);
		frame.getContentPane().add(jcalendar);
		frame.pack();
		// focus fix after startup.
		frame.addWindowFocusListener(new WindowAdapter() {

			public void windowGainedFocus(WindowEvent e) {
				frame.removeWindowFocusListener(this);
				jcalendar.getDayChooser().getSelectedDay().requestFocus();
			}
		});

		frame.setVisible(true);
	}
}
