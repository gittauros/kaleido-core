package com.tauros.kaleido.core.kaleidolib;

import com.tauros.kaleido.core.util.ConsoleLog;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import static com.tauros.kaleido.core.kaleidolib.SizeUnit.convertToBytes;

/**
 * Created by tauros on 2016/4/17.
 */
public final class KaleidoCache<K, E> {

	private final int TO_CACHE_THRESHOLD = 2;

	private final MemoryCalculator<E> memoryCalculator;
	private       Vector<Node>        history;
	private       long                historyMemory;
	private       Vector<Node>        cache;
	private       long                cacheMemory;
	private final long                maxMemory;


	public KaleidoCache(long maxMemory, MemoryCalculator<E> memoryCalculator) {
		this.maxMemory = maxMemory;
		this.memoryCalculator = memoryCalculator;
		this.history = new Vector<>();
		this.cache = new Vector<>();
	}

	public KaleidoCache(SizeUnit unit, long maxMemory, MemoryCalculator<E> memoryCalculator) {
		this(convertToBytes(unit, maxMemory), memoryCalculator);
	}

	private class Node {
		int hits;
		K   key;
		E   data;

		public Node(K key, E data) {
			this.key = key;
			this.data = data;
			this.hits = 0;
			hit();
		}

		public long memorySize() {
			return memoryCalculator.calculate(this.data);
		}

		public Node hit() {
			hits++;
			return this;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Node node = (Node) o;

			return key.equals(node.key);

		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}

	public synchronized int size() {
		int size = history.size() + cache.size();
		return size;
	}

	public synchronized long memorySize() {
		long memory = historyMemory + cacheMemory;
		return memory;
	}

	private boolean inHistory(Node node) {
		return history.contains(node);
	}

	private boolean inCache(Node node) {
		return cache.contains(node);
	}

	private long maxCacheMemory() {
		return (long) ((double) maxMemory * 0.7);
	}

	private long maxHistoryMemory() {
		return maxMemory - cacheMemory;
	}

	private boolean isHistoryFull() {
		return historyMemory >= maxHistoryMemory();
	}

	private boolean isCacheFull() {
		return cacheMemory >= maxCacheMemory();
	}

	private boolean willCacheFull(long memory) {
		return cacheMemory + memory >= maxCacheMemory();
	}

	private Node findCacheNode(Node keyNode) {
		int index = cache.indexOf(keyNode);
		return cache.get(index);
	}

	private Node findHistoryNode(Node keyNode) {
		int index = history.indexOf(keyNode);
		return history.get(index);
	}

	private ReentrantLock trimLock = new ReentrantLock();

	private void trimHistory() {
		trimLock.lock();
		while (historyMemory > maxHistoryMemory()) {
			Node node = history.lastElement();
			history.remove(history.size() - 1);
			historyMemory -= node.memorySize();
			ConsoleLog.e("缓存历史抛弃：key=" + node.key);
		}
		trimLock.unlock();
	}

	private void trimCache() {
		trimLock.lock();
		while (cacheMemory > maxCacheMemory()) {
			Node node = cache.lastElement();
			cache.remove(cache.size() - 1);
			cacheMemory -= node.memorySize();
			ConsoleLog.e("缓存抛弃：key=" + node.key);
		}
		trimLock.unlock();
	}

	private boolean toCache(Node node, boolean inCache) {
		if (!inCache && (isCacheFull() || willCacheFull(node.memorySize()))) {
			return false;
		}
		if (inCache) {
			int index = cache.indexOf(node);
			Node cacheNode = cache.get(index);
			cacheNode.hit();
			cache.remove(index);
			cache.add(0, cacheNode);
		} else {
			cache.add(0, node);
			cacheMemory += node.memorySize();
			if (isCacheFull()) {
				trimCache();
			}
			if (isHistoryFull()) {
				trimHistory();
			}
		}
		return true;
	}

	public synchronized boolean put(K key, E element) {
		if (key == null || element == null) {
			return false;
		}
		Node node = new Node(key, element);
		if (inCache(node)) {
			toCache(node, true);
			return true;
		}
		if (inHistory(node)) {
			node = findHistoryNode(node).hit();
			if (node.hits >= TO_CACHE_THRESHOLD) {
				boolean success = toCache(node, false);
				if (success) {
					history.remove(node);
					historyMemory -= node.memorySize();
				}
			}
		} else {
			history.add(0, node);
			historyMemory += node.memorySize();
			if (isHistoryFull()) {
				trimHistory();
			}
		}
		return true;
	}

	public synchronized E get(K key) {
		if (key == null) {
			return null;
		}
		Node keyNode = new Node(key, null);
		E data = null;
		if (inCache(keyNode)) {
			data = findCacheNode(keyNode).data;
			put(key, data);
		}
		if (inHistory(keyNode)) {
			data = findHistoryNode(keyNode).data;
			put(key, data);
		}
		return data;
	}
}
