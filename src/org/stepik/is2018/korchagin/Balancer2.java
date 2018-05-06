package org.stepik.is2018.korchagin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.stepik.is2018.korchagin.Task;
/**
 * Отдельные блокировки для каждой очереди
 * @author MY
 *
 */
public class Balancer2 extends BalancerBase {
	Balancer2(int executorCount, int taskLimitPerExecutor) {
		super(executorCount, taskLimitPerExecutor);

		allTasks = new ArrayList<>(executorCount);
		initAllTasksList();
	}

	private final List<Queue<Task>> allTasks;

	void initAllTasksList() {
		for (int i = 0; i < executorCount; i++) {
			allTasks.add(new ArrayDeque<>(taskLimit));
		}
	}

	@Override
	protected Task getTaskForExecutorImpl(int executorNumber) throws InterruptedException {
		Queue<Task> tasks = allTasks.get(executorNumber);

		while (tasks.isEmpty()) {
			synchronized (this) {
				this.notify();
			}
			synchronized (tasks) {
				if (tasks.isEmpty()) {
					tasks.wait();
				}
			}
		}
		Task result = tasks.poll();

		synchronized (this) {
			this.notify();
		}
		return result;
	}

	Queue<Task> findLowestElementQueue() {
		int lowest = taskLimit;
		Queue<Task> select = null;
		for (Queue<Task> queue : allTasks) {
			synchronized (queue) {
				if (queue.size() < lowest) {
					lowest = queue.size();
					select = queue;
				}
			}
		}
		return select;
	}

	@Override
	protected void addTaskImpl(Task task) {
		synchronized (this) {
			while (true) {
				Queue<Task> selected = findLowestElementQueue();
				if (selected != null) {
					synchronized (selected) {
						selected.add(task);
						selected.notify();
					}
				} else {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
