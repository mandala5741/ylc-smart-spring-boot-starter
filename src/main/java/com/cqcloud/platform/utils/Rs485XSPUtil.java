package com.cqcloud.platform.utils;

/**
 * 横屏(小竖屏)
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕 2025年8月27日 🐬🐇 💓💕
 */
public class Rs485XSPUtil {

    // 流水号计数器
    private static int serialNumber = 0x20;
    // 设备地址（默认100=0x64）
    private static final int DEFAULT_ADDRESS = 0x64;

    // 帧头帧尾定义
    private static final String FRAME_HEADER = "AA55";
    private static final String FRAME_TAIL = "AF";

    // ==================== 语音索引常量 ====================
    public static final int WELCOME = 0x01;           // 欢迎光临
    public static final int PAYMENT = 0x0B;           // 请缴费
    public static final int PLATE_NUMBER = 0x13;      // 此车/车牌
    public static final int PLEASE_ENTER = 0x14;      // 请入场停车
    public static final int SMOOTH_JOURNEY = 0x5F;    // 一路顺风
    public static final int AGAIN_WELCOME = 0x62;     // 再次光临
    public static final int PARKING_FEE = 0x6A;       // 停车费
    public static final int CONSUMPTION = 0x44;       // 消费
    public static final int YUAN = 0x2D;              // 元
    public static final int THIS_TIME = 0x16;         // 本次

