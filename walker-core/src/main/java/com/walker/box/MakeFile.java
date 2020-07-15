package com.walker.box;

import com.walker.common.util.ExcelUtil;
import com.walker.common.util.FileUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 401KB
 *
 * 1    10  42s
 * 2    10  32s
 * 4    10  28s
 * 6    10  28s
 * 10   10  35s
 *
 *
 */

public class MakeFile {



	public static void  main(String[] argv) throws Exception{//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ?
        System.setProperty("path_conf", "conf");

		new MakeFile().make(4, 2000);
	}
	MakeFile(){}

	public MakeFile make(int threadSize, int num) throws IOException {
        String savePath = "D:\\down\\make\\";
		List<List<String>> list = ExcelUtil.readExcelList(new File("exp1500.xls"));
		Tools.out("lines", list.size(), "eg:");
		Tools.out("b:", list.get(0));
		Tools.out("e:", list.get(list.size() - 1));

		new TaskThreadPie(num){
			@Override
			public void onStartThread(int threadNo) throws IOException, Exception {
				String toFile = savePath + "make." + threadNo + ".xls";
				if(new File(toFile).isFile() && new File(toFile).length() < 1024 * 10){
                    new File(toFile).delete();
                }
				if(new File(toFile).isFile()){
					Tools.out("have exists " + toFile);
					return;
				}
				List<List<?>> copy = new ArrayList<>();
				copy.addAll(list);
				List<String> titles = Arrays.asList("by threadNo:" + threadNo, ""+System.currentTimeMillis(), TimeUtil.getTimeYmdHmss(), "" + Math.random());
				ExcelUtil.saveToExcel(copy, titles, "sheet-make", toFile);
//				com.ExcelUtil.writeExcel(copy, toFile);

			}
		}.setThreadSize(threadSize).start();

//		Bean
        List<File> files = FileUtil.showDir(savePath,null);
        new TaskThreadPie(files.size()){
            @Override
            public void onStartThread(int threadNo) throws IOException, Exception {
                File file = files.get(threadNo);
                if(file.isFile())
                    Tools.out(file.getAbsolutePath(), FileUtil.checksumMd5(file));
            }
        }.start();


		return this;
	}




}
