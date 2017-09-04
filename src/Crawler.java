import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class Crawler {
	public static int CUANTAS_APPS = 40;

	public static void main(String[] args) {
		try {
			print("Starting crawler");
			
			String[] categories = {"ios-social-networking/id6005?mt=8"};
			String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "U", "V", "W", "X", "Y", "Z"};
			
			for (String categoryName : categories) {
				String category = "https://itunes.apple.com/us/genre/" + categoryName;
				String categoryId = categoryName.split("/")[0];
				ArrayList<String> imagenes = new ArrayList<String>();
				
				for (String letter : letters) {
					String url = category + "&letter=" + letter;
					print("");
					print("Loading " + url);
					Document doc = Jsoup.connect(url).get();
					Element list = doc.getElementById("selectedcontent");
					Elements links = list.select("a[href]");

					for (int i = 0; i < CUANTAS_APPS; i++) {
						Element link = links.get(i);
						Thread.sleep(1000);
						(new AppParser(link.attr("abs:href"), imagenes)).start();
					}
				}
				
				while (imagenes.size() < CUANTAS_APPS*letters.length) {
					Thread.sleep(5000);
				}

				print("");
				System.out.print("Loading images...");

				int[] rch = new int[256];
				int[] gch = new int[256];
				int[] bch = new int[256];

				for (String s : imagenes) {
					if(s.length() <= 0) continue;
					URL url = new URL(s);
					BufferedImage image = ImageIO.read(url);
					int iWidth = image.getWidth();
					int iHeight = image.getHeight();
					for (int x = 0; x < iWidth; x++) {
						for (int y = 0; y < iHeight; y++) {
							Color c = new Color(image.getRGB(x, y));
							rch[c.getRed()]++;
							gch[c.getGreen()]++;
							bch[c.getBlue()]++;
						}
					}
					System.out.print(".");
				}
				
				print("");
				print("Processing...");
				
				int[] rchc = rch.clone();
				int[] gchc = gch.clone();
				int[] bchc = bch.clone();
				
				Arrays.sort(rchc);
				Arrays.sort(gchc);
				Arrays.sort(bchc);
				
				outer: for (int i = 0; i < 5; i++) {
					rinner: for(int j = 0; j < rch.length; j++) {
						if (rchc[rch.length - 1 - i] == rch[j]) {
							print("Top " + (i+1) + " red is " + j);
							break rinner;
						}
					}
					ginner: for(int j = 0; j < gch.length; j++) {
						if (gchc[gch.length - 1 - i] == gch[j]) {
							print("Top " + (i+1) + " green is " + j);
							break ginner;
						}
					}
					binner: for(int j = 0; j < bch.length; j++) {
						if (bchc[bch.length - 1 - i] == bch[j]) {
							print("Top " + (i+1) + " blue is " + j);
							break binner;
						}
					}
				}

				BufferedImage canvas = new BufferedImage(818, 350, BufferedImage.TYPE_INT_ARGB);
				BufferedImage rcanvas = new BufferedImage(818, 350, BufferedImage.TYPE_INT_ARGB);
				BufferedImage gcanvas = new BufferedImage(818, 350, BufferedImage.TYPE_INT_ARGB);
				BufferedImage bcanvas = new BufferedImage(818, 350, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) canvas.getGraphics();
				Graphics2D rg = (Graphics2D) rcanvas.getGraphics();
				Graphics2D gg = (Graphics2D) gcanvas.getGraphics();
				Graphics2D bg = (Graphics2D) bcanvas.getGraphics();

				int max = 0;
				int rmax = 0;
				int gmax = 0;
				int bmax = 0;

				for (int i = 0; i < 256; i++) {
					if (rch[i] > max) {
						max = rch[i];
					}
					if (rch[i] > rmax) {
						rmax = rch[i];
					}
					if (gch[i] > max) {
						max = gch[i];
					}
					if (gch[i] > gmax) {
						gmax = gch[i];
					}
					if (bch[i] > max) {
						max = bch[i];
					}
					if (bch[i] > bmax) {
						bmax = bch[i];
					}
				}
				
				Double scale = 300D / Math.log10(max);
				Double rscale = 300D / Math.log10(rmax);
				Double gscale = 300D / Math.log10(gmax);
				Double bscale = 300D / Math.log10(bmax);
				
				g.setColor(new Color(0, 0, 0, 255));
				g.fillRect(23, 325, 512, 2);
				g.fillRect(23, 25, 2, 302);
				for(int i = 1; i < 10; i++) {
					int num = (int) Math.pow(10, i);
					if(max > num)
						g.drawString(String.valueOf(num), 0, 325 - Math.round(Math.log10(num) * scale));
				}
//				g.drawString(String.valueOf(max), 2, 325 - Math.round(Math.log10(max) * scale));
				
				rg.setColor(new Color(0, 0, 0, 255));
				rg.fillRect(23, 325, 512, 2);
				rg.fillRect(23, 25, 2, 302);
				for(int i = 1; i < 10; i++) {
					int num = (int) Math.pow(10, i);
					if(rmax > num)
						rg.drawString(String.valueOf(num), 0, 325 - Math.round(Math.log10(num) * rscale));
				}
				rg.drawString(String.valueOf(rmax), 2, 325 - Math.round(Math.log10(rmax) * rscale));
				
				gg.setColor(new Color(0, 0, 0, 255));
				gg.fillRect(23, 325, 512, 2);
				gg.fillRect(23, 25, 2, 302);
				for(int i = 1; i < 10; i++) {
					int num = (int) Math.pow(10, i);
					if(gmax > num)
						gg.drawString(String.valueOf(num), 0, 325 - Math.round(Math.log10(num) * gscale));
				}
				gg.drawString(String.valueOf(gmax), 2, 325 - Math.round(Math.log10(gmax) * gscale));
				
				bg.setColor(new Color(0, 0, 0, 255));
				bg.fillRect(23, 325, 512, 2);
				bg.fillRect(23, 25, 2, 302);
				for(int i = 1; i < 10; i++) {
					int num = (int) Math.pow(10, i);
					if(bmax > num)
						bg.drawString(String.valueOf(num), 0, 325 - Math.round(Math.log10(num) * bscale));
				}
				bg.drawString(String.valueOf(bmax), 2, 325 - Math.round(Math.log10(bmax) * bscale));
				
				for (int i = 0; i < 256; i++) {
					if (rch[i] > 0) {
						g.setColor(new Color(255, 0, 0, 100));
						int pos = (int) Math.round(Math.log10(rch[i]) * scale);
						g.fillRect(25 + i*3, 325 - pos, 1, pos);
						rg.setColor(new Color(255, 0, 0, 100));
						int rpos = (int) Math.round(Math.log10(rch[i]) * rscale);
						rg.fillRect(25 + i*3, 325 - rpos, 1, rpos);
					}
					if (rch[i] > 0) {
						g.setColor(new Color(0, 255, 0, 100));
						int pos = (int) Math.round(Math.log10(gch[i]) * scale);
						g.fillRect(26 + i*3, 325 - pos, 1, pos);
						gg.setColor(new Color(0, 255, 0, 100));
						int gpos = (int) Math.round(Math.log10(gch[i]) * gscale);
						gg.fillRect(26 + i*3, 325 - gpos, 1, gpos);
					}
					if (rch[i] > 0) {
						g.setColor(new Color(0, 0, 255, 100));
						int pos = (int) Math.round(Math.log10(bch[i]) * scale);
						g.fillRect(27 + i*3, 325 - pos, 1, pos);
						bg.setColor(new Color(0, 0, 255, 100));
						int bpos = (int) Math.round(Math.log10(bch[i]) * bscale);
						bg.fillRect(27 + i*3, 325 - bpos, 1, bpos);
					}
				}

				print("");
				print("Saving...");
				
				ImageIO.write(canvas, "png", new File("./data/" + categoryId +".png"));
				ImageIO.write(rcanvas, "png", new File("./data/r-" + categoryId +".png"));
				ImageIO.write(gcanvas, "png", new File("./data/g-" + categoryId +".png"));
				ImageIO.write(bcanvas, "png", new File("./data/b-" + categoryId +".png"));
				
				print("");
				print("Done");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void print(String msg) {
		System.out.println(msg);
	}
}