    /**
     * 数字语音索引 (0-9)
     */
    public static final int[] NUMBER_VOICE = {
            0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39  // 0-9
    };
    /**
     * 字符串转HEX (使用GBK编码处理中文)
     */
    public static String stringToHex(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes = str.getBytes("GBK");
            for (byte b : bytes) {
                sb.append(String.format("%02X", b & 0xFF));
            }
        } catch (java.io.UnsupportedEncodingException e) {
            // 降级处理
            byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
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
    private static final byte[] aucCRCHi = {
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x00, (byte) 0xC1, (byte) 0x81, 0x40,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40, 0x01, (byte) 0xC0, (byte) 0x80, 0x41, 0x01, (byte) 0xC0, (byte) 0x80, 0x41,
            0x00, (byte) 0xC1, (byte) 0x81, 0x40
    };

    private static final byte[] aucCRCLo = {
            0x00, (byte) 0xC0, (byte) 0xC1, 0x01, (byte) 0xC3, 0x03, 0x02, (byte) 0xC2, (byte) 0xC6, 0x06, 0x07, (byte) 0xC7,
            0x05, (byte) 0xC5, (byte) 0xC4, 0x04, (byte) 0xCC, 0x0C, 0x0D, (byte) 0xCD, 0x0F, (byte) 0xCF, (byte) 0xCE, 0x0E,
            0x0A, (byte) 0xCA, (byte) 0xCB, 0x0B, (byte) 0xC9, 0x09, 0x08, (byte) 0xC8, (byte) 0xD8, 0x18, 0x19, (byte) 0xD9,
            0x1B, (byte) 0xDB, (byte) 0xDA, 0x1A, 0x1E, (byte) 0xDE, (byte) 0xDF, 0x1F, (byte) 0xDD, 0x1D, 0x1C, (byte) 0xDC,
            0x14, (byte) 0xD4, (byte) 0xD5, 0x15, (byte) 0xD7, 0x17, 0x16, (byte) 0xD6, (byte) 0xD2, 0x12, 0x13, (byte) 0xD3,
            0x11, (byte) 0xD1, (byte) 0xD0, 0x10, (byte) 0xF0, 0x30, 0x31, (byte) 0xF1, 0x33, (byte) 0xF3, (byte) 0xF2, 0x32,
            0x36, (byte) 0xF6, (byte) 0xF7, 0x37, (byte) 0xF5, 0x35, 0x34, (byte) 0xF4, 0x3C, (byte) 0xFC, (byte) 0xFD, 0x3D,
            (byte) 0xFF, 0x3F, 0x3E, (byte) 0xFE, (byte) 0xFA, 0x3A, 0x3B, (byte) 0xFB, 0x39, (byte) 0xF9, (byte) 0xF8, 0x38,
            0x28, (byte) 0xE8, (byte) 0xE9, 0x29, (byte) 0xEB, 0x2B, 0x2A, (byte) 0xEA, (byte) 0xEE, 0x2E, 0x2F, (byte) 0xEF,
            0x2D, (byte) 0xED, (byte) 0xEC, 0x2C, (byte) 0xE4, 0x24, 0x25, (byte) 0xE5, 0x27, (byte) 0xE7, (byte) 0xE6, 0x26,
            0x22, (byte) 0xE2, (byte) 0xE3, 0x23, (byte) 0xE1, 0x21, 0x20, (byte) 0xE0, (byte) 0xA0, 0x60, 0x61, (byte) 0xA1,
            0x63, (byte) 0xA3, (byte) 0xA2, 0x62, 0x66, (byte) 0xA6, (byte) 0xA7, 0x67, (byte) 0xA5, 0x65, 0x64, (byte) 0xA4,
            0x6C, (byte) 0xAC, (byte) 0xAD, 0x6D, (byte) 0xAF, 0x6F, 0x6E, (byte) 0xAE, (byte) 0xAA, 0x6A, 0x6B, (byte) 0xAB,
            0x69, (byte) 0xA9, (byte) 0xA8, 0x68, 0x78, (byte) 0xB8, (byte) 0xB9, 0x79, (byte) 0xBB, 0x7B, 0x7A, (byte) 0xBA,
            (byte) 0xBE, 0x7E, 0x7F, (byte) 0xBF, 0x7D, (byte) 0xBD, (byte) 0xBC, 0x7C, (byte) 0xB4, 0x74, 0x75, (byte) 0xB5,
            0x77, (byte) 0xB7, (byte) 0xB6, 0x76, 0x72, (byte) 0xB2, (byte) 0xB3, 0x73, (byte) 0xB1, 0x71, 0x70, (byte) 0xB0,
            0x50, (byte) 0x90, (byte) 0x91, 0x51, (byte) 0x93, 0x53, 0x52, (byte) 0x92, (byte) 0x96, 0x56, 0x57, (byte) 0x97,
            0x55, (byte) 0x95, (byte) 0x94, 0x54, (byte) 0x9C, 0x5C, 0x5D, (byte) 0x9D, 0x5F, (byte) 0x9F, (byte) 0x9E, 0x5E,
            0x5A, (byte) 0x9A, (byte) 0x9B, 0x5B, (byte) 0x99, 0x59, 0x58, (byte) 0x98, (byte) 0x88, 0x48, 0x49, (byte) 0x89,
            0x4B, (byte) 0x8B, (byte) 0x8A, 0x4A, 0x4E, (byte) 0x8E, (byte) 0x8F, 0x4F, (byte) 0x8D, 0x4D, 0x4C, (byte) 0x8C,
            0x44, (byte) 0x84, (byte) 0x85, 0x45, (byte) 0x87, 0x47, 0x46, (byte) 0x86, (byte) 0x82, 0x42, 0x43, (byte) 0x83,
            0x41, (byte) 0x81, (byte) 0x80, 0x40
    };
// ==================== 小竖屏显示控制方法 ====================

    /**
     * 构建小竖屏显示指令 (0x29) - 一次性发送四行内容
     * 协议格式：每行 = 0001 + 颜色 + 内容
     *
     * @param lines 四行显示内容数组
     * @param colors 四行颜色数组
     */
    public static String buildSmallScreenDisplayCommand(String[] lines, int[] colors) {
        if (lines.length != 4 || colors.length != 4) {
            throw new IllegalArgumentException("必须提供4行内容和4个颜色");
        }

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String hexContent = stringToHex(lines[i]);
            // 格式：0001 + 颜色 + 内容
            content.append(String.format("0001%02X", colors[i])).append(hexContent);
        }

        return buildFrame("29", content.toString());
    }

