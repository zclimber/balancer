package org.stepik.is2018.korchagin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.stepik.is2018.korchagin.Task;

/**
 * 1 глобальная блокировка
 * @author MY
 *
 */
public class Balancer1 extends BalancerBase {
	Balancer1(int executorCount, int taskLimitPerExecutor) {
		super(executorCount, taskLimitPerExecutor);

		allTasks = new ArrayList<>(executorCount);
		initAllTasksList();
	}

	private final Object globalLock = new Object();

	private final List<Queue<Task>> allTasks;

	void initAllTasksList() {
		for (int i = 0; i < executorCount; i++) {
			allTasks.add(new ArrayDeque<>(taskLimit));
		}
	}

	@Override
	protected Task getTaskForExecutorImpl(int executorNumber) throws InterruptedException {
		Queue<Task> tasks = allTasks.get(executorNumber);
		Task result = null;

		synchronized (globalLock) {
			while (tasks.isEmpty()) {
				globalLock.wait();
			}
			result = tasks.poll();
			globalLock.notifyAll();
		}

		return result;
	}

	Queue<Task> findLowestElementQueue() {
		int lowest = taskLimit;
		Queue<Task> select = null;
		for (Queue<Task> queue : allTasks) {
			if (queue.size() < lowest) {
				lowest = queue.size();
				select = queue;
			}
		}
		return select;
	}

	@Override
	protected void addTaskImpl(Task task) {
		synchronized (globalLock) {
			while (true) {
				Queue<Task> selected = findLowestElementQueue();
				if (selected != null) {
					selected.add(task);
					globalLock.notifyAll();
					break;
				} else {
					try {
						globalLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
