package com.mantism.storage;

import java.util.HashSet;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
/*
 * Entity class for storing languages that i've coded in, essentially key
 * value pair between language and line count
 */
@Entity
public class LanguageEntity {
	//empty constructor
	public LanguageEntity() {}
	
	private int count;
	
	@PrimaryKey
	private String language;
	
	public void setLanguage(String l) {
		language = l;
	}
	
	public void updateCount(int c) {
		count += c;
	}
	
	public void resetCount() {
		count = 0;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public int getCount() {
		return count;
	}
}
