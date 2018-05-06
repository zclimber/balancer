package org.stepik.is2018.korchagin;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorTask implements Runnable {

	private final int number;

	private final BalancerBase balancer;

	private final AtomicBoolean running = new AtomicBoolean(true);

	public ExecutorTask(int number, BalancerBase balancer) {
		this.number = number;
		this.balancer = balancer;
	}

	public void complete() {
		running.set(false);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				balancer.getTaskForExecutor(number).complete();
			} catch (InterruptedException e) {
				running.set(false);
			}
		}
	}

}
