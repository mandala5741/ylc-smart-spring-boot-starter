package com.cqcloud.platform.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 显示语音控制卡协议工具类（支持多种屏幕类型）
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕 2025年8月27日 🐬🐇 💓💕
 */
public class DisplayVoiceUtils {

	// 屏幕类型枚举（与你提供的枚举对应）
	public enum ScreenEnum {

		MODULE_VERTICAL_LARGE_P(1, "1模组竖屏_大P"), MODULE_VERTICAL_SMALL_P(2, "1模组竖屏_小P"), MODULE_VERTICAL(3, "2模组_竖屏"),
		STANDARD_HORIZONTAL(4, "标准横屏_式样"), COLOR_SCREEN(5, "全彩屏_款式_式样jpg"), SMALL_VERTICAL(6, "小竖屏");

		private final int code;

		private final String desc;

		ScreenEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static ScreenEnum fromCode(int code) {
			for (ScreenEnum type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			return STANDARD_HORIZONTAL; // 默认返回标准横屏
		}

	}

	// 协议常量
	public static final byte[] HEADER = { (byte) 0xAA, 0x55 };

	public static final byte END_MARKER = (byte) 0xAF;

	public static final byte RESERVED = 0x00;

	public static final int DEFAULT_ADDRESS = 0x64; // 默认地址100

	// 命令集
	public static final byte CMD_QUERY_VERSION = 0x01;

	public static final byte CMD_SET_TIME = 0x10;

	public static final byte CMD_TRAFFIC_LIGHT_1 = 0x12;

	public static final byte CMD_TRAFFIC_LIGHT_2 = 0x13;

	public static final byte CMD_CANCEL_TEMP_DISPLAY = 0x21;

	public static final byte CMD_PLAY_VOICE = 0x22;

	public static final byte CMD_LOAD_ADS = 0x25;

	public static final byte CMD_LOAD_TEMP_DISPLAY = 0x27;

	public static final byte CMD_LOAD_QRCODE = 0x28;

	public static final byte CMD_CACHE_VOICE = 0x32;

	public static final byte CMD_SET_DND = 0x56;

	public static final byte CMD_SET_VOLUME = (byte) 0xF0;

	public static final byte CMD_SET_SPEED = (byte) 0xF1;

	public static final byte CMD_ENCRYPT_DECRYPT = (byte) 0xF2;

	public static final byte CMD_SUPER_CHANGE_ADDR = (byte) 0xF3;

	public static final byte CMD_CHANGE_ADDR = (byte) 0xF4;

	public static final byte CMD_ADJUST_POLARITY = (byte) 0xF5;

	public static final byte CMD_TIME_DISPLAY_MODE = (byte) 0xF6;

	public static final byte CMD_SET_COLOR_MODE = (byte) 0xF7;

	public static final byte CMD_SET_BAUD_RATE = (byte) 0xF8;

	public static final byte CMD_LOAD_ADS_COLOR = 0x35;

	public static final byte CMD_LOAD_TEMP_DISPLAY_COLOR = 0x37;

	public static final byte CMD_SET_ANIMATION = (byte) 0xF9;

	public static final byte CMD_SET_COMPATIBILITY_MODE = (byte) 0xFA;

	private int sequenceNumber = 0;

	private int address = DEFAULT_ADDRESS;

	private ScreenEnum screenType;

	public DisplayVoiceUtils() {
		this(ScreenEnum.STANDARD_HORIZONTAL); // 默认标准横屏
	}

	public DisplayVoiceUtils(ScreenEnum screenType) {
		this.screenType = screenType;
	}

	public DisplayVoiceUtils(int screenCode) {
		this(ScreenEnum.fromCode(screenCode));
	}

	public void setScreenType(ScreenEnum screenType) {
		this.screenType = screenType;
	}

	public void setScreenType(int screenCode) {
		this.screenType = ScreenEnum.fromCode(screenCode);
	}

	public ScreenEnum getScreenType() {
		return screenType;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	/**
	 * 判断是否为彩屏
	 */
	private boolean isColorScreen() {
		return screenType == ScreenEnum.COLOR_SCREEN;
	}

	/**
	 * 生成完整的数据包
	 */
	public byte[] buildPacket(byte command, byte[] data) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.append(HEADER);
		builder.append((byte) (sequenceNumber++ & 0xFF));
		builder.append((byte) (address & 0xFF));
		builder.append(RESERVED);
		builder.append(command);

		int dataLength = data != null ? data.length : 0;
		builder.append((byte) ((dataLength >> 8) & 0xFF));
		builder.append((byte) (dataLength & 0xFF));

		if (data != null && data.length > 0) {
			builder.append(data);
		}

		byte[] packetWithoutCrc = builder.toByteArray();
		byte[] crc = calculateCRC16(packetWithoutCrc);
		builder.append(crc);
		builder.append(END_MARKER);

		return builder.toByteArray();
	}

