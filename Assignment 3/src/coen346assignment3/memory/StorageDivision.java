package coen346assignment3.memory;

import java.sql.Time;
import java.time.LocalTime;

public abstract class StorageDivision {

	protected String variableID;
	protected int value;
	
	protected Time lastAccess;
	
	protected boolean locked = false;
	
	/**
	 * returns value stored in storage division
	 * @return
	 */
	public int GetValue() {
		UpdateLastAccess();
		return value;
	}
	
	/**
	 * Returns the ID of the variable
	 * @return
	 */
	public String GetVariableID() {
		UpdateLastAccess();
		return variableID;
	}

	private void UpdateLastAccess() {
		this.lastAccess = Time.valueOf(LocalTime.now());
	}
	
	public synchronized boolean IsLocked() {
		return locked;
	}
	
	public synchronized void SetLocked(boolean value) {
		locked = value;
	}
}
