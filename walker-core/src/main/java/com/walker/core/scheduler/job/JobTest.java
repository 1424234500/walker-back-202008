package com.walker.core.scheduler.job;

import com.walker.common.util.Tools;
import com.walker.core.scheduler.TaskJob;

public class JobTest extends TaskJob{

	@Override
	public String make() {
		Tools.out("scheduler quartz run test");

		return "world";
	}
}
