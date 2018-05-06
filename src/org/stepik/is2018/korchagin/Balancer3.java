package org.stepik.is2018.korchagin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.stepik.is2018.korchagin.Task;
/**
 * Продвинутые очереди из java.util.concurrent
 * @author MY
 *
 */
public class Balancer3 extends BalancerBase {
	Balancer3(int executorCount, int taskLimitPerExecutor) {
		super(executorCount, taskLimitPerExecutor);
		allTasks = new ArrayList<>(executorCount);
		for (int i = 0; i < executorCount; i++) {
			allTasks.add(new ArrayBlockingQueue<>(taskLimitPerExecutor));
		}
		placeCount = new ArrayList<>(taskLimitPerExecutor);
		for (int i = 0; i < taskLimitPerExecutor; i++) {
			placeCount.add(new ConcurrentLinkedQueue<>());
			for (int j = 0; j < executorCount; j++) {
				placeCount.get(i).add(j);
			}
		}
	}

	private final List<BlockingQueue<Task>> allTasks;
	private final List<ConcurrentLinkedQueue<Integer>> placeCount;

	@Override
	protected Task getTaskForExecutorImpl(int executorNumber) throws InterruptedException {
		BlockingQueue<Task> tasks = allTasks.get(executorNumber);
		Task result = tasks.take();
		placeCount.get(tasks.size()).add(executorNumber);
		synchronized (this) {
			this.notify();
		}
		return result;
	}

	@Override
	protected void addTaskImpl(Task task) {
		for (;;) {
			for (int i = 0; i < taskLimit; i++) {
				Integer cand = placeCount.get(i).poll();
				if (cand != null) {
					if (allTasks.get(cand).offer(task)) {
						task = null;
						break;
					}
				}
			}
			if (task == null) {
				break;
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
