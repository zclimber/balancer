package org.stepik.is2018.korchagin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public abstract class BalancerTests {
	private ExecutorService executor;
	private List<ExecutorTask> executors;
	private BalancerBase balancer;
	private List<Task> tasks;

	@Parameter(value = 0)
	public int threadCount;

	@Parameter(value = 1)
	public int taskQueueSize;

	@Parameter(value = 2)
	public int taskCount;

	abstract protected BalancerBase getBalancer();

	@Before
	public void setUp() throws Exception {
		balancer = getBalancer();
		populateExecutors();
		createTasks();
	}

	public void populateExecutors() {
		executor = Executors.newCachedThreadPool();
		executors = new ArrayList<ExecutorTask>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			ExecutorTask task = new ExecutorTask(i, balancer);
			executors.add(task);
			executor.execute(task);
		}
	}

	public void createTasks() {
		tasks = new ArrayList<>(taskCount);
		for (int i = 0; i < taskCount; i++) {
			tasks.add(new Task(5));
		}
	}

	@After
	public void stopExecutors() throws Exception {
		for (ExecutorTask task : executors) {
			task.complete();
		}
		executor.shutdownNow();
	}

	@Test
	public void test() {
		for (Task task : tasks) {
			balancer.addTask(task);
		}
		try {
			balancer.waitDone();
		} catch (InterruptedException e) {
		}
		for (Task task : tasks) {
			assertEquals(1, task.getCompletedTimes());
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ 1, 1, 100 },
				{ 2, 2, 500 },
				{ 10, 10, 5000 },
				{ 100, 100, 50000 },
				{ 1000, 1000, 500000 } });
	}

}
