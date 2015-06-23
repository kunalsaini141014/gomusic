package com.gomusic.app.model;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MusicCategory implements Comparable{
	public int _id;
	public String link, text;

	MusicCategory(String text, String link,int _id) {
		this.link = link;
		this.text = text;
		this._id = _id;
	}

	public static ArrayList<MusicCategory> parseHtml(String response) {
		Document doc = Jsoup.parse(response);
		ArrayList<MusicCategory> musicCategoryList = new ArrayList<MusicCategory>();
		Elements catElements = doc.select("ul.link-full li a");
		for (Element elem : catElements) {

			if (elem.hasText() && elem.hasAttr("href")) {
				MusicCategory category = new MusicCategory(elem.text(),elem.attr("href"),musicCategoryList.size()+1);
				//System.out.println(elem.text());
				musicCategoryList.add(category);
			}

		}
		return musicCategoryList;
	}
	
	public static ArrayList<MusicCategory> fetchDummy() {
		ArrayList<MusicCategory> musicCategoryList = new ArrayList<MusicCategory>();
		musicCategoryList.add(new MusicCategory("Punjabi","page.php?cat_name=Punjabi_Music",1));
		return musicCategoryList;
	}

	@Override
	public int compareTo(Object another) {
		MusicCategory target = (MusicCategory)another;
		return this.text.compareTo(target.text);
	}

}
