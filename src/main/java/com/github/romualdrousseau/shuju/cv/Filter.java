package com.github.romualdrousseau.shuju.cv;

public class Filter
{
	public Filter(Template filter) {
    	this.filter = filter;
  	}

	public void apply(ISearchBitmap searchBitmap, int threshold) {
		int hw = filter.getWidth() / 2;
		int hh = filter.getHeight() / 2;

		for(int y = 0; y < searchBitmap.getHeight(); y++) {
			for(int x = 0; x < searchBitmap.getWidth(); x++) {

				int acc = 0;
				for(int i = 0; i < this.filter.getHeight(); i++) {
					for(int j = 0; j < this.filter.getWidth(); j++) {
						acc += this.filter.get(j, i) * searchBitmap.get(x + j - hw, y + i - hh);
					}
				}

				if(acc < threshold) {
					searchBitmap.set(x, y, 0);
				}
			}
		}
	}

	private Template filter;
}