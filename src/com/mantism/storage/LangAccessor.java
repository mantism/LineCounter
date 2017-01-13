package com.mantism.storage;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class LangAccessor {
	public PrimaryIndex<String,LanguageEntity> pIdx;
	
	public LangAccessor(EntityStore store) throws DatabaseException {
		pIdx = store.getPrimaryIndex(String.class, LanguageEntity.class);
	}
}
