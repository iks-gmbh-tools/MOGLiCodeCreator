package com.iksgmbh.moglicc.demo;

import java.util.HashSet;
import java.util.Arrays;
import com.iksgmbh.moglicc.demo.Person;

public class Misc extends Person
{
	// instance fields
	private boolean ready;
	private int numberInt;
	private long numberLong;
	private float numberFloat;
	private double numberDouble;
	private byte numberByte;
	private char id;
	private String[] stringArray;
	private HashSet<String> hashSet;

	// ===============  setter methods  ===============

	public void setReady(final boolean ready)
	{
		this.ready = ready;
	}

	public void setNumberInt(final int numberInt)
	{
		this.numberInt = numberInt;
	}

	public void setNumberLong(final long numberLong)
	{
		this.numberLong = numberLong;
	}

	public void setNumberFloat(final float numberFloat)
	{
		this.numberFloat = numberFloat;
	}

	public void setNumberDouble(final double numberDouble)
	{
		this.numberDouble = numberDouble;
	}

	public void setNumberByte(final byte numberByte)
	{
		this.numberByte = numberByte;
	}

	public void setId(final char id)
	{
		this.id = id;
	}

	public void setStringArray(final String[] stringArray)
	{
		this.stringArray = stringArray;
	}

	public void setHashSet(final HashSet<String> hashSet)
	{
		this.hashSet = hashSet;
	}

	// ===============  getter methods  ===============

	public boolean getReady()
	{
		return ready;
	}

	public int getNumberInt()
	{
		return numberInt;
	}

	public long getNumberLong()
	{
		return numberLong;
	}

	public float getNumberFloat()
	{
		return numberFloat;
	}

	public double getNumberDouble()
	{
		return numberDouble;
	}

	public byte getNumberByte()
	{
		return numberByte;
	}

	public char getId()
	{
		return id;
	}

	public String[] getStringArray()
	{
		return stringArray;
	}

	public HashSet<String> getHashSet()
	{
		return hashSet;
	}

	// ===============  additional Javabean methods  ===============

	@Override
	public String toString()
	{
		return "Misc ["
				+ "ready = " + ready
				+ "numberInt = " + numberInt
				+ "numberLong = " + numberLong
				+ "numberFloat = " + numberFloat
				+ "numberDouble = " + numberDouble
				+ "numberByte = " + numberByte
				+ "id = " + id
				+ "stringArray = " + Arrays.toString(stringArray)
				+ "hashSet = " + hashSet
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Misc other = (Misc) obj;

		if (ready != other.ready)
			return false;
		if (numberInt != other.numberInt)
			return false;
		if (numberLong != other.numberLong)
			return false;
		if (Float.floatToIntBits(numberFloat) != Float.floatToIntBits(other.numberFloat))
			return false;
		if (Double.doubleToLongBits(numberDouble) != Double.doubleToLongBits(other.numberDouble))
			return false;
		if (numberByte != other.numberByte)
			return false;
		if (id != other.id)
			return false;
		if (!Arrays.equals(stringArray, other.stringArray))
			return false;
		if (hashSet == null)
		{
			if (other.hashSet != null)
				return false;
		} else
		{
			if (! hashSet.equals(other.hashSet))
				   return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

 		result = prime * result + (ready ? 1231 : 1237);
		result = prime * result + numberInt;
 		result = prime * result + (int) (numberLong ^ (numberLong >>> 32));
 		result = prime * result + Float.floatToIntBits(numberFloat);
		long temp = Double.doubleToLongBits(numberDouble);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + numberByte;
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(stringArray);
               result = prime * result + ((hashSet == null) ? 0 : 697516148);

		return result;
	}

}
