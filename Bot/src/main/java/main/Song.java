package main;

import java.io.Serializable;

public class Song implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String title, url;

	public Song(String title, String url) {
		this.title = title;
		this.url = url;
	}

}
