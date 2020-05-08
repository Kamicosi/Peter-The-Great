package tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.junit.Test;

public class ScrapeTests {

	@Test
	public void imageTest() throws IOException {
		String searchTerm = "okboomergirl";
		try {
			URL url = new URL("https://www.bing.com/images/search?q=" + searchTerm);
			Scanner scanner = new Scanner(url.openStream());
			StringBuffer pageContent = new StringBuffer();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				pageContent.append(line + "\n");
			}
			scanner.close();
			System.out.println(pageContent);
			//String image = pageContent.substring(pageContent.indexOf("<a class=\"thumb\" target=\"_blank\" href=\"") + 39);
			//image = image.substring(0, image.indexOf("h=") - 2);
			//System.out.println(image);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
