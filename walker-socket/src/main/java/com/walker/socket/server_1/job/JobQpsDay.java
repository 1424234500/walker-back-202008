package com.walker.socket.server_1.job;

import com.walker.common.util.Tools;
import com.walker.core.scheduler.TaskJob;

public class JobQpsDay extends TaskJob{


	@Override
	public String make() {
		Tools.out("scheduler quartz run test");
		return "world";
	}
}
