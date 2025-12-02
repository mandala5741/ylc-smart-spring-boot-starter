package com.cqcloud.platform.utils;


import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 显示屏通信数据包装工具类
 */
public class LED_ResponseUtil {
    /**
     * @param VoiceText 需要播放的语音文字，字符串类型。
     * @return
     */
    public static byte[] LED_PlayVoice(String VoiceText) throws UnsupportedEncodingException {
        //分配缓冲数组
        byte[] Buff = new byte[512];
        //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936
        byte[] TextBuff = VoiceText.getBytes("GBK");
        int Pos = 0;
        //获取字符串数组的长度
        int TextLen = TextBuff.length;

        //数据最大不能超过255长度
        if ((1 + TextLen) >= 255) {
            return null;
        }
        /*0.填充显示命令参数*/

        //显示屏地址
        Buff[Pos++] = 0x00;
        //固定参数
        Buff[Pos++] = 0x64;
        //包序列
        Buff[Pos++] = (byte) 0xFF;
        //包序列
        Buff[Pos++] = (byte) 0xFF;
        //指令
        Buff[Pos++] = 0x30;
        //数据长度
        Buff[Pos++] = (byte) (1 + TextLen);
        //播放选项
        Buff[Pos++] = (byte) 0x01;
        /*1.复制语音文本内容到缓冲区*/
        for (int i = 0; i < TextLen; i++) {
            Buff[Pos++] = TextBuff[i];
        }
        /*2.计算校验码*/
        int CRC = CRC16Util.getCRC3(Buff, Pos);
        //校验码低字节
        Buff[Pos++] = (byte) (CRC & 0xff);
        //校验码高字节
        Buff[Pos++] = (byte) ((CRC >> 8) & 0xff);

        byte[] copyByte = getCopyByte(Buff, Pos);

        return copyByte;
    }

    /**
     * @param Line       显示的行号，对应显示屏的显示行位置,取值范围为0~3，分别对应第一行至第四行。
     * @param Text       显示的字符内容，字符串类型。
     * @param DisMode    显示模式，取值范围及含义如下:
     *                   0 立即显示，整行显示不完的字符，自动下一屏显示。
     *                   1 从右向左移动显示，每移动完一满行会停留。
     *                   2 从左向右移动显示，每移动完一满行会停留。
     *                   3 从下往上移动显示，每移动完一满行会停留。
     *                   4 从上往下移动显示，每移动完一满行会停留。
     *                   5 从上下往中间拉开显示，每满行会停留。
     *                   6 从中间往上下拉开显示，每满行会停留。
     *                   7 从左右往中间拉开显示，每满行会停留。
     *                   8 从中间往左右拉开显示，每满行会停留。
     *                   13 逐字出现显示，每满行会停留。
     *                   21 连续往左移动显示，中间不会停留，直到最后满行会停留。
     * @param EnterSpeed 显示的速度，单位为毫秒。
     * @param DelayTime  停留时间，单位为秒。
     * @param DisTimes   表示这条文字循环显示的次数，0为一直循环显示,最大255次。
     */
    public static byte[] LED_DisText(byte Line, String Text, byte DisMode, byte EnterSpeed, byte DelayTime, byte DisTimes) throws UnsupportedEncodingException {
        //分配缓冲数组
        byte[] Buff = new byte[512];
        //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936
        byte[] TextBuff = Text.getBytes("GBK");
        int Pos = 0;
        //获取字符串数组的长度
        int TextLen = TextBuff.length;

        //数据最大不能超过255长度
        if ((20 + TextLen) >= 255) {
            return null;
        }

        /*0.填充显示命令参数*/
        //显示屏地址
        Buff[Pos++] = 0x00;
        //固定参数
        Buff[Pos++] = 0x64;
        //包序列
        Buff[Pos++] = (byte) 0xFF;
        //包序列
        Buff[Pos++] = (byte) 0xFF;
        //指令
        Buff[Pos++] = 0x62;
        //数据长度
        Buff[Pos++] = (byte) (19 + TextLen);
        //显示行号
        Buff[Pos++] = Line;
        //显示模式
        Buff[Pos++] = DisMode;
        //显示速度
        Buff[Pos++] = EnterSpeed;
        //停留模式
        Buff[Pos++] = 0x00;
        //停留时间
        Buff[Pos++] = DelayTime;
        //退出模式
        Buff[Pos++] = DisMode;
        //退出速度
        Buff[Pos++] = 0x01;
        //字体类型
        Buff[Pos++] = 0x03;
        //显示次数
        Buff[Pos++] = DisTimes;
        //32位字体颜色 红色分量
        Buff[Pos++] = (byte) (LED_COLOR_RED & 0xff);
        //32位字体颜色 绿色分量
        Buff[Pos++] = (byte) ((LED_COLOR_RED >> 8) & 0xff);
        //32位字体颜色 蓝色分量
        Buff[Pos++] = (byte) ((LED_COLOR_RED >> 16) & 0xff);
        //32位字体颜色 保留字节
        Buff[Pos++] = (byte) ((LED_COLOR_RED >> 24) & 0xff);
        //32位背景颜色 红色分量
        Buff[Pos++] = 0x00;
        //32位背景颜色 绿色分量
        Buff[Pos++] = 0x00;
        //32位背景颜色 蓝色分量
        Buff[Pos++] = 0x00;
        //32位背景颜色 保留字节
        Buff[Pos++] = 0x00;
        //16位文本长度 低字节
        Buff[Pos++] = (byte) TextLen;
        //16位文本长度 高字节
        Buff[Pos++] = 0x00;
        /*1.复制文本内容到缓冲区*/
        for (int i = 0; i < TextLen; i++) {
            Buff[Pos++] = TextBuff[i];
        }
        /*2.计算校验码*/
        int CRC = CRC16Util.getCRC3(Buff, Pos);
        //校验码低字节
        Buff[Pos++] = (byte) (CRC & 0xff);
        //校验码高字节
        Buff[Pos++] = (byte) ((CRC >> 8) & 0xff);

        byte[] copyByte = getCopyByte(Buff, Pos);
        return copyByte;
    }

