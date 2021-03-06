package com.sly.demo.webrtc.config;

import com.sly.demo.webrtc.code.MessageTypeCode;

/**
 * 消息传递结构配置
 * @author sly
 *
 */
public class MessageStructConfig {
	private MessageTypeCode key;
	
	private Object value;
	
	private Object temp;
	
	

	public Object getTemp() {
		return temp;
	}

	public void setTemp(Object temp) {
		this.temp = temp;
	}

	public MessageTypeCode getKey() {
		return key;
	}

	public void setKey(MessageTypeCode key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	
}
