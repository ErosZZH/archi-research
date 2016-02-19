package com.rick.archi.security;

import org.junit.Test;

import com.rick.archi.image.ImageUtils;

public class TestImage {

	@Test
	public void testResize() throws Exception {
		ImageUtils.resetSize("/home/eros/1.png", "/home/eros/2.png");
	}

}
