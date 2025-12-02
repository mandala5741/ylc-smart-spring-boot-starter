package com.cqcloud.platform.utils;

import java.nio.charset.StandardCharsets;

/**
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕 2025年8月27日 🐬🐇 💓💕
 */
public class Rs485DisplayUtils {

	// 帧头帧尾定义
	private static final String FRAME_HEADER = "AA55";

	private static final String FRAME_TAIL = "AF";

	// 设备地址（默认100=0x64）
	private static final int DEFAULT_ADDRESS = 0x64;

	// 命令类型定义
	private static final String CMD_LOAD_TEMP_DISPLAY = "27"; // 下发临显指令

	private static final String CMD_CANCEL_TEMP_DISPLAY = "21"; // 取消临显指令

	private static final String CMD_PLAY_VOICE = "22"; // 立即播报语音

	// 颜色定义
	public static final int COLOR_RED = 1;

	public static final int COLOR_GREEN = 2;

	public static final int COLOR_YELLOW = 3;

	// 流水号计数器
	private static int serialNumber = 0x20;

	// ==================== 语音控制方法 ====================

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

	public static final int VOICE_THANK_YOU = 0x03; // 谢谢

	public static final int VOICE_PLEASE_WAIT = 0x0C; // 请稍候

	public static final int VOICE_PAY_SUCCESS = 0x45; // 缴费成功