	/**
	 * CRC16计算（Modbus）
	 */
	public static byte[] calculateCRC16(byte[] data) {
		int crc = 0xFFFF;
		for (byte b : data) {
			crc ^= (b & 0xFF);
			for (int i = 0; i < 8; i++) {
				if ((crc & 0x0001) != 0) {
					crc = (crc >> 1) ^ 0xA001;
				}
				else {
					crc = crc >> 1;
				}
			}
		}
		return new byte[] { (byte) (crc & 0xFF), (byte) ((crc >> 8) & 0xFF) };
	}

	// ========== 通用指令（所有屏幕都支持） ==========

	public byte[] buildQueryVersionPacket() {
		return buildPacket(CMD_QUERY_VERSION, null);
	}

	public byte[] buildSetTimePacket(int year, int month, int day, int hour, int minute, int second) {
		byte[] data = { (byte) (year % 100), (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second };
		return buildPacket(CMD_SET_TIME, data);
	}

	public byte[] buildPlayVoicePacket(int voiceIndex) {
		return buildPacket(CMD_PLAY_VOICE, new byte[] { (byte) voiceIndex });
	}

	public byte[] buildSetVolumePacket(int volume) {
		volume = Math.max(0, Math.min(9, volume));
		return buildPacket(CMD_SET_VOLUME, new byte[] { (byte) volume });
	}

	// ========== 屏幕特定指令 ==========

	/**
	 * 加载广告内容（自动根据屏幕类型选择合适的方法）
	 */
	public byte[] buildLoadAdsPacket(int lineNumber, int color, String content) {
		if (isColorScreen()) {
			return buildLoadAdsColorPacket(lineNumber, color, content);
		}
		else {
			return buildLoadAdsStandardPacket(lineNumber, color, content);
		}
	}

	/**
	 * 标准屏加载广告内容
	 */
	private byte[] buildLoadAdsStandardPacket(int lineNumber, int color, String content) {
		validateLineNumber(lineNumber, 1, 4);

		// 标准屏颜色范围：1-3（红、绿、黄）
		if (color < 1 || color > 3) {
			color = 1; // 默认红色
		}

		byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
		ByteArrayBuilder data = new ByteArrayBuilder();
		data.append((byte) lineNumber);
		data.append((byte) color);
		data.append((byte) 0); // 保留
		data.append(contentBytes);

		return buildPacket(CMD_LOAD_ADS, data.toByteArray());
	}

	/**
	 * 彩屏加载广告内容
	 */
	private byte[] buildLoadAdsColorPacket(int lineNumber, int color, String content) {
		validateLineNumber(lineNumber, 1, 4);

		// 彩屏颜色范围：0-8（0=按字节随机，1-7=具体颜色，8=按行随机）
		if (color < 0 || color > 8) {
			color = 8; // 默认按行随机
		}

		byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
		ByteArrayBuilder data = new ByteArrayBuilder();
		data.append((byte) lineNumber);
		data.append((byte) color);
		data.append((byte) 0); // 保留
		data.append(contentBytes);

		return buildPacket(CMD_LOAD_ADS_COLOR, data.toByteArray());
	}

	/**
	 * 下发临显内容（自动根据屏幕类型选择合适的方法）
	 */
	public byte[] buildLoadTempDisplayPacket(int lineNumber, int duration, int color, String content) {
		if (isColorScreen()) {
			return buildLoadTempDisplayColorPacket(lineNumber, duration, color, content);
		}
		else {
			return buildLoadTempDisplayStandardPacket(lineNumber, duration, color, content);
		}
	}

	/**
	 * 标准屏下发临显内容
	 */
	private byte[] buildLoadTempDisplayStandardPacket(int lineNumber, int duration, int color, String content) {
		validateLineNumber(lineNumber, 1, 4);

		if (color < 1 || color > 3) {
			color = 1; // 默认红色
		}

		byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
		ByteArrayBuilder data = new ByteArrayBuilder();
		data.append((byte) lineNumber);
		data.append((byte) duration);
		data.append((byte) color);
		data.append((byte) 0); // 保留
		data.append(contentBytes);

		return buildPacket(CMD_LOAD_TEMP_DISPLAY, data.toByteArray());
	}

	/**
	 * 彩屏下发临显内容
	 */
	private byte[] buildLoadTempDisplayColorPacket(int lineNumber, int duration, int color, String content) {
		validateLineNumber(lineNumber, 1, 4);

		if (color < 0 || color > 8) {
			color = 8; // 默认按行随机
		}

		byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
		ByteArrayBuilder data = new ByteArrayBuilder();
		data.append((byte) lineNumber);
		data.append((byte) duration);
		data.append((byte) color);
		data.append((byte) 0); // 保留
		data.append(contentBytes);

		return buildPacket(CMD_LOAD_TEMP_DISPLAY_COLOR, data.toByteArray());
	}

	/**
	 * 彩屏专用：设置是否插播动画
	 */
	public byte[] buildSetAnimationPacket(boolean enableAnimation) {
		if (!isColorScreen()) {
			throw new UnsupportedOperationException("此指令仅支持全彩屏，当前屏幕类型: " + screenType.getDesc());
		}
		return buildPacket(CMD_SET_ANIMATION, new byte[] { (byte) (enableAnimation ? 1 : 0) });
	}

	/**
	 * 彩屏专用：设置兼容指令的显示方式
	 */
	public byte[] buildSetCompatibilityModePacket(boolean lineRandom) {
		if (!isColorScreen()) {
			throw new UnsupportedOperationException("此指令仅支持全彩屏，当前屏幕类型: " + screenType.getDesc());
		}
		return buildPacket(CMD_SET_COMPATIBILITY_MODE, new byte[] { (byte) (lineRandom ? 1 : 0) });
	}

	/**
	 * 验证行号有效性
	 */
	private void validateLineNumber(int lineNumber, int min, int max) {
		if (lineNumber < min || lineNumber > max) {
			throw new IllegalArgumentException(String.format("行号必须在%d-%d之间，当前: %d", min, max, lineNumber));
		}
	}

	// ========== 辅助类和方法 ==========

	private static class ByteArrayBuilder {

		private byte[] buffer = new byte[256];

		private int length = 0;

		public void append(byte b) {
			ensureCapacity(length + 1);
			buffer[length++] = b;
		}

		public void append(byte[] bytes) {
			ensureCapacity(length + bytes.length);
			System.arraycopy(bytes, 0, buffer, length, bytes.length);
			length += bytes.length;
		}

		private void ensureCapacity(int capacity) {
			if (capacity > buffer.length) {
				buffer = Arrays.copyOf(buffer, Math.max(buffer.length * 2, capacity));
			}
		}

		public byte[] toByteArray() {
			return Arrays.copyOf(buffer, length);
		}

	}

	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

	// ========== 使用示例 ==========

	public static void main(String[] args) {
		System.out.println("=== 不同屏幕类型指令生成示例 ===");

		// 示例1：使用枚举值创建实例
		DisplayVoiceUtils colorScreen = new DisplayVoiceUtils(ScreenEnum.COLOR_SCREEN);
		System.out.println("彩屏类型: " + colorScreen.getScreenType().getDesc());

		byte[] packet1 = colorScreen.buildLoadAdsPacket(1, 8, "欢迎光临");
		System.out.println("彩屏广告包: " + bytesToHex(packet1));

		// 示例2：使用数字代码创建实例
		DisplayVoiceUtils horizontalScreen = new DisplayVoiceUtils(4); // 标准横屏代码
		System.out.println("横屏类型: " + horizontalScreen.getScreenType().getDesc());

		byte[] packet2 = horizontalScreen.buildLoadAdsPacket(1, 1, "欢迎光临");
		System.out.println("横屏广告包: " + bytesToHex(packet2));

		// 示例3：动态切换屏幕类型
		DisplayVoiceUtils screen = new DisplayVoiceUtils();
		screen.setScreenType(1); // 设置为1模组竖屏_大P
		System.out.println("当前屏幕: " + screen.getScreenType().getDesc());

		byte[] packet3 = screen.buildLoadAdsPacket(1, 2, "竖屏内容"); // 绿色
		System.out.println("竖屏广告包: " + bytesToHex(packet3));

		// 示例4：彩屏专用指令
		try {
			byte[] animationPacket = colorScreen.buildSetAnimationPacket(true);
			System.out.println("彩屏动画设置: " + bytesToHex(animationPacket));
		}
		catch (Exception e) {
			System.out.println("错误: " + e.getMessage());
		}

		// 示例5：在非彩屏上尝试使用彩屏指令（会抛出异常）
		try {
			byte[] invalidPacket = horizontalScreen.buildSetAnimationPacket(true);
			System.out.println("横屏动画设置: " + bytesToHex(invalidPacket));
		}
		catch (Exception e) {
			System.out.println("预期错误: " + e.getMessage());
		}

		// 示例6：播放语音（所有屏幕通用）
		byte[] voicePacket = screen.buildPlayVoicePacket(1); // 播放"欢迎光临"
		System.out.println("语音包: " + bytesToHex(voicePacket));
	}

}