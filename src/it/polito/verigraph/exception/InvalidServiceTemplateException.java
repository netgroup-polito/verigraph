package it.polito.verigraph.exception;

public class InvalidServiceTemplateException extends RuntimeException {

	private static final long serialVersionUID = -3138131670694139585L;

	public InvalidServiceTemplateException(String message) {
        super(message);
    }
}
