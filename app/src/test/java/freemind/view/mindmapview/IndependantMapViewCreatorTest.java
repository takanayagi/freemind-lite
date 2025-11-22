package freemind.view.mindmapview;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import tests.freemind.FreeMindMainMock;

public class IndependantMapViewCreatorTest {

	@Test
	void testExportFileToPng() {
		String[] args = {"src/test/resources/tests/freemind/testmap.mm", System.getProperty("java.io.tmpdir") + "/testmap.png"};
		System.setProperty("java.awt.headless", "true");
		if (args.length != 2) {
			System.out.println(
					"Export map to png.\nUsage:\n java -jar lib/freemind.jar freemind.view.mindmapview.IndependantMapViewCreator <map_path>.mm <picture_path>.png");
			System.exit(0);
		}
		FreeMindMainMock freeMindMain = new FreeMindMainMock();
		IndependantMapViewCreator creator = new IndependantMapViewCreator();
		try {
			String outputFileName = args[1];
			creator.exportFileToPng(args[0], outputFileName, freeMindMain);
			System.out.println("Export to " + outputFileName + " done.");
			return;
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			freemind.main.Resources.getInstance().logException(e);

		}
		fail("Error.");
	}

}
