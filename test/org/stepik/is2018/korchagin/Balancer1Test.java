package org.stepik.is2018.korchagin;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class Balancer1Test extends BalancerTests {
	@Override
	protected BalancerBase getBalancer() {
		return new Balancer1(threadCount, taskQueueSize);
	}

}
