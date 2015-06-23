package com.gomusic.app.model;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AlbumDetails {
	public String albumTitle,albumArtist,albumIcon;
	public ArrayList<Song> songs;
	
	AlbumDetails(){
		songs = new ArrayList<Song>(); 
	}

	public static AlbumDetails parseHtml(String response) {
		AlbumDetails album = new AlbumDetails();
		try{
			Document doc = Jsoup.parse(response);
			Element albumDescription = doc.select(".single-song-decription").first();
			Element pictureC = albumDescription.select(".picture-of-details img").first();
			album.albumIcon = pictureC.attr("src");
			Element descC = albumDescription.select(".iontro-about-select-area").first();
			Elements others = descC.select("p");
			album.albumTitle = others.get(0).select("span").text();
			album.albumArtist = others.get(1).select("a").text();
			Elements songsLinks =  doc.select("ul.for-play li");
			if(songsLinks != null){
				for(int i=0; i < songsLinks.size();i++){
					Element span = songsLinks.get(i).select("span").first();
					Element more = songsLinks.get(i).select("a.more").first();
					Song song = new Song(span.text(), null, more.attr("href"));
					album.songs.add(song);
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return album;
	}
}