    /**
     * 多行文字和语音一起下载，通过这个接口可以一起下载多行的文字信息，同时可以携带语音文字。
     *
     * @param textContextList 每行的显示参数，详细的解释参加TEXT_CONTEXT 定义处。
     * @param VoiceText       需要播放的语音文字，字符串类型，编码必须是GBK2312。
     * @param SaveFlag        保存标志:
     *                        0 为下载到临时信息区，掉电会丢失，
     *                        1 为下载到广告语存储区，掉电会保存，不建议频繁修改的文字下载到广告语存储区。
     * @throws UnsupportedEncodingException
     */
    public static byte[] LED_MuiltLineDisAndPlayVoice(List<TextContext> textContextList, String VoiceText) throws UnsupportedEncodingException {
        int BuffPos;
        //分配缓冲数组
        byte[] Buff = new byte[512];
        int CRC;
        byte TextContextNum = (byte) textContextList.size();
        /*0.填充命令参数*/
        BuffPos = 0;
        //显示屏地址
        Buff[BuffPos++] = 0x00;
        //固定参数
        Buff[BuffPos++] = 0x64;
        //包序列
        Buff[BuffPos++] = (byte) 0xFF;
        //包序列
        Buff[BuffPos++] = (byte) 0xFF;
        //指令
        Buff[BuffPos++] = 0x6E;
        //数据长度
        Buff[BuffPos++] = 0;

        /*1.填充文本参数*/
        //文本类型,1为广告语，0为临时信息
        Buff[BuffPos++] = 0x00;
        //文本数量
        Buff[BuffPos++] = TextContextNum;
        int i = 0;
        for (TextContext textContext : textContextList) {
            //行号
            Buff[BuffPos++] = textContext.getLID();
            //显示模式
            Buff[BuffPos++] = textContext.getDisMode();
            //显示速度
            Buff[BuffPos++] = 0x01;
            //停留时间
            Buff[BuffPos++] = textContext.getDelayTime();
            //显示次数
            Buff[BuffPos++] = textContext.getDisTimes();
            //32位字体颜色 红色分量
            Buff[BuffPos++] = (byte) (textContext.getTextColor() & 0xff);
            //32位字体颜色 绿色分量
            Buff[BuffPos++] = (byte) ((textContext.getTextColor() >> 8) & 0xff);
            //32位字体颜色 蓝色分量
            Buff[BuffPos++] = (byte) ((textContext.getTextColor() >> 16) & 0xff);
            //32位字体颜色 保留字节
            Buff[BuffPos++] = (byte) ((textContext.getTextColor() >> 24) & 0xff);

            //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936
            byte[] TextBuff = textContext.getText().getBytes("GBK");
            //整包长度不能大于255
            if ((BuffPos + TextBuff.length) >= 255) {
                return null;
            }
            //文本长度
            Buff[BuffPos++] = (byte) TextBuff.length;
            //复制文本到缓冲
            for (int z = 0; z < TextBuff.length; z++) {
                Buff[BuffPos++] = TextBuff[z];
            }

            //添加文本分隔符
            if (i == (TextContextNum - 1)) {
                Buff[BuffPos++] = 0x00;
            } else {
                Buff[BuffPos++] = 0x0D;
            }
            i++;
        }

        /*2.填充语音参数*/
        //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936
        byte[] VoiceTextBuff = VoiceText.getBytes("GBK");
        if (VoiceTextBuff.length > 0) {
            //语音分隔符
            Buff[BuffPos++] = 0x0A;
            //语音文本长度
            Buff[BuffPos++] = (byte) VoiceTextBuff.length;
            //长度检查
            if ((BuffPos + VoiceTextBuff.length) >= 255) {
                return null;
            }
            //复制文本到缓冲
            for (int z = 0; z < VoiceTextBuff.length; z++) {
                Buff[BuffPos++] = VoiceTextBuff[z];
            }
        } else {
            Buff[BuffPos++] = 0x00;
        }
        Buff[BuffPos++] = 0;
        //重新修改数据长度
        Buff[5] = (byte) (BuffPos - 6);

        /*3.计算校验码*/
        CRC = CRC16Util.getCRC3(Buff, BuffPos);
        //校验码低字节
        Buff[BuffPos++] = (byte) (CRC & 0xff);
        //校验码高字节
        Buff[BuffPos++] = (byte) ((CRC >> 8) & 0xff);

        byte[] copyByte = getCopyByte(Buff, BuffPos);

        return copyByte;

    }

