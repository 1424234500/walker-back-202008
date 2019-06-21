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
		Tools.out("crc", FileUtil.checksumCrc32(new File("/home/walker/tomcat/file.log")));
		
		
	}

}
