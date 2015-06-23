package com.gomusic.app.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gomusic.app.helpers.constants.Constants;

public class SongDetails {
	public String songTitle,songArtist,album,linkFor48Bit,linkFor128Bit,linkFor320Bit,albumIcon,artitstLink,albumLink;
	
	SongDetails(){
	}

	public static SongDetails parseHtml(String response) {
		SongDetails song = new SongDetails();
		try{
			Document doc = Jsoup.parse(response);
			Element songDescription = doc.select(".single-song-decription").first();
			Element pictureC = songDescription.select(".picture-of-details img").first();
			song.albumIcon = pictureC.attr("src");
			Element descC = songDescription.select(".iontro-about-select-area").first();
			Elements others = descC.select("p");
			song.songTitle = others.get(0).select("span").text();
			song.songArtist = others.get(1).select("a").text();
			song.album = others.get(2).select("a").text();
			String artitstLink = others.get(1).select("a").attr("href");
			song.artitstLink = Constants.CATEGORY_URL + artitstLink.substring(artitstLink.lastIndexOf("album2.php"));
			Elements downloadLinks =  doc.select(".link-full.download-files li");
			for(int i=0; i < downloadLinks.size();i++){
				Element span = downloadLinks.get(i).select("span").first();
				String link = span.attr("onclick");
				link = link.substring(link.indexOf("http://"),link.lastIndexOf("')"));
				switch(i){
				case 0:
					if(link.indexOf("p48.ve.vc") == -1){
						link = "http://p48.ve.vc" + link.substring(link.indexOf("/load"));
					}
					song.linkFor48Bit = link;
					break;
				case 1:
					if(link.indexOf("p128.ve.vc") == -1){
						link = "http://p128.ve.vc" + link.substring(link.indexOf("/load"));
					}
					song.linkFor128Bit = link;
					break;
				case 2:
					if(link.indexOf("p320.ve.vc") == -1){
						link = "http://p320.ve.vc" + link.substring(link.indexOf("/load"));
					}
					song.linkFor320Bit = link;
					break;
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return song;
	}
}
