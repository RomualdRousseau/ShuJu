package org.shuju.cv;

public abstract class ISearchBitmap
{
	public abstract int getWidth();

	public abstract int getHeight();

	public abstract int get(int x, int y);

	public abstract void set(int x, int y, int v);
}