	/**
	 * 数字语音索引 (0-9)
	 */
	public static final int[] NUMBER_VOICE = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39 // 0-9
	};

	/**
	 * 构建单条语音播报指令
	 * @param voiceIndex 语音索引
	 * @return 完整的语音命令
	 */
	public static String buildSingleVoiceCommand(int voiceIndex) {
		String data = String.format("%02X", voiceIndex);
		return buildFrame(CMD_PLAY_VOICE, data);
	}

	/**
	 * 构建"请缴费"语音指令 对应: AA550164002200010B0776AF
	 */
	public static String buildPleasePayVoice() {
		return buildSingleVoiceCommand(VOICE_PLEASE_PAY);
	}

	/**
	 * 构建车牌号+欢迎光临语音 - 使用字符索引 示例：渝A12345欢迎光临
	 */
	public static String buildPlateWelcomeVoice(String plateNumber) {
		return buildPlateWithVoiceCommand(plateNumber, '\u0001'); // 车牌 + 欢迎光临
	}

	/**
	 * 构建车牌号+请入场停车语音 - 使用字符索引 示例：渝A12345请入场停车
	 */
	public static String buildPlateEnterVoice(String plateNumber) {
		return buildPlateWithVoiceCommand(plateNumber, '\u0014'); // 车牌 + 请入场停车
	}

	/**
	 * 构建车牌号+请缴费语音 - 使用字符索引 示例：渝A12345请缴费
	 */
	public static String buildPlatePaymentVoice(String plateNumber) {
		return buildPlateWithVoiceCommand(plateNumber, '\u000B'); // 车牌 + 请缴费
	}

	/**
	 * 构建车牌号+一路平安语音 - 使用字符索引 示例：渝A12345一路平安
	 */
	public static String buildPlateSmoothJourneyVoice(String plateNumber) {
		return buildPlateWithVoiceCommand(plateNumber, '\u005F'); // 车牌 + 一路平安
	}

	/**
	 * 构建车牌号+再次光临语音 - 使用字符索引 示例：渝A12345再次光临
	 */
	public static String buildPlateAgainWelcomeVoice(String plateNumber) {
		return buildPlateWithVoiceCommand(plateNumber, '\u0062'); // 车牌 + 再次光临
	}

	/**
	 * 构建车牌号+语音播报指令 - 支持字符索引
	 */
	public static String buildPlateWithVoiceCommand(String plateNumber, char voiceChar) {
		String plateHex = stringToHex(plateNumber);
		String voiceHex = String.format("%02X", (int) voiceChar);
		String data = plateHex + voiceHex;
		return buildFrame("22", data);
	}

	/**
	 * 构建车牌号+语音播报指令 - 支持整数索引
	 */
	public static String buildPlateWithVoiceCommand(String plateNumber, int voiceIndex) {
		String plateHex = stringToHex(plateNumber);
		String voiceHex = String.format("%02X", voiceIndex);
		String data = plateHex + voiceHex;
		return buildFrame("22", data);
	}
	// ==================== 组合语音方法（使用字符索引）====================

	/**
	 * 构建入场完整语音 - "欢迎光临，请入场停车" - 使用字符索引
	 */
	public static String buildEntryCompleteVoice() {
		return buildMultiVoiceCommand(new int[] { 0x01, 0x14 }); // 欢迎光临 + 请入场停车
	}

	/**
	 * 构建出场祝福语音 - "一路顺风，欢迎再次光临" - 使用字符索引
	 */
	public static String buildExitBlessingVoice() {
		return buildMultiVoiceCommand(new int[] { 0x5F, 0x62 }); // 一路顺风 + 再次光临
	}

	/**
	 * 构建多条语音组合播报指令
	 * @param voiceIndexes 语音索引数组
	 * @return 完整的语音命令
	 */
	public static String buildMultiVoiceCommand(int[] voiceIndexes) {
		StringBuilder data = new StringBuilder();
		for (int index : voiceIndexes) {
			data.append(String.format("%02X", index));
		}
		return buildFrame(CMD_PLAY_VOICE, data.toString());
	}

	// ==================== 常用语音场景 ====================

	/**
	 * 构建入场完整语音场景
	 */
	public static String buildEntryVoiceScene(String plateNumber) {
		return buildEntryWelcomeVoice(plateNumber);
	}

	/**
	 * 构建出场完整语音场景
	 */
	public static String buildExitVoiceScene(String plateNumber, int amount) {
		if (amount > 0) {
			return buildPaymentVoice(amount); // 需要缴费
		}
		else {
			return buildExitBlessingVoice(plateNumber); // 免费出场
		}
	}

	/**
	 * 构建支付提醒语音场景
	 */
	public static String buildPaymentReminderScene(int amount) {
		return buildPaymentVoice(amount);
	}

	/**
	 * 构建支付成功语音场景
	 */
	public static String buildPaymentSuccessScene(String plateNumber) {
		return buildPaySuccessVoice(plateNumber);
	}

	/**
	 * 构建车牌号+欢迎光临，请入场停车
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
	 * 构建车牌号+一路平安，欢迎再次光临
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
	 * 构建本次消费xx元，欢迎再次光临
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
	 * 构建缴费成功语音
	 */
	public static String buildPaySuccessVoice(String plateNumber) {
		// 组合：车牌语音 + 缴费成功 + 谢谢
		int[] voiceIndexes = { getPlateVoiceIndex(plateNumber), // 车牌语音
				VOICE_PAY_SUCCESS, // 缴费成功
				VOICE_THANK_YOU // 谢谢
		};
		return buildMultiVoiceCommand(voiceIndexes);
	}

	/**
	 * 根据车牌号获取对应的语音索引
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
	 * 获取下一个流水号
	 */
	private static String getNextSerialNumber() {
		String serial = String.format("%02X", serialNumber);
		serialNumber = (serialNumber + 1) & 0xFF; // 循环0-255
		return serial;
	}

	/**
	 * 构建完整的数据帧
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

	/**
	 * 构建下发临显内容指令 (0x27) - 临时显示内容 协议格式：控制字1(行号) | 控制字2(时长) | 控制字3(颜色) | 控制字4(保留) | 内容
	 * @param line 行号 (1-4)
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
	 * 构建长期显示的临显指令（简化版）
	 */
	public static String buildLoadTempDisplayCommand(int line, int color, String content) {
		System.out.println("调用前显示:" + content);
		return buildLoadTempDisplayCommand(line, 0, color, content);
	}

	// ==================== 智慧停车四行显示场景 ====================

	/**
	 * 构建智慧停车四行显示场景
	 * @param parkingSpaceCount 剩余车位数（动态参数）
	 * @param color 显示颜色
	 * @return 四行显示命令数组
	 */
	public static String[] buildSmartParkingDisplay(int parkingSpaceCount, int color) {
		return new String[] { buildLoadTempDisplayCommand(1, color, "智慧停车"),
				buildLoadTempDisplayCommand(2, color, "车位" + parkingSpaceCount + "个"),
				buildLoadTempDisplayCommand(3, color, "一车一杆"), buildLoadTempDisplayCommand(4, color, "请勿跟车") };
	}

	/**
	 * 构建红色智慧停车四行显示场景（默认颜色）
	 */
	public static String[] buildSmartParkingDisplay(int parkingSpaceCount) {
		return buildSmartParkingDisplay(parkingSpaceCount, COLOR_RED);
	}

	/**
	 * 构建带语音提示的智慧停车完整场景
	 */
	public static String[] buildSmartParkingScene(int parkingSpaceCount) {
		String[] displayCommands = buildSmartParkingDisplay(parkingSpaceCount, COLOR_RED);

		// 可以添加语音命令
		// String voiceCommand = buildParkingSpaceVoice(parkingSpaceCount);

		return displayCommands;
	}

	/**
	 * 构建剩余车位语音播报
	 */
	public static String buildParkingSpaceVoice(int parkingSpaceCount) {
		String content;
		if (parkingSpaceCount > 10) {
			content = "剩余车位充足，欢迎停车";
		}
		else if (parkingSpaceCount > 0) {
			content = "剩余车位" + parkingSpaceCount + "个，请尽快停车";
		}
		else {
			content = "车位已满，请稍候";
		}
		return buildVoiceCommand(content);
	}

	/**
	 * 构建语音播报指令
	 */
	public static String buildVoiceCommand(String text) {
		String hexContent = stringToHex(text);
		return buildFrame(CMD_PLAY_VOICE, hexContent);
	}

	// ==================== 其他停车场相关场景 ====================

	/**
	 * 构建入场欢迎显示场景
	 */
	public static String[] buildEntryWelcomeDisplay(String plateNumber, int parkingSpaceCount) {
		return new String[] { buildLoadTempDisplayCommand(1, COLOR_GREEN, "欢迎光临"),
				buildLoadTempDisplayCommand(2, COLOR_GREEN, plateNumber),
				buildLoadTempDisplayCommand(3, COLOR_GREEN, "一车一杆自动识别"),
				buildLoadTempDisplayCommand(4, COLOR_GREEN, "剩余车位" + parkingSpaceCount + "个") };
	}

	/**
	 * 构建出场显示场景
	 */
	public static String[] buildExitDisplay(String plateNumber, String amount) {
		return new String[] { buildLoadTempDisplayCommand(1, COLOR_GREEN, "一路平安"),
				buildLoadTempDisplayCommand(2, COLOR_GREEN, plateNumber),
				buildLoadTempDisplayCommand(3, COLOR_GREEN, "缴费" + amount + "元"),
				buildLoadTempDisplayCommand(4, COLOR_GREEN, "谢谢光临") };
	}

	/**
	 * 构建车位紧张警告显示
	 */
	public static String[] buildParkingFullWarning(int parkingSpaceCount) {
		int color = parkingSpaceCount <= 5 ? COLOR_RED : COLOR_YELLOW;
		String warningText = parkingSpaceCount <= 5 ? "车位紧张" : "车位较少";

		return new String[] { buildLoadTempDisplayCommand(1, color, warningText),
				buildLoadTempDisplayCommand(2, color, "剩余" + parkingSpaceCount + "个"),
				buildLoadTempDisplayCommand(3, color, "请尽快停车"), buildLoadTempDisplayCommand(4, color, "谢谢配合") };
	}

	// ==================== 取消显示控制 ====================

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

	/**
	 * 构建取消指定行显示指令
	 */
	public static String buildCancelLineDisplayCommand(int line) {
		if (line < 1 || line > 4) {
			throw new IllegalArgumentException("行号必须在1-4之间");
		}
		int lineMask = 1 << (line - 1);
		return buildCancelTempDisplayCommand(lineMask);
	}

	// ==================== 工具方法 ====================

	/**
	 * 字符串转HEX (使用GBK编码处理中文)
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
			byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
			for (byte b : bytes) {
				sb.append(String.format("%02X", b & 0xFF));
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
	 * CRC16校验算法
	 */
	public static int usMBCRC16(byte[] pucFrame, int usLen) {
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

	// ==================== 测试方法 ====================

	public static void main(String[] args) {
		// 测试智慧停车四行显示
		/**
		 * int parkingSpaceCount = 50; // 动态参数
		 *
		 * System.out.println("智慧停车四行显示命令:"); String[] commands =
		 * buildSmartParkingDisplay(parkingSpaceCount); for (int i = 0; i <
		 * commands.length; i++) { System.out.println("第" + (i + 1) + "行: " +
		 * commands[i]); }
		 *
		 * System.out.println("\n显示效果:"); System.out.println("智慧停车");
		 * System.out.println("车位" + parkingSpaceCount + "个"); System.out.println("一车一杆");
		 * System.out.println("请勿跟车");
		 *
		 * // 测试不同车位数的显示 System.out.println("\n不同车位数的显示:"); int[] testCounts = {5, 15, 0,
		 * 48}; for (int count : testCounts) { System.out.println("\n车位" + count +
		 * "个时的显示:"); String[] testCommands = buildSmartParkingDisplay(count); for (String
		 * cmd : testCommands) { System.out.println(cmd); } }
		 *
		 * // 测试取消显示 String cancelCmd = buildCancelAllTempDisplayCommand();
		 * System.out.println("\n取消所有显示: " + cancelCmd);
		 *
		 *
		 * String a =Rs485Util.buildPlayVoiceCommand("渝A12345" + "\u0001"); // 车牌 + 欢迎光临语音
		 * System.out.println("\n取消所有显示1: " + a);
		 **/

		// 测试"请缴费"语音
		String pleasePayCmd = buildPleasePayVoice();
		System.out.println("请缴费语音: " + pleasePayCmd);
		// 输出: AA550164002200010B0776AF (与您提供的指令一致)

		// 测试支付10元语音
		String payment10Cmd = buildPaymentVoice(10);
		System.out.println("支付10元语音: " + payment10Cmd);
		// 播报: "请缴费停车费10元"

		// 播报: "本次消费25元欢迎再次光临"
	}

}