package org.stepik.is2018.korchagin;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class Balancer2Test extends BalancerTests {
	@Override
	protected BalancerBase getBalancer() {
		return new Balancer2(threadCount, taskQueueSize);
	}

}
