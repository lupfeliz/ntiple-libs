package com.ntiple.libs.process;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerConsumerWorker<SourceType, ProduceType> implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ProducerConsumerWorker.class);
	
	public static final int STARTED = 0;
	public static final int PRODUCING = STARTED + 1;
	public static final int CONSUMING = PRODUCING + 1;
	
	private List<? extends Producer<SourceType,ProduceType>> producerList;
	private List<? extends Consumer<ProduceType>> consumerList;
	private Monitor produceMonitor;
	private Monitor consumeMonitor;
	private AtomicInteger state;
	private BlockingQueue<ProduceType> dataQueue;
	private int maxQueueSize;
	private int waitTimeout;
	
	protected ProducerConsumerWorker() { }
	
	public ProducerConsumerWorker(List<? extends Producer<SourceType, ProduceType>> producerList, List<? extends Consumer<ProduceType>> consumerList, int maxQueueSize, int waitTimeout) {
		this.producerList = producerList;
		this.consumerList = consumerList;
		this.maxQueueSize = maxQueueSize;
		this.waitTimeout = waitTimeout;
		produceMonitor = new Monitor();
		consumeMonitor = new Monitor();
		state = new AtomicInteger(STARTED);
		dataQueue = new LinkedBlockingQueue<ProduceType>();
	}
	
	public List<? extends Producer<SourceType,ProduceType>> getProducerList() {
		return producerList;
	}
	
	public List<? extends Consumer<ProduceType>> getConsumerList() {
		return consumerList;
	}
	
	public Object getProduceMonitor() {
		return produceMonitor;
	}
	
	public Object getConsumeMonitor() {
		return consumeMonitor;
	}
	
	public void run() {
		for (Producer<SourceType, ProduceType> thread : producerList) { 
			thread.setVariables(dataQueue, maxQueueSize, state, produceMonitor, consumeMonitor, waitTimeout);
			thread.setName("PRODUCER");
			thread.start(); 
		}
		logger.trace("PRODUCER STARTED..");
		try { Thread.sleep(waitTimeout); } catch (Exception ignore) { }
		
		for (Consumer<ProduceType> thread : consumerList) { 
			thread.setVariables(dataQueue, state, produceMonitor, consumeMonitor, waitTimeout);
			thread.setName("CONSUMER");
			thread.start(); 
		}
		logger.trace("CONSUMER STARTED..");
		try { Thread.sleep(waitTimeout); } catch (Exception ignore) { }
		
		List<? extends Thread> threadList = producerList;
		Object monitor = produceMonitor;
		for (Thread thread; threadList.size() > 0;) {
			try {
				while (state.get() < PRODUCING) {
					Thread.sleep(waitTimeout);
				}
				thread = threadList.remove(0);
				if (thread != null) {
					synchronized(monitor) { monitor.notify(); }
					thread.join();
				}
				if (threadList.size() == 0) {
					threadList = consumerList;
					monitor = consumeMonitor;
					if (state.get() < CONSUMING) {
						logger.trace("STATE SET TO CONSUMING..");
						state.set(CONSUMING);
					}
				}
			} catch (Exception e) { logger.error("", e); }
		}
	}
	
	public static abstract class Consumer<ProduceType> extends Thread {
		private BlockingQueue<ProduceType> dataQueue;
		private AtomicInteger state;
		private Object produceMonitor;
		private Object consumeMonitor;
		private int waitTimeout;
		
		public Consumer() { }
		
		public void setVariables(BlockingQueue<ProduceType> dataQueue, AtomicInteger state, 
			Object produceMonitor, Object consumeMonitor, int waitTimeout) {
			this.dataQueue = dataQueue;
			this.state = state;
			this.produceMonitor = produceMonitor;
			this.consumeMonitor = consumeMonitor;
			this.waitTimeout = waitTimeout;
		}
		
		private void waitProduce(Object monitorProduce, Object monitorConsume, int waitTimeout) {
			synchronized (monitorProduce) { monitorProduce.notify(); }
			synchronized (monitorConsume) { monitorConsume.notify(); }
			Thread.yield();
			try {
				if (waitTimeout > 0) {
					synchronized(monitorConsume) { monitorConsume.wait(waitTimeout); }
				} else {
					synchronized (monitorConsume) { monitorConsume.wait(); }
				}
			} catch (Exception e) { logger.error("", e); }
		}
		
		public void run() {
			int waitTimeout = this.waitTimeout;
			while (state.get() < PRODUCING) {
				waitProduce(produceMonitor, consumeMonitor, -1);
			}
			
			while (state.get() < CONSUMING) {
				if (dataQueue.size() > 0) {
					try {
						ProduceType data = dataQueue.poll(waitTimeout, TimeUnit.MILLISECONDS);
						if (data != null) { 
							consume(data); 
							logger.debug("CONSUME:{} / {} {}", data, waitTimeout, dataQueue.size());
						}
					} catch (Exception e) { logger.error("", e); }
					waitProduce(produceMonitor, consumeMonitor, waitTimeout);
				} else {
					waitProduce(produceMonitor, consumeMonitor, -1);
				}
			}
			synchronized(consumeMonitor) { consumeMonitor.notify(); }
			while (state.get() == CONSUMING && dataQueue.size() > 0) {
				try {
					ProduceType data = dataQueue.poll(waitTimeout, TimeUnit.MILLISECONDS);
					if (data != null) { consume(data); }
					synchronized(produceMonitor) { produceMonitor.notify(); }
				} catch (Exception e) { logger.error("", e); }
			}
		}
		public abstract void consume(ProduceType data);
	}
	
	public static class Monitor { }
	
	public static abstract class Producer<SourceType, ProduceType> extends Thread {
		private SourceType source;
		private BlockingQueue<ProduceType> dataQueue;
		private int maxQueueSize;
		private AtomicInteger state;
		private Object produceMonitor;
		private Object consumeMonitor;
		private int waitTimeout;
		
		public Producer(SourceType source) {
			this.source = source;
		}
		
		public SourceType getSource() {
			return this.source;
		}
		
		public void setSource(SourceType source) {
			this.source = source;
		}
		
		public void setVariables(BlockingQueue<ProduceType> dataQueue, int maxQueueSize, AtomicInteger state, 
			Object produceMonitor, Object consumeMonitor, int waitTimeout) {
			this.dataQueue = dataQueue;
			this.maxQueueSize = maxQueueSize;
			this.state = state;
			this.produceMonitor = produceMonitor;
			this.consumeMonitor = consumeMonitor;
			this.waitTimeout = waitTimeout;
		}
		
		private void waitConsume(Object monitorProduce, Object monitorConsume, int waitTimeout) {
			synchronized (monitorProduce) { monitorProduce.notify(); }
			synchronized (monitorConsume) { monitorConsume.notify(); }
			Thread.yield();
			try {
				if (waitTimeout > 0) {
					synchronized(monitorProduce) { monitorProduce.wait(waitTimeout); }
				} else {
					synchronized(monitorProduce) { monitorProduce.wait(); }
				}
			} catch (Exception e) { logger.error("", e);}
		}
		
		public void run() {
			int waitTimeout = this.waitTimeout;
			if (state.get() < PRODUCING) {
				logger.trace("STATE SET TO PRODUCING..");
				state.set(PRODUCING);
			}
			while (isAvail(source)) {
				logger.trace("DATA-QUEUE:{}", dataQueue.size());
				ProduceType produce = produce(source);
				if (produce != null) {
					logger.debug("WAIT QUEUE REDUCE");
					while (dataQueue.size() >= maxQueueSize) {
						waitConsume(produceMonitor, consumeMonitor, waitTimeout);
					}
					logger.debug("ADD PRODUCE : {}", produce);
					dataQueue.add(produce);
					synchronized(consumeMonitor) { consumeMonitor.notify(); }
				}
				waitConsume(produceMonitor, consumeMonitor, waitTimeout);
			}
		}
		public abstract boolean isAvail(SourceType source);
		public abstract ProduceType produce(SourceType source);
	}
}