    static byte[] LED_DisQR_V1(int ShowTime, String QRMsg, String TextInfo, int VoieceEn, byte QRSize) throws UnsupportedEncodingException {
        byte[] Buff = new byte[1024];//分配缓冲数组
        byte[] TextBuff = TextInfo.getBytes("GBK"); //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936
        byte[] QRMsgBuff = QRMsg.getBytes("GBK"); //把string 类型字符串 读取到字节数组里面.注意字符串编码必须是GB2312 WINDOWS代码页为936

        int Pos = 0;
        int TextLen = TextBuff.length; //获取字符串数组的长度
        int QRMsgLen = QRMsgBuff.length;
        int CRC;
        int PakDataLen;

        PakDataLen = TextLen + QRMsgLen + 34;
        if (PakDataLen > 255) {
            return null;
        }
        /*0.填充显示命令参数*/
        Buff[Pos++] = 0x00; //显示屏地址
        Buff[Pos++] = 0x64; //固定参数
        Buff[Pos++] = (byte) 0xFF; //包序列
        Buff[Pos++] = (byte) 0xFF; //包序列
        Buff[Pos++] = (byte) 0xE1; //指令
        Buff[Pos++] = (byte) (PakDataLen & 0xff); //数据长度

        Buff[Pos++] = 1; //显示标志
        Buff[Pos++] = 0; //界面进入模式
        Buff[Pos++] = 0; //界面退出模式
        Buff[Pos++] = (byte) ShowTime; //界面显示的时间
        Buff[Pos++] = 0; //下一界面的索引号
        Buff[Pos++] = 0; //保留字节停车时长
        Buff[Pos++] = 0; //保留字节停车时长
        Buff[Pos++] = 0; //保留字节停车时长
        Buff[Pos++] = 0; //保留字节停车时长
        Buff[Pos++] = 0; //保留字节收费金额
        Buff[Pos++] = 0; //保留字节收费金额
        Buff[Pos++] = 0; //保留字节收费金额
        Buff[Pos++] = 0; //保留字节收费金额
        Buff[Pos++] = (byte) (QRMsgLen & 0xff); //二维码消息长度
        Buff[Pos++] = (byte) (TextLen & 0xff); //文本长度
        Buff[Pos++] = (byte) (0x80 | VoieceEn); //播报语音标志
        Buff[Pos++] = QRSize; //二维码尺寸
        /*保留的15个字节*/
        for (int i = 0; i < 15; i++) {
            Buff[Pos++] = 0;
        }
        /*复制二维码内容到缓冲区*/
        for (int i = 0; i < QRMsgLen; i++) {
            Buff[Pos++] = QRMsgBuff[i];
        }
        Buff[Pos++] = 0;//结束符

        /*复制文本内容到缓冲区*/
        for (int i = 0; i < TextLen; i++) {
            Buff[Pos++] = TextBuff[i];
        }
        Buff[Pos++] = 0;//结束符

        /*1.计算校验码*/
        CRC = CRC16Util.getCRC3(Buff, Pos);
        Buff[Pos++] = (byte) (CRC & 0xff);//校验码低字节
        Buff[Pos++] = (byte) ((CRC >> 8) & 0xff);//校验码高字节

        byte[] copyByte = getCopyByte(Buff, Pos);

        return copyByte;
    }


    /**
     * 获取byte的实际值
     *
     * @param bytes
     * @return 实际长度的byte[]
     */
    public static byte[] getCopyByte(byte[] bytes, int BuffPos) {

        if (null == bytes || 0 == bytes.length) {
            return new byte[1];
        }
        int length = BuffPos;
        byte[] bb = new byte[length];
        System.arraycopy(bytes, 0, bb, 0, length);
        return bb;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //红色
    public static int LED_COLOR_RED = 0x000000FF;
    //绿色
    public static int LED_COLOR_GREEN = 0x0000FF00;
    //黄色
    public static int LED_COLOR_YEELOW = 0x0000FFFF;

}
