package common;

import java.io.Serializable;

public class TransferrableData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageType messageType;
	private Object message;
	
	public TransferrableData(MessageType messageType, Object message) {
		this.messageType = messageType;
		this.message = message;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
	
	public Object getMessage() {
		return message;
	}
}
