package com.mantism.storage;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DBWrapper {
	private static String envDirectory = null;
	private static Environment myEnv;
	private static EntityStore store;

	public DBWrapper(String dir) throws DatabaseException {
		envDirectory = dir;
		setup();
		System.out.println("BDB was initialized");
	}

	public void setup() {
		File envHome = new File(envDirectory);
		openEnv(envHome);
		openStore(myEnv);

	}

	// method for opening the environment given a directory
	public void openEnv(File envHome) throws DatabaseException {
		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(true);
			myEnv = new Environment(envHome, envConfig);
		} catch (DatabaseException dbe) {
			System.err.println("Error opening environment: " + dbe.toString());
		}
	}

	// method for opening the store given an environment
	public void openStore(Environment env) throws DatabaseException {
		try {
			StoreConfig storeConfig = new StoreConfig();
			storeConfig.setAllowCreate(true);
			storeConfig.setTransactional(true);
			store = new EntityStore(env, "Entity Store", storeConfig);
		} catch (DatabaseException dbe) {
			System.err.println("Error opening store: " + dbe.toString());
		}
	}

	// method for getting the environment
	public Environment getEnv() {
		return myEnv;
	}

	// getting the store
	public EntityStore getStore() {
		return store;
	}

	// closing the environment
	public void closeEnv() {
		if (myEnv != null) {
			try {
				myEnv.close();
			} catch (DatabaseException dbe) {
				System.err.println("Error closing environment" + dbe.toString());
			}
		}
	}

	// closing the store
	public void closeStore() {

		if (store != null) {
			try {
				store.close();
			} catch (DatabaseException dbe) {
				System.err.println("Error closing store: " + dbe.toString());
			}
		}
	}

	// closes both environment and store
	public void shutdown() {
		closeStore();
		closeEnv();
	}
}