    /**
     * 构建小竖屏欢迎显示
     */
    public static String buildSmallScreenWelcome() {
        String[] lines = {"欢迎光临", "车牌识别", "一车一杆", "减速慢行"};
        int[] colors = {0x08, 0x08, 0x08, 0x08}; // 根据日志，都是08
        return buildSmallScreenDisplayCommand(lines, colors);
    }

    /**
     * 构建小竖屏智慧停车显示
     */
    public static String buildSmallScreenSmartParking(int parkingSpaceCount) {
        String[] lines = {
                "智慧停车",
                "车位" + parkingSpaceCount + "个",
                "一车一杆",
                "请勿跟车"
        };
        int[] colors = {0x08, 0x08, 0x08, 0x08};
        return buildSmallScreenDisplayCommand(lines, colors);
    }

    /**
     * 构建小竖屏入场显示
     */
    public static String buildSmallScreenEntry(String plateNumber, int parkingSpaceCount) {
        String[] lines = {
                "欢迎光临",
                plateNumber,
                "一车一杆自动识别",
                "剩余车位" + parkingSpaceCount + "个"
        };
        int[] colors = {0x08, 0x08, 0x08, 0x08};
        return buildSmallScreenDisplayCommand(lines, colors);
    }

    /**
     * 构建小竖屏出场显示
     */
    public static String buildSmallScreenExit(String plateNumber, String amount) {
        String[] lines = {
                "一路平安",
                plateNumber,
                "缴费" + amount + "元",
                "谢谢光临"
        };
        int[] colors = {0x08, 0x08, 0x08, 0x08};
        return buildSmallScreenDisplayCommand(lines, colors);
    }

// ==================== 小竖屏完整场景方法 ====================

    /**
     * 构建小竖屏入场完整场景（显示+语音）
     */
    public static String[] buildSmallScreenEntryScene(String plateNumber, int parkingSpaceCount) {
        return new String[] {
                buildSmallScreenEntry(plateNumber, parkingSpaceCount), // 显示
                buildPlateWelcomeVoice(plateNumber),                   // 车牌欢迎语音
                buildPleaseEnterVoice()                               // 请入场语音
        };
    }
    /**
     * 构建单个语音播报指令 (0x22)
     * 格式：语音索引(1字节)
     */
    public static String buildVoiceCommand(int voiceIndex) {
        String data = String.format("%02X", voiceIndex);
        return buildFrame("22", data);
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
        String reserved = "00";

        // 计算数据长度（字节数）
        int dataLength = data.length() / 2;
        String lengthHex = String.format("%04X", dataLength);

        // 构建数据部分
        String frameData = serial + address + reserved + command + lengthHex + data;

        // 计算CRC
        String dataForCRC = frameData + "0000";
        byte[] dataBytes = hexStringToByteArray(dataForCRC);
        int crc = usMBCRC16(dataBytes, dataBytes.length);
        String crcHex = String.format("%04X", crc);

        return FRAME_HEADER + frameData + crcHex + FRAME_TAIL;
    }

