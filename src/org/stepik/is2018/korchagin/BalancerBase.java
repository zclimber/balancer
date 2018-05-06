package org.stepik.is2018.korchagin;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BalancerBase {
	protected final int executorCount;
	protected final int taskLimit;
	private AtomicInteger totalTasks;

	BalancerBase(int executorCount, int taskLimitPerExecutor) {
		this.executorCount = executorCount;
		this.taskLimit = taskLimitPerExecutor;
		totalTasks = new AtomicInteger(0);
	}

	protected abstract Task getTaskForExecutorImpl(int executorNumber) throws InterruptedException;

	protected abstract void addTaskImpl(Task task);

	public Task getTaskForExecutor(int executorNumber) throws InterruptedException {
		totalTasks.decrementAndGet();
		if (allDone()) {
			synchronized (totalTasks) {
				totalTasks.notifyAll();
			}
		}
		Task task = getTaskForExecutorImpl(executorNumber);
		return task;
	}

	void addTask(Task task) {
		totalTasks.incrementAndGet();
		addTaskImpl(task);
	}

	boolean allDone() {
		return totalTasks.get() == -executorCount;
	}

	void waitDone() throws InterruptedException {
		while (!allDone()) {
			synchronized (totalTasks) {
				if (!allDone()) {
					totalTasks.wait();
				}
			}
		}
	}
}