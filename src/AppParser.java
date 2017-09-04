import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppParser extends Thread {
	
	private String url;
	private ArrayList<String> lista;
	
	public AppParser(String url, ArrayList<String> lista) {
		this.url = url;
		this.lista = lista;
	}
	
	public void run() {
		Document doc;
		try {
			doc = Jsoup.connect(this.url).get();
			
			Element list = doc.getElementById("content");
			
			String image = list.select(".artwork meta").first().attr("content");
			
			System.out.print(".");
			
			this.lista.add(image);
		} catch (Exception e) {
			System.out.print("x");
			this.lista.add("");
		}
	}
	
	private void print(Object msg) {
		System.out.println(msg);
	}
}