    /**
     * 构建请入场停车语音 - 使用字符索引
     */
    public static String buildPleaseEnterVoice() {
        return buildVoiceCommand('\u0014'); // 0x14 请入场停车
    }
    /**
     * 构建车牌号+欢迎光临语音 - 使用字符索引
     * 示例：渝A12345欢迎光临
     */
    public static String buildPlateWelcomeVoice(String plateNumber) {
        return buildPlateWithVoiceCommand(plateNumber, '\u0001'); // 车牌 + 欢迎光临
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
     * 构建小竖屏出场完整场景（显示+语音）
     */
    public static String[] buildSmallScreenExitScene(String plateNumber, int amount) {
        return new String[] {
                buildSmallScreenExit(plateNumber, String.valueOf(amount)), // 显示
                buildPlatePaymentVoice(plateNumber),                      // 车牌请缴费语音
                buildPaymentAmountVoice(amount),                         // 金额语音
                buildSmoothJourneyVoice()                                // 一路顺风语音
        };
    }
    /**
     * 构建一路顺风语音 - 使用字符索引
     */
    public static String buildSmoothJourneyVoice() {
        return buildVoiceCommand('\u005F'); // 0x5F 一路顺风
    }
    /**
     * 构建请缴费XX元语音
     */
    public static String buildPaymentAmountVoice(int amount) {
        String amountStr = String.valueOf(amount);
        int[] digits = new int[amountStr.length()];
        for (int i = 0; i < amountStr.length(); i++) {
            digits[i] = Character.getNumericValue(amountStr.charAt(i));
        }

        int[] voiceIndexes = new int[1 + digits.length + 1];
        voiceIndexes[0] = PAYMENT;  // 请缴费

        for (int i = 0; i < digits.length; i++) {
            voiceIndexes[i + 1] = NUMBER_VOICE[digits[i]];  // 金额数字
        }

        voiceIndexes[voiceIndexes.length - 1] = YUAN;  // 元

        return buildMultiVoiceCommand(voiceIndexes);
    }
    /**
     * 构建多语音组合指令
     * 格式：多个语音索引连续
     */
    public static String buildMultiVoiceCommand(int[] voiceIndexes) {
        StringBuilder data = new StringBuilder();
        for (int index : voiceIndexes) {
            data.append(String.format("%02X", index));
        }
        return buildFrame("22", data.toString());
    }
    /**
     * 构建车牌号+请缴费语音 - 使用字符索引
     * 示例：渝A12345请缴费
     */
    public static String buildPlatePaymentVoice(String plateNumber) {
        return buildPlateWithVoiceCommand(plateNumber, '\u000B'); // 车牌 + 请缴费
    }
    // ==================== JSON构建方法 ====================

    /**
     * 构建单个RS485命令的JSON格式
     *
     * @param command RS485命令字符串
     * @return JSON格式字符串
     */
    public static String buildRs485Json(String command) {
        return String.format(
                "{\"error_str\":\"noerror\",\"gpio_data\":[{\"action\":\"off\",\"ionum\":\"io1\"}],\"error_num\":0,\"rs485_data\":[{\"encodetype\":\"hex2string\",\"data\":\"%s\"}]}",
                command
        );
    }

    /**
     * 构建多个RS485命令的JSON格式
     *
     * @param commands RS485命令数组
     * @return JSON格式字符串
     */
    public static String buildRs485Json(String[] commands) {
        StringBuilder rs485Data = new StringBuilder();
        rs485Data.append("[");

        for (int i = 0; i < commands.length; i++) {
            rs485Data.append(String.format(
                    "{\"encodetype\":\"hex2string\",\"data\":\"%s\"}",
                    commands[i]
            ));
            if (i < commands.length - 1) {
                rs485Data.append(",");
            }
        }
        rs485Data.append("]");

        return String.format(
                "{\"error_str\":\"noerror\",\"gpio_data\":[{\"action\":\"off\",\"ionum\":\"io1\"}],\"error_num\":0,\"rs485_data\":%s}",
                rs485Data.toString()
        );
    }

    /**
     * 构建带自定义GPIO的RS485 JSON格式
     *
     * @param commands RS485命令数组
     * @param gpioAction GPIO动作 ("on" 或 "off")
     * @param gpioNum GPIO编号 ("io1", "io2", 等)
     * @return JSON格式字符串
     */
    public static String buildRs485Json(String[] commands, String gpioAction, String gpioNum) {
        StringBuilder rs485Data = new StringBuilder();
        rs485Data.append("[");

        for (int i = 0; i < commands.length; i++) {
            rs485Data.append(String.format(
                    "{\"encodetype\":\"hex2string\",\"data\":\"%s\"}",
                    commands[i]
            ));
            if (i < commands.length - 1) {
                rs485Data.append(",");
            }
        }
        rs485Data.append("]");

        return String.format(
                "{\"error_str\":\"noerror\",\"gpio_data\":[{\"action\":\"%s\",\"ionum\":\"%s\"}],\"error_num\":0,\"rs485_data\":%s}",
                gpioAction, gpioNum, rs485Data.toString()
        );
    }

    /**
     * 构建带自定义错误信息的RS485 JSON格式
     *
     * @param commands RS485命令数组
     * @param errorStr 错误信息
     * @param errorNum 错误码
     * @return JSON格式字符串
     */
    public static String buildRs485Json(String[] commands, String errorStr, int errorNum) {
        StringBuilder rs485Data = new StringBuilder();
        rs485Data.append("[");

        for (int i = 0; i < commands.length; i++) {
            rs485Data.append(String.format(
                    "{\"encodetype\":\"hex2string\",\"data\":\"%s\"}",
                    commands[i]
            ));
            if (i < commands.length - 1) {
                rs485Data.append(",");
            }
        }
        rs485Data.append("]");

        return String.format(
                "{\"error_str\":\"%s\",\"gpio_data\":[{\"action\":\"off\",\"ionum\":\"io1\"}],\"error_num\":%d,\"rs485_data\":%s}",
                errorStr, errorNum, rs485Data.toString()
        );
    }

    /**
     * 构建完全自定义的RS485 JSON格式
     *
     * @param commands RS485命令数组
     * @param gpioAction GPIO动作
     * @param gpioNum GPIO编号
     * @param errorStr 错误信息
     * @param errorNum 错误码
     * @return JSON格式字符串
     */
    public static String buildRs485Json(String[] commands, String gpioAction, String gpioNum, String errorStr, int errorNum) {
        StringBuilder rs485Data = new StringBuilder();
        rs485Data.append("[");

        for (int i = 0; i < commands.length; i++) {
            rs485Data.append(String.format(
                    "{\"encodetype\":\"hex2string\",\"data\":\"%s\"}",
                    commands[i]
            ));
            if (i < commands.length - 1) {
                rs485Data.append(",");
            }
        }
        rs485Data.append("]");

        return String.format(
                "{\"error_str\":\"%s\",\"gpio_data\":[{\"action\":\"%s\",\"ionum\":\"%s\"}],\"error_num\":%d,\"rs485_data\":%s}",
                errorStr, gpioAction, gpioNum, errorNum, rs485Data.toString()
        );
    }
    // ==================== 便捷JSON构建方法 ====================

    /**
     * 构建语音播报JSON
     */
    public static String buildVoiceJson(int voiceIndex) {
        String command = buildVoiceCommand(voiceIndex);
        return buildRs485Json(command);
    }

    /**
     * 构建语音播报JSON（使用字符索引）
     */
    public static String buildVoiceJson(char voiceChar) {
        String command = buildVoiceCommand(voiceChar);
        return buildRs485Json(command);
    }


    /**
     * 构建车牌语音播报JSON（使用字符索引）
     */
    public static String buildPlateVoiceJson(String plateNumber, char voiceChar) {
        String command = buildPlateWithVoiceCommand(plateNumber, voiceChar);
        return buildRs485Json(command);
    }

// ==================== 小竖屏JSON构建方法 ====================

    /**
     * 构建小竖屏显示JSON
     */
    public static String buildSmallScreenDisplayJson(String[] lines, int[] colors) {
        String command = buildSmallScreenDisplayCommand(lines, colors);
        return buildRs485Json(command);
    }

    /**
     * 构建小竖屏欢迎显示JSON
     */
    public static String buildSmallScreenWelcomeJson() {
        String command = buildSmallScreenWelcome();
        return buildRs485Json(command);
    }

    /**
     * 构建小竖屏智慧停车JSON
     */
    public static String buildSmallScreenSmartParkingJson(int parkingSpaceCount) {
        String command = buildSmallScreenSmartParking(parkingSpaceCount);
        return buildRs485Json(command);
    }

    /**
     * 构建小竖屏入场场景JSON
     */
    public static String buildSmallScreenEntrySceneJson(String plateNumber, int parkingSpaceCount) {
        String[] commands = buildSmallScreenEntryScene(plateNumber, parkingSpaceCount);
        return buildRs485Json(commands);
    }

    /**
     * 构建小竖屏出场场景JSON
     */
    public static String buildSmallScreenExitSceneJson(String plateNumber, int amount) {
        String[] commands = buildSmallScreenExitScene(plateNumber, amount);
        return buildRs485Json(commands);
    }
    // ==================== 补充缺失的方法 ====================

    /**
     * 构建语音播报指令 (0x22) - 支持字符索引
     */
    public static String buildVoiceCommand(char voiceChar) {
        String data = String.format("%02X", (int) voiceChar);
        return buildFrame("22", data);
    }

    /**
     * 构建车牌语音播报JSON
     */
    public static String buildPlateVoiceJson(String plateNumber, int voiceIndex) {
        String command = buildPlateWithVoiceCommand(plateNumber, voiceIndex);
        return buildRs485Json(command);
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

    /**
     * 构建欢迎光临语音 - 使用字符索引
     */
    public static String buildWelcomeVoice() {
        return buildVoiceCommand('\u0001'); // 0x01 欢迎光临
    }

    /**
     * 构建请缴费语音 - 使用字符索引
     */
    public static String buildPaymentVoice() {
        return buildVoiceCommand('\u000B'); // 0x0B 请缴费
    }

    /**
     * 构建车牌号+一路平安语音 - 使用字符索引
     */
    public static String buildPlateSmoothJourneyVoice(String plateNumber) {
        return buildPlateWithVoiceCommand(plateNumber, '\u005F'); // 车牌 + 一路平安
    }

    /**
     * 构建车牌号+再次光临语音 - 使用字符索引
     */
    public static String buildPlateAgainWelcomeVoice(String plateNumber) {
        return buildPlateWithVoiceCommand(plateNumber, '\u0062'); // 车牌 + 再次光临
    }

// ==================== 大屏显示控制方法 ====================

    /**
     * 构建显示指令 (0x37) - 固定显示内容
     * 协议格式：控制字1(行号) | 控制字2(00) | 控制字3(颜色) | 控制字4(00) | 内容
     */
    public static String buildDisplayCommand(int line, int color, String content) {
        String hexContent = stringToHex(content);
        String controlData = String.format("%02X00%02X00", line, color) + hexContent;
        return buildFrame("37", controlData);
    }

    /**
     * 构建智慧停车四行显示
     */
    public static String[] buildSmartParkingDisplay(int parkingSpaceCount) {
        return new String[] {
                buildDisplayCommand(1, 0, "智慧停车"),
                buildDisplayCommand(2, 3, "车位" + parkingSpaceCount + "个"),
                buildDisplayCommand(3, 4, "一车一杆"),
                buildDisplayCommand(4, 8, "请勿跟车")
        };
    }

    /**
     * 构建完整的四行欢迎显示场景
     */
    public static String[] buildWelcomeDisplay() {
        return new String[] {
                buildDisplayCommand(1, 0, "欢迎光临"),
                buildDisplayCommand(2, 3, "车牌识别"),
                buildDisplayCommand(3, 4, "一车一杆"),
                buildDisplayCommand(4, 8, "减速慢行")
        };
    }

    /**
     * 构建入场显示场景
     */
    public static String[] buildEntryDisplay(String plateNumber, int parkingSpaceCount) {
        return new String[] {
                buildDisplayCommand(1, 0, "欢迎光临"),
                buildDisplayCommand(2, 3, plateNumber),
                buildDisplayCommand(3, 4, "一车一杆自动识别"),
                buildDisplayCommand(4, 8, "剩余车位" + parkingSpaceCount + "个")
        };
    }

    /**
     * 构建出场显示场景
     */
    public static String[] buildExitDisplay(String plateNumber, String amount) {
        return new String[] {
                buildDisplayCommand(1, 0, "一路平安"),
                buildDisplayCommand(2, 3, plateNumber),
                buildDisplayCommand(3, 4, "缴费" + amount + "元"),
                buildDisplayCommand(4, 8, "谢谢光临")
        };
    }

// ==================== 大屏完整场景方法 ====================

    /**
     * 构建入场完整场景（显示+语音）
     */
    public static String[] buildEntryScene(String plateNumber, int parkingSpaceCount) {
        return new String[] {
                // 显示内容
                buildDisplayCommand(1, 0, "欢迎光临"),
                buildDisplayCommand(2, 3, plateNumber),
                buildDisplayCommand(3, 4, "一车一杆自动识别"),
                buildDisplayCommand(4, 8, "剩余车位" + parkingSpaceCount + "个"),
                // 语音播报 - 使用字符索引
                buildPlateWelcomeVoice(plateNumber),  // 车牌 + 欢迎光临
                buildPleaseEnterVoice()               // 请入场停车
        };
    }

    /**
     * 构建出场完整场景（显示+语音）
     */
    public static String[] buildExitScene(String plateNumber, int amount) {
        return new String[] {
                // 显示内容
                buildDisplayCommand(1, 0, "一路平安"),
                buildDisplayCommand(2, 3, plateNumber),
                buildDisplayCommand(3, 4, "缴费" + amount + "元"),
                buildDisplayCommand(4, 8, "谢谢光临"),
                // 语音播报 - 使用字符索引
                buildPlatePaymentVoice(plateNumber),  // 车牌 + 请缴费
                buildPaymentAmountVoice(amount),      // XX元
                buildSmoothJourneyVoice()             // 一路顺风
        };
    }

// ==================== 便捷JSON构建方法补充 ====================

    /**
     * 构建入场完整场景JSON
     */
    public static String buildEntrySceneJson(String plateNumber, int parkingSpaceCount) {
        String[] commands = buildEntryScene(plateNumber, parkingSpaceCount);
        return buildRs485Json(commands);
    }

    /**
     * 构建出场场景JSON
     */
    public static String buildExitSceneJson(String plateNumber, int amount) {
        String[] commands = buildExitScene(plateNumber, amount);
        return buildRs485Json(commands);
    }

    /**
     * 构建智慧停车显示JSON
     */
    public static String buildSmartParkingJson(int parkingSpaceCount) {
        String[] commands = buildSmartParkingDisplay(parkingSpaceCount);
        return buildRs485Json(commands);
    }

    /**
     * 构建欢迎显示JSON
     */
    public static String buildWelcomeDisplayJson() {
        String[] commands = buildWelcomeDisplay();
        return buildRs485Json(commands);
    }

// ==================== 组合语音方法 ====================

    /**
     * 构建入场完整语音 - "欢迎光临，请入场停车" - 使用字符索引
     */
    public static String buildEntryCompleteVoice() {
        return buildMultiVoiceCommand(new int[]{0x01, 0x14}); // 欢迎光临 + 请入场停车
    }

    /**
     * 构建出场祝福语音 - "一路顺风，欢迎再次光临" - 使用字符索引
     */
    public static String buildExitBlessingVoice() {
        return buildMultiVoiceCommand(new int[]{0x5F, 0x62}); // 一路顺风 + 再次光临
    }

    /**
     * 构建本次消费XX元语音
     */
    public static String buildConsumptionVoice(int amount) {
        String amountStr = String.valueOf(amount);
        int[] digits = new int[amountStr.length()];
        for (int i = 0; i < amountStr.length(); i++) {
            digits[i] = Character.getNumericValue(amountStr.charAt(i));
        }

        int[] voiceIndexes = new int[2 + digits.length + 2];
        voiceIndexes[0] = THIS_TIME;      // 本次
        voiceIndexes[1] = CONSUMPTION;    // 消费

        for (int i = 0; i < digits.length; i++) {
            voiceIndexes[i + 2] = NUMBER_VOICE[digits[i]];  // 金额数字
        }

        voiceIndexes[voiceIndexes.length - 2] = YUAN;        // 元
        voiceIndexes[voiceIndexes.length - 1] = AGAIN_WELCOME; // 再次光临

        return buildMultiVoiceCommand(voiceIndexes);
    }

// ==================== 字符索引常量类 ====================

    /**
     * 语音索引字符常量
     */
    public static class VoiceChar {
        public static final char WELCOME = '\u0001';           // 欢迎光临
        public static final char PAYMENT = '\u000B';           // 请缴费
        public static final char PLATE_NUMBER = '\u0013';      // 此车/车牌
        public static final char PLEASE_ENTER = '\u0014';      // 请入场停车
        public static final char SMOOTH_JOURNEY = '\u005F';    // 一路顺风
        public static final char AGAIN_WELCOME = '\u0062';     // 再次光临
        public static final char PARKING_FEE = '\u006A';       // 停车费
        public static final char CONSUMPTION = '\u0044';       // 消费
        public static final char YUAN = '\u002D';              // 元
        public static final char THIS_TIME = '\u0016';         // 本次
    }

// ==================== 测试方法 ====================

    public static void main(String[] args) {
        String plateNumber = "渝A12345";

        System.out.println("=== 各种JSON格式输出 ===");

        // 1. 单个语音命令
        System.out.println("1. 欢迎光临语音:");
        System.out.println(buildVoiceJson(WELCOME));

        // 2. 车牌欢迎语音
        System.out.println("\n2. 车牌欢迎语音:");
        System.out.println(buildPlateVoiceJson(plateNumber, WELCOME));

        // 3. 入场完整场景
        System.out.println("\n3. 入场完整场景:");
        System.out.println(buildEntrySceneJson(plateNumber, 48));

        // 4. 出场场景
        System.out.println("\n4. 出场场景:");
        System.out.println(buildExitSceneJson(plateNumber, 15));

        // 5. 智慧停车显示
        System.out.println("\n5. 智慧停车显示:");
        System.out.println(buildSmartParkingJson(50));

        // 6. 自定义GPIO控制
        System.out.println("\n6. 自定义GPIO控制:");
        String[] welcomeCommands = buildWelcomeDisplay();
        System.out.println(buildRs485Json(welcomeCommands, "on", "io2"));

        // 7. 小竖屏显示
        System.out.println("\n7. 小竖屏欢迎显示:");
        System.out.println(buildSmallScreenWelcomeJson());

        // 8. 带错误信息的JSON
        System.out.println("\n8. 带错误信息的JSON:");
        System.out.println(buildRs485Json(welcomeCommands, "设备异常", 1001));

        // 9. 使用字符索引的语音
        System.out.println("\n9. 使用字符索引的语音:");
        System.out.println("欢迎光临: " + buildVoiceJson(VoiceChar.WELCOME));
        System.out.println("请缴费: " + buildVoiceJson(VoiceChar.PAYMENT));
        System.out.println("车牌欢迎: " + buildPlateVoiceJson(plateNumber, VoiceChar.WELCOME));
        System.out.println("车牌平安: " + buildPlateVoiceJson(plateNumber, VoiceChar.SMOOTH_JOURNEY));
    }
}