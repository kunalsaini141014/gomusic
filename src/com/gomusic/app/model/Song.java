package com.gomusic.app.model;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Song {
	public String title,artist,downloadLink;
	public static boolean doMoveForward;
	
	public Song(String title, String artist,String downloadLink) {
		this.title = title;
		this.artist = artist;
		this.downloadLink = downloadLink;
	}

	public static ArrayList<Song> parseHtml(String response) {
		Document doc = Jsoup.parse(response);
		ArrayList<Song> songs = new ArrayList<Song>();
		Elements catElements = doc.select("ul.link-full li a");
		Elements navElements = doc.select("a.next_page");
		if(navElements != null && navElements.size() == 2){
			doMoveForward = true;
		}else{
			doMoveForward = false;
		}
		for (Element elem : catElements) {
			Element span = elem.select("span").first();
			if (elem.hasText() && elem.hasAttr("href")) {
				String text = elem.text();
				if(!text.contains(" [")){
					text = text.replace("[", " [");
				}
				Song category = new Song(text,span != null && span.hasText() ? span.text() : "",elem.attr("href"));
				//System.out.println(elem.text());
				songs.add(category);
			}
		}
		return songs;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean retVal = false;
        if (o instanceof Song){
        	Song ptr = (Song) o;
            retVal = ptr.downloadLink.equals(this.downloadLink);
        }
     return retVal;
	}
	
	@Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.title != null ? this.downloadLink.hashCode() : 0);
        return hash;
    }
}
