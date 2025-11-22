package freemind.common;

import org.junit.jupiter.api.Test;

import freemind.main.Resources;
import tests.freemind.FreeMindMainMock;

public class FreeMindProgressMonitorTest {

	@Test
	void testShowProgressIntIntStringObjectArray() throws InterruptedException {
		FreeMindMainMock mock = new FreeMindMainMock();
		Resources.createInstance(mock);
		FreeMindProgressMonitor progress = new FreeMindProgressMonitor("title");
		progress.setVisible(true);
		for (int i = 0; i < 10; i++) {
			boolean canceled =
					progress.showProgress(i, 10, "inhalt {0}", new Object[] {Integer.valueOf(i)});
			if (canceled) {
				progress.dismiss();
				System.exit(1);
			}
			Thread.sleep(1000L);
		}
		progress.dismiss();
	}
}
