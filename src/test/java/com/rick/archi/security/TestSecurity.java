package com.rick.archi.security;

import java.io.IOException;

import org.junit.Test;

public class TestSecurity {

	@Test
	public void testFileType() throws IOException {
		System.out.println(SecurityUtils.getType("/home/eros/1.java").name());
	}

}
