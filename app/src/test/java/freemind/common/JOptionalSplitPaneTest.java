package freemind.common;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import freemind.main.Resources;
import tests.freemind.FreeMindMainMock;

public class JOptionalSplitPaneTest {

	@Test
	void testComponent() {
		Resources.createInstance(new FreeMindMainMock());
		final JFrame frame = new JFrame("JOptionalSplitPane");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JOptionalSplitPane panel = new JOptionalSplitPane();
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(5, 1));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setSize(800, 400);
		contentPane.add(new JButton(new AbstractAction("Add 0") {
			private int index = 0;

			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.setComponent(new JLabel("links " + index++), 0);
			}
		}));
		contentPane.add(new JButton(new AbstractAction("Add 1") {
			private int index = 0;

			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.setComponent(new JLabel("rechts " + index++), 1);
			}
		}));
		contentPane.add(new JButton(new AbstractAction("Remove 0") {

			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.removeComponent(0);
			}
		}));
		contentPane.add(new JButton(new AbstractAction("Remove 1") {

			@Override
			public void actionPerformed(ActionEvent pE) {
				panel.removeComponent(1);
			}
		}));
		contentPane.add(panel);
		frame.setVisible(true);
	}
}
