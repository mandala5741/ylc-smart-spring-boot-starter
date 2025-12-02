package com.cqcloud.platform.context;

/**
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕2025年12月2日🐬🐇 💓💕
 */
public class TextContext {

	private byte LID; // 显示行号

	private byte DisMode;// 显示模式

	private byte DelayTime;// 停留时间

	private byte DisTimes;// 显示次数

	private int TextColor;// 文本颜色

	private String Text;// 显示文本

	public byte getLID() {
		return LID;
	}

	public void setLID(byte LID) {
		this.LID = LID;
	}

	public byte getDisMode() {
		return DisMode;
	}

	public void setDisMode(byte disMode) {
		DisMode = disMode;
	}

	public byte getDelayTime() {
		return DelayTime;
	}

	public void setDelayTime(byte delayTime) {
		DelayTime = delayTime;
	}

	public byte getDisTimes() {
		return DisTimes;
	}

	public void setDisTimes(byte disTimes) {
		DisTimes = disTimes;
	}

	public int getTextColor() {
		return TextColor;
	}

	public void setTextColor(int textColor) {
		TextColor = textColor;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public TextContext(byte LID, byte disMode, byte delayTime, byte disTimes, int textColor, String text) {
		this.LID = LID;
		DisMode = disMode;
		DelayTime = delayTime;
		DisTimes = disTimes;
		TextColor = textColor;
		Text = text;
	}

}
