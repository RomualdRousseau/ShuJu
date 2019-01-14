package com.github.romualdrousseau.shuju.cv;

public class EdgeFilter
{
	public EdgeFilter() {
    }

    public void apply(ISearchBitmap searchBitmap, double threshold) {
		for(int y = 0; y < searchBitmap.getHeight(); y++) {
			for(int x = 0; x < searchBitmap.getWidth(); x++) {
				int lx = this.edgeX.sobel(searchBitmap, x, y);
                int ly = this.edgeY.sobel(searchBitmap, x, y);
                double acc = Math.sqrt(lx * lx + ly * ly);
                //double phi = Math.atan2(ly, lx);
				if(acc < threshold) {
					searchBitmap.set(x, y, 0);
				}
			}
		}
    }

    public void applyNeg(ISearchBitmap searchBitmap, double threshold) {
		for(int y = 0; y < searchBitmap.getHeight(); y++) {
			for(int x = 0; x < searchBitmap.getWidth(); x++) {
				int lx = this.edgeX.sobel(searchBitmap, x, y);
                int ly = this.edgeY.sobel(searchBitmap, x, y);
                double acc = Math.sqrt(lx * lx + ly * ly);
                //double phi = Math.atan2(ly, lx);
				if(acc >= threshold) {
					searchBitmap.set(x, y, 1);
				}
			}
		}
    }

    public void apply(ISearchBitmap sourceBitmap, ISearchBitmap destBitmap, double threshold) {
		for(int y = 0; y < sourceBitmap.getHeight(); y++) {
			for(int x = 0; x < sourceBitmap.getWidth(); x++) {
                int lx = this.edgeX.sobel(sourceBitmap, x, y);
                int ly = this.edgeY.sobel(sourceBitmap, x, y);
                double acc = Math.sqrt(lx * lx + ly * ly);
                //double phi = Math.atan2(ly, lx);
				if(acc < threshold) {
					destBitmap.set(x, y, 0);
                }
                else {
                    destBitmap.set(x, y, 1);
                }
			}
		}
    }

    private Template edgeX = new Template(new int[][] {
        {1, 0, -1},
        {2, 0, -2},
        {1, 0, -1}
    });

    private Template edgeY = new Template(new int[][] {
        { 1,  2,  1},
        { 0,  0,  0},
        {-1, -2, -1}
    });
}
