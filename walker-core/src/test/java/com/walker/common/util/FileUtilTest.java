package com.walker.common.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void test() throws IOException {
//		3459791362
		Tools.out("crc", FileUtil.checksumCrc32(new File("/home/walker/e/help_note/shell/test.sh")));
	}
	@Test
	public void testPath() {
		String paths[] = {"test.sh",  "/home/test.sh", "test", ".sh"};
		for(String item : paths) {
			Tools.out(item, FileUtil.getFilePath(item), FileUtil.getFileName(item), FileUtil.getFileNameOnly(item), FileUtil.getFileType(item));
		
		}
		String path = "/home/test.sh";
		int index = path.lastIndexOf(File.separator);
		Tools.out(path.substring(0, index));
		String name = path.substring(index + 1, path.length());
		Tools.out(name);
		int index1 = name.lastIndexOf(".");
		Tools.out(name.substring(0, index1));
		Tools.out(name.substring(index1 +1, name.length()));
		
		
		
	}
	

}
