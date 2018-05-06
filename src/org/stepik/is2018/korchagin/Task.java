package org.stepik.is2018.korchagin;

public class Task {
	private final int millis;
	private int completedTimes = 0;

	public int getCompletedTimes() {
		return completedTimes;
	}

	public Task(int millis) {
		this.millis = millis;
	}

	void complete() throws InterruptedException {
		Thread.sleep(millis);
		completedTimes++;
	}
}