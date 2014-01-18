package sk.peterjurkovic.dril.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.analytics.tracking.android.ExceptionParser;

public class AnalyticsExceptionParser implements ExceptionParser{

	@Override
	public String getDescription(String thread, Throwable throwable) {
	     return bulidStackgrace(thread, throwable);
	  }
	
	
	
	private String bulidStackgrace(String thread, Throwable throwable){
		StringBuilder message = new StringBuilder("Thread: ");
		message.append(thread);
		message.append(" / ");
		message.append(ExceptionUtils.getStackTrace(ExceptionUtils.getRootCause(throwable)));
		return message.toString();
	}
}
