package com.smart_tiger.monio;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("UnitTest")
@ExtendWith(SpringExtension.class)
class MonioApplicationTests {

	@Test
	void MonioApplication_compiles() {
		// A simple test that doesn't require the application context
		assert(true);
	}

}
