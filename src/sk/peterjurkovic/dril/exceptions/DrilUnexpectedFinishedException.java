package sk.peterjurkovic.dril.exceptions;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 30, 2013
 *
 */
public class DrilUnexpectedFinishedException  extends Exception{
	
	
	private static final long serialVersionUID = 8595146912456769905L;

	
	public DrilUnexpectedFinishedException(){}
	
	
	public DrilUnexpectedFinishedException(String message ){
		super(message);
	}
}
