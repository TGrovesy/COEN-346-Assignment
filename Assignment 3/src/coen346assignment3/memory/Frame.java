package coen346assignment3.memory;

import java.sql.Time;
import java.time.LocalTime;

public class Frame extends StorageDivision {
	public Frame(String variableID, int value) {
		this.variableID = variableID;
		this.value = value;
		this.lastAccess = Time.valueOf(LocalTime.now());
	}
}
