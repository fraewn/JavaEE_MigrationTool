package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestStateMachine {

	@Test
	public void testStateMachine() {
		StateMachine<TestStates> states = new StateMachine<>(TestStates.class);
		assertEquals(false, states.isProcessDone());
		assertEquals(TestStates.FIRST, states.getCurrent());
		assertEquals(TestStates.FIRST, states.startState());
		assertEquals(TestStates.LAST, states.finishedState());
		states.nextStep();
		assertEquals((int) ((2. / 3.) * 100), states.getProcentOfProgress());
		assertEquals(TestStates.SECOND, states.getCurrent());
		states.nextStep();
		states.nextStep();
		assertEquals(TestStates.LAST, states.getCurrent());
		assertEquals(true, states.isProcessDone());
	}

	enum TestStates {
		FIRST, SECOND, LAST;
	}
}
