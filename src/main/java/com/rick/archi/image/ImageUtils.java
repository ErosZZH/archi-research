package com.rick.archi.image;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

public class ImageUtils {

	public static void resetSize(String picFrom, String picTo) throws Exception {
		IMOperation op = new IMOperation();

		op.addImage();
		op.resize(100, 100, '^').gravity("center").extent(100, 100);
		op.addImage();

		ConvertCmd convert = new ConvertCmd(true);
		convert.run(op, picFrom, picTo);
	}
}
