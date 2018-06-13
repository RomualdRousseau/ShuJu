package com.github.romualdrousseau.shuju.ml.qlearner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class MemoryMap
{
	public MemoryMap() {
		this.memoryMax = -1;
		clear();
	}

	public MemoryMap(int n) {
		this.memoryMax = n;
		clear();
	}

	public void clear() {
		this.timestamp = 0;
		this.map.clear();
	}

	public void put(int s, int a, double v, double learnRate) {
		Integer[] node = new Integer[] {s, a};
		
		MemoryCell cell = this.map.get(node);
		if(cell == null) {
			forgetOldestMemory();
			cell = new MemoryCell(s, a, learnRate * v, this.timestamp);
			this.map.put(node, cell);	
		}
		else {
			cell.reward += learnRate * (v - cell.reward);
			cell.timestamp = this.timestamp;
		}

		this.timestamp++;
	}

	public MemoryCell replay(int s) {
		MemoryCell result = null;
		for(MemoryCell e: map.values()) if(e.state == s) {
			if(result == null || e.reward > result.reward) {
				result = e;
			}
		}
		if(result != null) {
			result.timestamp = this.timestamp;
		}
		return result;
	}

	public ArrayList<MemoryCell> replay(int start, int end) {
		ArrayList<MemoryCell> result = new ArrayList<MemoryCell>();
		for(int i = start; i < end; i++) {
			MemoryCell bestMemory = replay(i);
			if(bestMemory != null &&  Math.random() >= 0.2) {
				result.add(bestMemory);
			}
		}
		return result;
	}

	private void forgetOldestMemory() {
		if(this.memoryMax < 0 || this.map.size() < this.memoryMax) {
			return;
		}

		MemoryCell oldestCell = null;
		for(MemoryCell e: map.values()) {
			if(oldestCell == null || e.timestamp < oldestCell.timestamp) {
				oldestCell = e;
			}
		}
		
		Integer[] nodeToRemove = new Integer[] {oldestCell.state, oldestCell.action};
		map.remove(nodeToRemove);
	}

	private HashMap<Integer[], MemoryCell> map = new HashMap<Integer[], MemoryCell>();
	private int memoryMax;
	private int timestamp;
}
