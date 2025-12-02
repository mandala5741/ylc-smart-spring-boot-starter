package com.cqcloud.platform.re485;

import com.cqcloud.platform.utils.UrlEncoderUtil;

/**
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕 2025年8月27日 🐬🐇 💓💕
 */
public class Rs485Util {

	// 帧头帧尾定义
	private static final String FRAME_HEADER = "AA55";

	private static final String FRAME_TAIL = "AF";

	// 设备地址（默认100=0x64）
	private static final int DEFAULT_ADDRESS = 0x64;

	// 命令类型定义
	// private static final String CMD_LOAD_AD = "25"; // 加载广告指令
	private static final String CMD_PLAY_VOICE = "22"; // 立即播报语音

	private static final String CMD_LOAD_TEMP_DISPLAY = "27"; // 下发临显指令

	private static final String CMD_CANCEL_TEMP_DISPLAY = "21"; // 取消临显指令

	// 颜色定义
	public static final int COLOR_RED = 1;

	public static final int COLOR_GREEN = 2;

	public static final int COLOR_YELLOW = 3;

	// 流水号计数器
	private static int serialNumber = 0x6C;

	/**
	 * 数字颜色定义
	 */
	public static final int DIGIT_COLOR_SAME = 0; // 数字颜色和控制字3一样

	public static final int DIGIT_COLOR_RED = 1; // 数字为红色

	public static final int DIGIT_COLOR_GREEN = 2; // 数字为绿色

	public static final int DIGIT_COLOR_YELLOW = 3; // 数字为黄色

	/**
	 * 常用语音索引常量（基于语音目录）
	 */
	public static final int VOICE_WELCOME = 0x01; // 欢迎光临

	public static final int VOICE_PLATE_NUMBER = 0x13; // 此车 (用于组合车牌)

	public static final int VOICE_SMOOTH_JOURNEY = 0x5F; // 一路顺风

	public static final int VOICE_AGAIN_WELCOME = 0x62; // 再次光临

	public static final int VOICE_PLEASE_ENTER = 0x14; // 请入场停车

	public static final int VOICE_PLEASE_PAY = 0x0B; // 请缴费

	public static final int VOICE_PARKING_FEE = 0x6A; // 缴费 (停车费)

	public static final int VOICE_CONSUMPTION = 0x44; // 扣款 (消费)

	public static final int VOICE_YUAN = 0x2D; // 金额 (元)

	public static final int VOICE_THIS_TIME = 0x16; // 本次

