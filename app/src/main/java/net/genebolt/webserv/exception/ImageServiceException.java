package net.genebolt.webserv.exception;

public class ImageServiceException extends RuntimeException {

	private static final long serialVersionUID = 2468434988680850339L;	
	
	public ImageServiceException(String msg, Throwable throwable){
		super(msg, throwable);
	}
}