	/**
	 * 数字语音索引 (0-9)
	 */
	public static final int[] NUMBER_VOICE = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 // 0-9
	};

	/**
	 * 构建车牌号+一路平安，欢迎再次光临
	 * @param plateNumber 车牌号（用于选择对应的车牌语音组合）
	 * @return 语音命令
	 */
	public static String buildExitBlessingVoice(String plateNumber) {
		// 组合：车牌语音 + 一路顺风 + 欢迎再次光临
		int[] voiceIndexes = { getPlateVoiceIndex(plateNumber), // 根据车牌获取对应语音
				VOICE_SMOOTH_JOURNEY, // 一路顺风
				VOICE_WELCOME, // 欢迎
				VOICE_AGAIN_WELCOME // 再次光临
		};
		return buildMultiVoiceCommand(voiceIndexes);
	}

	/**
	 * 构建车牌号+欢迎光临，请入场停车
	 * @param plateNumber 车牌号
	 * @return 语音命令
	 */
	public static String buildEntryWelcomeVoice(String plateNumber) {
		// 组合：车牌语音 + 欢迎光临 + 请入场停车
		int[] voiceIndexes = { getPlateVoiceIndex(plateNumber), // 根据车牌获取对应语音
				VOICE_WELCOME, // 欢迎光临
				VOICE_PLEASE_ENTER // 请入场停车
		};
		return buildMultiVoiceCommand(voiceIndexes);
	}

	/**
	 * 构建本次消费xx元，欢迎再次光临
	 * @param amount 金额（数字）
	 * @return 语音命令
	 */
	public static String buildConsumptionVoice(int amount) {
		// 解析金额数字
		int[] amountDigits = parseAmount(amount);

		// 组合：本次 + 消费 + 金额数字 + 元 + 欢迎 + 再次光临
		int[] voiceIndexes = new int[6 + amountDigits.length];
		int index = 0;
		voiceIndexes[index++] = VOICE_THIS_TIME; // 本次
		voiceIndexes[index++] = VOICE_CONSUMPTION; // 消费

		// 添加金额数字
		for (int digit : amountDigits) {
			voiceIndexes[index++] = NUMBER_VOICE[digit];
		}

		voiceIndexes[index++] = VOICE_YUAN; // 元
		voiceIndexes[index++] = VOICE_WELCOME; // 欢迎
		voiceIndexes[index++] = VOICE_AGAIN_WELCOME; // 再次光临

		return buildMultiVoiceCommand(voiceIndexes);
	}

	/**
	 * 构建请支付停车费xx元
	 * @param amount 金额（数字）
	 * @return 语音命令
	 */
	public static String buildPaymentVoice(int amount) {
		// 解析金额数字
		int[] amountDigits = parseAmount(amount);

		// 组合：请缴费 + 停车费 + 金额数字 + 元
		int[] voiceIndexes = new int[3 + amountDigits.length];
		int index = 0;
		voiceIndexes[index++] = VOICE_PLEASE_PAY; // 请缴费
		voiceIndexes[index++] = VOICE_PARKING_FEE; // 停车费

		// 添加金额数字
		for (int digit : amountDigits) {
			voiceIndexes[index++] = NUMBER_VOICE[digit];
		}

		voiceIndexes[index++] = VOICE_YUAN; // 元

		return buildMultiVoiceCommand(voiceIndexes);
	}

	/**
	 * 根据车牌号获取对应的语音索引 注意：标准语音中车牌需要预定义，这里使用"此车"作为通用车牌语音
	 */
	private static int getPlateVoiceIndex(String plateNumber) {
		// 在实际系统中，可能需要根据车牌映射到具体的语音索引
		// 这里返回通用的"此车"语音
		return VOICE_PLATE_NUMBER; // "此车"
	}

	/**
	 * 解析金额数字为单个数字数组
	 */
	private static int[] parseAmount(int amount) {
		String amountStr = String.valueOf(amount);
		int[] digits = new int[amountStr.length()];
		for (int i = 0; i < amountStr.length(); i++) {
			digits[i] = Character.getNumericValue(amountStr.charAt(i));
		}
		return digits;
	}

	/**
	 * 解析金额字符串（支持小数）
	 */
	public static int[] parseAmount(String amount) {
		// 去除小数点，只取整数部分
		String cleanAmount = amount.split("\\.")[0];
		return parseAmount(Integer.parseInt(cleanAmount));
	}

	/**
	 * 获取下一个流水号
	 */
	private static String getNextSerialNumber() {
		String serial = String.format("%02X", serialNumber);
		serialNumber = (serialNumber + 1) & 0xFF; // 循环0-255
		return serial;
	}

	/**
	 * 构建完整的数据帧（基于实际通信格式）
	 */
	public static String buildFrame(String command, String data) {
		String serial = getNextSerialNumber();
		String address = String.format("%02X", DEFAULT_ADDRESS);
		String reserved = "00"; // 业务类型/保留

		// 计算数据长度（字节数）
		int dataLength = data.length() / 2;
		String lengthHex = String.format("%04X", dataLength);

		// 构建数据部分
		String frameData = serial + address + reserved + command + lengthHex + data;

		// 计算CRC（CRC位置用00 00填充）
		String dataForCRC = frameData + "0000";
		byte[] dataBytes = hexStringToByteArray(dataForCRC);
		int crc = usMBCRC16(dataBytes, dataBytes.length);
		String crcHex = String.format("%04X", crc);

		// 构建完整命令
		return FRAME_HEADER + frameData + crcHex + FRAME_TAIL;
	}

	// ==================== 显示控制方法 ====================

	/**
	 * 构建下发临显内容指令 (0x27) - 临时显示内容，可设置时长 协议格式：控制字1(行号) | 控制字2(时长) | 控制字3(颜色) | 控制字4(保留) |
	 * 内容
	 * @param line 行号 (1-4, 5-7为虚拟行) 显示时长 (秒, 0=长期显示)
	 * @param color 显示颜色 (1=红, 2=绿, 3=黄)
	 * @param content 显示内容
	 */
	public static String buildLoadAdCommand(int line, int color, String content) {
		String hexContent = stringToHex(content);
		String controlData = String.format("%02X%02X00", line, color) + hexContent;
		return buildFrame(CMD_LOAD_TEMP_DISPLAY, controlData);
	}

	/**
	 * 构建第一行显示命令 - "欢迎光临"
	 */
	public static String buildWelcomeLine() {
		return buildLoadAdCommand(1, COLOR_RED, "欢迎光临");
	}

	/**
	 * 构建第二行显示命令 - "渝A12345"
	 */
	public static String buildPlateNumberLine(String plateNumber) {
		return buildLoadAdCommand(2, COLOR_RED, plateNumber);
	}

	/**
	 * 构建第三行显示命令 - "一车一杆自动识别"
	 */
	public static String buildAutoRecognitionLine() {
		return buildLoadAdCommand(3, COLOR_RED, "一车一杆自动识别");
	}

	/**
	 * 构建第四行显示命令 - "减速慢行"
	 */
	public static String buildSlowDownLine() {
		return buildLoadAdCommand(4, COLOR_RED, "减速慢行");
	}

	// ==================== 语音控制方法 ====================

	/**
	 * 构建立即播报语音指令 (0x22)
	 * @param voiceContent 语音内容组合
	 */
	public static String buildPlayVoiceCommand(String voiceContent) {
		System.out.println("调用前语音:" + voiceContent);
		String hexContent = stringToHex(voiceContent);
		return buildFrame(CMD_PLAY_VOICE, hexContent);
	}

	/**
	 * 构建车牌语音播报 - "渝A12345，欢迎光临"
	 */
	public static String buildPlateVoiceCommand(String plateNumber) {
		// 组合语音：文字"渝A12345" + 语音索引01("欢迎光临")
		return buildPlayVoiceCommand(plateNumber + "\u0001"); // 使用0x01作为语音索引
	}

	/**
	 * 构建语音播报命令（支持多个语音索引）
	 * @param voiceIndexes 语音索引数组
	 */
	public static String buildMultiVoiceCommand(int[] voiceIndexes) {
		StringBuilder data = new StringBuilder();
		for (int index : voiceIndexes) {
			data.append(String.format("%02X", index));
		}
		return buildFrame(CMD_PLAY_VOICE, data.toString());
	}

	// ==================== 临时显示控制方法 ====================

	/**
	 * 构建下发临显内容指令 (0x27)
	 * @param line 行号 (1-4, 5-7为虚拟行)
	 * @param duration 显示时长 (秒, 0=长期显示)
	 * @param color 显示颜色 (1=红, 2=绿, 3=黄)
	 * @param content 显示内容
	 */
	public static String buildLoadTempDisplayCommand(int line, int duration, int color, String content) {
		String hexContent = stringToHex(content);
		String controlData = String.format("%02X%02X%02X00", line, duration, color) + hexContent;
		return buildFrame(CMD_LOAD_TEMP_DISPLAY, controlData);
	}

	/**
	 * 构建剩余车位显示命令
	 * @param count 车位数
	 * @param duration 显示时长 (秒)
	 * @param color 颜色 (1=红, 2=绿, 3=黄)
	 */
	public static String buildParkingSpaceCommand(int count, int duration, int color) {
		String content = "剩余车位" + String.format("%03d", count);
		return buildLoadTempDisplayCommand(6, duration, color, content); // 行号6为剩余车位虚拟行
	}

	/**
	 * 构建加载广告内容指令 (0x25) - 固定广告内容，不滑动显示 协议格式：控制字1(行号) | 控制字2(颜色) | 控制字3(保留) | 内容
	 * @param line 行号 (1-4)
	 * @param color 显示颜色 (1=红, 2=绿, 3=黄)
	 * @param content 显示内容
	 */
	public static String buildLoadAdFixedDisplayCommand(int line, int color, String content) {
		String hexContent = stringToHex(content);
		String controlData = String.format("%02X%02X00", line, color) + hexContent;
		return buildFrame(CMD_LOAD_TEMP_DISPLAY, controlData);
	}

	/**
	 * 构建取消临显指令 (0x21)
	 * @param lineMask 行掩码 (按位操作: bit0=第1行, bit1=第2行, bit2=第3行, bit3=第4行)
	 */
	public static String buildCancelTempDisplayCommand(int lineMask) {
		String data = String.format("%02X", lineMask & 0x0F);
		return buildFrame(CMD_CANCEL_TEMP_DISPLAY, data);
	}

	/**
	 * 构建取消所有临显指令
	 */
	public static String buildCancelAllTempDisplayCommand() {
		return buildCancelTempDisplayCommand(0x0F); // 取消所有1-4行
	}

	// ==================== 完整场景方法 ====================

	/**
	 * 构建完整的入场显示场景
	 */
	public static String[] buildEntryScene(String plateNumber, int parkingSpace) {
		return new String[] { buildLoadAdCommand(1, COLOR_RED, "欢迎光临"), buildLoadAdCommand(2, COLOR_RED, plateNumber),
				buildLoadAdCommand(3, COLOR_RED, "一车一杆自动识别"), buildLoadAdCommand(4, COLOR_RED, "减速慢行"),
				buildParkingSpaceCommand(parkingSpace, 60, COLOR_GREEN), buildPlayVoiceCommand(plateNumber + "\u0001") // 车牌
																														// +
																														// 欢迎光临语音
		};
	}

	/**
	 * 构建完整的出场显示场景
	 */
	public static String[] buildExitScene(String plateNumber, String amount) {
		return new String[] { buildLoadAdCommand(1, COLOR_RED, "一路平安"), buildLoadAdCommand(2, COLOR_RED, plateNumber),
				buildLoadAdCommand(3, COLOR_RED, "请交费" + amount + "元"), buildLoadAdCommand(4, COLOR_RED, "谢谢光临"),
				buildPlayVoiceCommand(plateNumber + "\u0002") // 车牌 + 一路平安语音
		};
	}

	// ==================== 工具方法 ====================

	/**
	 * 字符串转HEX (修复中英文混合编码问题)
	 */
	public static String stringToHex(String str) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try {
			byte[] bytes = str.getBytes("GBK"); // 使用GBK编码处理中文
			for (byte b : bytes) {
				sb.append(String.format("%02X", b & 0xFF));
			}
		}
		catch (java.io.UnsupportedEncodingException e) {
			// 如果GBK不支持，使用默认编码
			for (char c : str.toCharArray()) {
				sb.append(String.format("%02X", (int) c));
			}
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转字节数组
	 */
	public static byte[] hexStringToByteArray(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0) {
			throw new IllegalArgumentException("Invalid hex string");
		}

		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * CRC16校验算法（保持原有实现）
	 */
	public static int usMBCRC16(byte[] pucFrame, int usLen) {
		// 原有的CRC16实现代码
		byte ucCRCHi = (byte) 0xFF;
		byte ucCRCLo = (byte) 0xFF;
		int iIndex;
		int i = 0;

		while (usLen-- > 0) {
			iIndex = (ucCRCLo ^ pucFrame[i++]) & 0xFF;
			ucCRCLo = (byte) (ucCRCHi ^ aucCRCHi[iIndex]);
			ucCRCHi = aucCRCLo[iIndex];
		}
		return ((ucCRCHi & 0xFF) << 8) | (ucCRCLo & 0xFF);
	}

	// CRC16查找表
	private static final byte[] aucCRCHi = { 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
			0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80,
			0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0,
			(byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00,
			(byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
			0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81,
			0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1,
			(byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01,
			(byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
			0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80,
			0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1,
			(byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01,
			(byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
			0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80,
			0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1,
			(byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00,
			(byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
			0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81,
			0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1,
			(byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00,
			(byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
			0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80,
			0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0,
			(byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40 };

	private static final byte[] aucCRCLo = { 0x00, (byte) 0xC0, (byte) 0xC1, 0x01, (byte) 0xC3, 0x03, 0x02, (byte) 0xC2,
			(byte) 0xC6, 0x06, 0x07, (byte) 0xC7, 0x05, (byte) 0xC5, (byte) 0xC4, 0x04, (byte) 0xCC, 0x0C, 0x0D,
			(byte) 0xCD, 0x0F, (byte) 0xCF, (byte) 0xCE, 0x0E, 0x0A, (byte) 0xCA, (byte) 0xCB, 0x0B, (byte) 0xC9, 0x09,
			0x08, (byte) 0xC8, (byte) 0xD8, 0x18, 0x19, (byte) 0xD9, 0x1B, (byte) 0xDB, (byte) 0xDA, 0x1A, 0x1E,
			(byte) 0xDE, (byte) 0xDF, 0x1F, (byte) 0xDD, 0x1D, 0x1C, (byte) 0xDC, 0x14, (byte) 0xD4, (byte) 0xD5, 0x15,
			(byte) 0xD7, 0x17, 0x16, (byte) 0xD6, (byte) 0xD2, 0x12, 0x13, (byte) 0xD3, 0x11, (byte) 0xD1, (byte) 0xD0,
			0x10, (byte) 0xF0, 0x30, 0x31, (byte) 0xF1, 0x33, (byte) 0xF3, (byte) 0xF2, 0x32, 0x36, (byte) 0xF6,
			(byte) 0xF7, 0x37, (byte) 0xF5, 0x35, 0x34, (byte) 0xF4, 0x3C, (byte) 0xFC, (byte) 0xFD, 0x3D, (byte) 0xFF,
			0x3F, 0x3E, (byte) 0xFE, (byte) 0xFA, 0x3A, 0x3B, (byte) 0xFB, 0x39, (byte) 0xF9, (byte) 0xF8, 0x38, 0x28,
			(byte) 0xE8, (byte) 0xE9, 0x29, (byte) 0xEB, 0x2B, 0x2A, (byte) 0xEA, (byte) 0xEE, 0x2E, 0x2F, (byte) 0xEF,
			0x2D, (byte) 0xED, (byte) 0xEC, 0x2C, (byte) 0xE4, 0x24, 0x25, (byte) 0xE5, 0x27, (byte) 0xE7, (byte) 0xE6,
			0x26, 0x22, (byte) 0xE2, (byte) 0xE3, 0x23, (byte) 0xE1, 0x21, 0x20, (byte) 0xE0, (byte) 0xA0, 0x60, 0x61,
			(byte) 0xA1, 0x63, (byte) 0xA3, (byte) 0xA2, 0x62, 0x66, (byte) 0xA6, (byte) 0xA7, 0x67, (byte) 0xA5, 0x65,
			0x64, (byte) 0xA4, 0x6C, (byte) 0xAC, (byte) 0xAD, 0x6D, (byte) 0xAF, 0x6F, 0x6E, (byte) 0xAE, (byte) 0xAA,
			0x6A, 0x6B, (byte) 0xAB, 0x69, (byte) 0xA9, (byte) 0xA8, 0x68, 0x78, (byte) 0xB8, (byte) 0xB9, 0x79,
			(byte) 0xBB, 0x7B, 0x7A, (byte) 0xBA, (byte) 0xBE, 0x7E, 0x7F, (byte) 0xBF, 0x7D, (byte) 0xBD, (byte) 0xBC,
			0x7C, (byte) 0xB4, 0x74, 0x75, (byte) 0xB5, 0x77, (byte) 0xB7, (byte) 0xB6, 0x76, 0x72, (byte) 0xB2,
			(byte) 0xB3, 0x73, (byte) 0xB1, 0x71, 0x70, (byte) 0xB0, 0x50, (byte) 0x90, (byte) 0x91, 0x51, (byte) 0x93,
			0x53, 0x52, (byte) 0x92, (byte) 0x96, 0x56, 0x57, (byte) 0x97, 0x55, (byte) 0x95, (byte) 0x94, 0x54,
			(byte) 0x9C, 0x5C, 0x5D, (byte) 0x9D, 0x5F, (byte) 0x9F, (byte) 0x9E, 0x5E, 0x5A, (byte) 0x9A, (byte) 0x9B,
			0x5B, (byte) 0x99, 0x59, 0x58, (byte) 0x98, (byte) 0x88, 0x48, 0x49, (byte) 0x89, 0x4B, (byte) 0x8B,
			(byte) 0x8A, 0x4A, 0x4E, (byte) 0x8E, (byte) 0x8F, 0x4F, (byte) 0x8D, 0x4D, 0x4C, (byte) 0x8C, 0x44,
			(byte) 0x84, (byte) 0x85, 0x45, (byte) 0x87, 0x47, 0x46, (byte) 0x86, (byte) 0x82, 0x42, 0x43, (byte) 0x83,
			0x41, (byte) 0x81, (byte) 0x80, 0x40 };

	// ==================== 二维码控制方法 ====================

	/**
	 * 二维码显示模式
	 */
	public static final int QRCODE_MODE_CENTER = 0; // 两行居中模式

	public static final int QRCODE_MODE_LEFT = 1; // 两行居左带文字

	public static final int QRCODE_MODE_THREE_LINE = 2; // 三行模式

	/**
	 * 二维码颜色
	 */
	public static final int QRCODE_COLOR_RED = 1; // 红色

	public static final int QRCODE_COLOR_GREEN = 2; // 绿色

	public static final int QRCODE_COLOR_YELLOW = 3; // 黄色

	/**
	 * 构建二维码显示指令 (0x28)
	 * @param mode 显示模式 (0=居中, 1=居左带文字, 2=三行)
	 * @param duration 显示时长 (秒, 0=长期显示)
	 * @param color 二维码颜色 (1=红, 2=绿, 3=黄)
	 * @param content 二维码内容
	 * @return 完整的二维码命令
	 */
	public static String buildQrcodeCommand(int mode, int duration, int color, String content) {
		String hexContent = stringToHex(UrlEncoderUtil.encodeUrlParams(content));
		String controlData = String.format("%02X%02X%02X", mode, duration, color) + hexContent;
		return buildFrame("28", controlData);
	}

	/**
	 * 构建长期显示的二维码（绿色）
	 * @param content 二维码内容
	 * @return 二维码命令
	 */
	public static String buildQrcodeCommand(String content) {
		return buildQrcodeCommand(QRCODE_MODE_THREE_LINE, 0, QRCODE_COLOR_GREEN, content);
	}

	/**
	 * 构建与示例完全相同的百度二维码命令
	 */
	public static String buildBaiduQrcodeCommand() {
		return buildQrcodeCommand(QRCODE_MODE_THREE_LINE, 0, QRCODE_COLOR_GREEN, "https://www.baidu.com");
	}

	/**
	 * 构建支付二维码
	 * @param payUrl 支付链接
	 * @param duration 显示时长(秒)
	 * @return 二维码命令
	 */
	public static String buildPaymentQrcode(String payUrl, int duration) {
		return buildQrcodeCommand(QRCODE_MODE_THREE_LINE, duration, QRCODE_COLOR_GREEN, payUrl);
	}

	/**
	 * 构建网址二维码
	 * @param url 网址
	 * @param duration 显示时长(秒)
	 * @return 二维码命令
	 */
	public static String buildUrlQrcode(String url, int duration) {
		return buildQrcodeCommand(QRCODE_MODE_THREE_LINE, duration, QRCODE_COLOR_GREEN, url);
	}

	/**
	 * 构建临时显示的二维码（带时长）
	 * @param content 二维码内容
	 * @param duration 显示时长(秒)
	 * @return 二维码命令
	 */
	public static String buildTempQrcode(String content, int duration) {
		return buildQrcodeCommand(QRCODE_MODE_THREE_LINE, duration, QRCODE_COLOR_GREEN, content);
	}

	// ==================== 固定显示控制方法 ====================

	/**
	 * 构建固定显示的四行内容（不滑动显示） 适用于需要定屏显示的场景
	 * @param line1 第一行内容
	 * @param line2 第二行内容
	 * @param line3 第三行内容
	 * @param line4 第四行内容
	 * @param color 显示颜色 (1=红, 2=绿, 3=黄)
	 * @return 四行固定显示命令数组
	 */
	public static String[] buildFixedDisplay(String line1, String line2, String line3, String line4, int color) {
		return new String[] { buildLoadAdFixedDisplayCommand(1, color, line1),
				buildLoadAdFixedDisplayCommand(2, color, line2), buildLoadAdFixedDisplayCommand(3, color, line3),
				buildLoadAdFixedDisplayCommand(4, color, line4) };
	}

	/**
	 * 构建红色固定显示的四行内容
	 */
	public static String[] buildFixedDisplay(String line1, String line2, String line3, String line4) {
		return buildFixedDisplay(line1, line2, line3, line4, QRCODE_COLOR_GREEN);
	}

	/**
	 * 构建默认的固定欢迎显示
	 */
	public static String[] buildFixedWelcomeDisplay() {
		return buildFixedDisplay("欢迎光临", "恭喜发财万事如意", "一车一杆自动识别", "减速慢行", QRCODE_COLOR_GREEN);
	}

	/**
	 * 构建停车场固定信息显示
	 */
	public static String[] buildFixedParkingInfoDisplay(int availableSpaces, int totalSpaces) {
		return buildFixedDisplay("停车场信息", "总车位:" + totalSpaces + " 剩余:" + availableSpaces, "请有序停车", "谢谢配合",
				COLOR_GREEN);
	}

	/**
	 * 构建支付成功固定显示
	 */
	public static String[] buildFixedPaymentSuccessDisplay(String plateNumber, String amount) {
		return buildFixedDisplay("支付成功", "车牌:" + plateNumber, "金额:" + amount + "元", "一路平安", COLOR_GREEN);
	}

	/**
	 * 构建系统维护固定显示
	 */
	public static String[] buildFixedMaintenanceDisplay() {
		return buildFixedDisplay("系统维护中", "请稍候", "给您带来不便", "敬请谅解", COLOR_YELLOW);
	}

	/**
	 * 构建自定义固定显示场景
	 */
	public static String[] buildCustomFixedDisplay(String[] lines, int color) {
		if (lines.length != 4) {
			throw new IllegalArgumentException("必须提供4行显示内容");
		}
		return buildFixedDisplay(lines[0], lines[1], lines[2], lines[3], color);
	}

	// ==================== TTS万能语音控制方法 ====================

	/**
	 * TTS命令头
	 */
	private static final String TTS_HEADER = "FD00";

	/**
	 * 构建TTS万能语音播报指令
	 * @param text 要播报的文本内容
	 * @return TTS语音命令
	 */
	public static String buildTTSVoiceCommand(String text) {
		// 构建内容："TTS" + 文本内容
		String content = "TTS" + text;
		String hexContent = stringToHex(content);

		// 计算数据长度（内容字节数 + 3个固定参数字节）
		int dataLength = hexContent.length() / 2 + 3;
		String lengthHex = String.format("%02X", dataLength);

		// 构建完整数据：长度 + 01 + 01 + 内容
		String data = lengthHex + "0101" + hexContent;

		return TTS_HEADER + data;
	}

	/**
	 * 构建入场欢迎语音（支持车牌号参数）
	 * @param plateNumber 车牌号
	 * @return 入场欢迎语音命令
	 */
	public static String buildEntryWelcomeTTS(String plateNumber) {
		String text = plateNumber + "欢迎光临，请入场停车";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建出场祝福语音（支持车牌号参数）
	 * @param plateNumber 车牌号
	 * @return 出场祝福语音命令
	 */
	public static String buildExitBlessingTTS(String plateNumber) {
		String text = plateNumber + "一路平安，欢迎再次光临";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建消费提醒语音（支持金额参数）
	 * @param amount 消费金额
	 * @return 消费提醒语音命令
	 */
	public static String buildConsumptionTTS(String amount) {
		String text = "本次消费" + amount + "元，欢迎再次光临";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建支付提醒语音（支持金额参数）
	 * @param amount 支付金额
	 * @return 支付提醒语音命令
	 */
	public static String buildPaymentReminderTTS(String amount) {
		String text = "请支付停车费" + amount + "元";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建完整支付场景语音（支持车牌号和金额参数）
	 * @param plateNumber 车牌号
	 * @param amount 支付金额
	 * @return 支付场景语音命令
	 */
	public static String buildPaymentSceneTTS(String plateNumber, String amount) {
		String text = plateNumber + "请支付停车费" + amount + "元";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建车位提醒语音（支持车位数参数）
	 * @param availableSpaces 剩余车位数
	 * @return 车位提醒语音命令
	 */
	public static String buildParkingSpaceTTS(int availableSpaces) {
		String text = "剩余车位" + availableSpaces + "个，请合理安排";
		return buildTTSVoiceCommand(text);
	}

	/**
	 * 构建自定义模板语音（支持多个参数）
	 * @param template 模板字符串，使用{}作为占位符
	 * @param params 参数值
	 * @return 自定义语音命令
	 */
	public static String buildTemplateTTS(String template, String... params) {
		String result = template;
		for (int i = 0; i < params.length; i++) {
			result = result.replace("{" + i + "}", params[i]);
		}
		return buildTTSVoiceCommand(result);
	}

	/**
	 * 构建通用语音播报（支持格式化字符串）
	 * @param format 格式化字符串
	 * @param args 参数
	 * @return 语音命令
	 */
	public static String buildFormattedTTS(String format, Object... args) {
		String text = String.format(format, args);
		return buildTTSVoiceCommand(text);
	}

	// ==================== 测试方法 ====================

	public static void main(String[] args) {

		String plateNumber = "渝A12345";
		// 1. 车牌号+一路平安，欢迎再次光临
		String exitVoice = buildExitBlessingVoice(plateNumber);
		System.out.println("出场祝福语音: " + exitVoice);
		// 播报："此车一路顺风欢迎再次光临"

		// 2. 车牌号+欢迎光临，请入场停车
		String entryVoice = buildEntryWelcomeVoice(plateNumber);
		System.out.println("入场欢迎语音: " + entryVoice);
		// 播报："此车欢迎光临请入场停车"

		// 3. 本次消费10元，欢迎再次光临
		String consumptionVoice = buildConsumptionVoice(10);
		System.out.println("消费提醒语音: " + consumptionVoice);
		// 播报："本次消费10元欢迎再次光临"

		// 4. 请支付停车费15元
		String paymentVoice = buildPaymentVoice(15);
		System.out.println("支付提醒语音: " + paymentVoice);
		// 播报："请缴费停车费15元"

		// 5. 大金额测试
		String bigPayment = buildPaymentVoice(125);
		System.out.println("大金额支付: " + bigPayment);
		// 播报："请缴费停车费125元"

		// 3. 生成临时支付二维码（显示60秒）
		// String paymentQrcode =
		// Rs485Util.buildPaymentQrcode("https://tcc.tlgdiot.cn/wechat/pages/pay/index?carlicense="+"渝A12345",
		// 60);
		// System.out.println("支付二维码: " + paymentQrcode);
		/**
		 * // 1. 默认固定欢迎显示 String[] welcomeDisplay = buildFixedWelcomeDisplay();
		 * System.out.println("固定欢迎显示:"); for (String cmd : welcomeDisplay) {
		 * System.out.println(cmd); }
		 **/
		/**
		 * // 4. 生成临时网址二维码（显示30秒） String tempUrlQrcode =
		 * Rs485Util.buildTempQrcode("https://www.example.com", 30);
		 * System.out.println("临时网址二维码: " + tempUrlQrcode);
		 **/

		/**
		 * String plateNumber = "渝A12345"; // 测试生成与日志中相同的数据 String line1 =
		 * buildWelcomeLine(); String line2 = buildPlateNumberLine(plateNumber); String
		 * line3 = buildAutoRecognitionLine(); String line4 = buildSlowDownLine(); String
		 * voice = buildPlateVoiceCommand(plateNumber);
		 *
		 * System.out.println("第一行: " + line1); System.out.println("第二行: " + line2);
		 * System.out.println("第三行: " + line3); System.out.println("第四行: " + line4);
		 * System.out.println("语音: " + voice);
		 *
		 * // 测试完整场景 String[] entryScene = buildEntryScene(plateNumber, 56);
		 * System.out.println("\n入场场景:"); for (String cmd : entryScene) {
		 * System.out.println(cmd); } String[] entryScenes = buildExitScene(plateNumber,
		 * "10"); System.out.println("\n出场场景:"); for (String cmd : entryScenes) {
		 * System.out.println(cmd); }
		 *
		 * String cancelCmd = Rs485Util.buildCancelAllTempDisplayCommand();
		 * System.out.println("\n取消:"+cancelCmd);
		 **/
	}

}