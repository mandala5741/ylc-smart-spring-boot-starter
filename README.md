
显示语音控制卡协议工具包
项目概述
本工具包是一个用于控制多种类型LED显示屏（包括单色屏、彩屏、小竖屏等）的Java工具类集合。支持显示文字内容、播放语音、显示二维码等功能，适用于停车场管理系统、商场导览等场景。

主要功能
1. 显示控制
   文本显示: 支持多行文字显示，可设置颜色、显示模式、速度等参数

临时显示: 支持设置显示时长，超时后自动取消

二维码显示: 支持生成和显示二维码

固定显示: 支持不滑动的定屏显示

2. 语音控制
   单条语音播报: 播放预定义的语音内容

组合语音播报: 支持多条语音连续播放

车牌语音播报: 支持车牌号与语音的组合播放

TTS语音: 支持文本转语音功能

3. 屏幕类型支持
   标准横屏: 4行显示，红绿黄三色

全彩屏: 支持8种颜色，可按字节或按行随机颜色

小竖屏: 4行紧凑显示

1模组竖屏（大P/小P）: 竖屏显示模式

2模组竖屏: 双模组竖屏

主要类说明
1. DisplayVoiceCardProtocol - 主协议工具类
   java
   // 支持所有屏幕类型的通用协议工具
   DisplayVoiceCardProtocol protocol = new DisplayVoiceCardProtocol(ScreenEnum.COLOR_SCREEN);

// 加载广告内容（自动根据屏幕类型选择合适协议）
byte[] packet = protocol.buildLoadAdsPacket(1, 8, "欢迎光临");

// 播放语音
byte[] voicePacket = protocol.buildPlayVoicePacket(1);
2. Rs485Util - 横屏显示工具类
   java
   // 构建入场场景
   String[] entryScene = Rs485Util.buildEntryScene("渝BS775W", 56);

// 构建二维码显示
String qrcodeCmd = Rs485Util.buildQrcodeCommand("https://example.com");

// TTS语音播报
String ttsCmd = Rs485Util.buildTTSVoiceCommand("欢迎光临");
3. Rs485CPUtil - 彩屏显示工具类
   java
   // 彩屏显示控制
   String[] display = Rs485CPUtil.buildSmartParkingDisplay(50);

// 彩屏语音播报
String voiceCmd = Rs485CPUtil.buildPlateWelcomeVoice("渝BS775W");
4. Rs485XSPUtil - 小竖屏显示工具类
   java
   // 小竖屏显示
   String cmd = Rs485XSPUtil.buildSmallScreenWelcome();

// 小竖屏完整场景
String[] scene = Rs485XSPUtil.buildSmallScreenEntryScene("渝BS775W", 48);
5. Rs485DisplayUtils - 通用显示工具类
   java
   // 智慧停车显示
   String[] display = Rs485DisplayUtils.buildSmartParkingDisplay(50, COLOR_RED);

// 语音播报
String voice = Rs485DisplayUtils.buildPleasePayVoice();
6. LED_ResponseUtil - 低层LED控制
   java
   // 播放语音
   byte[] voiceData = LED_ResponseUtil.LED_PlayVoice("欢迎光临");

// 显示文本
byte[] textData = LED_ResponseUtil.LED_DisText((byte)1, "欢迎光临", (byte)0, (byte)1, (byte)5, (byte)1);

// 多行显示+语音
byte[] multiData = LED_ResponseUtil.LED_MuiltLineDisAndPlayVoice(textContextList, "语音内容");
7. TextContext - 文本上下文
   java
   // 定义显示行参数
   TextContext context = new TextContext(
   (byte)1,    // 行号
   (byte)0,    // 显示模式
   (byte)5,    // 停留时间
   (byte)1,    // 显示次数
   0x000000FF, // 文本颜色（红色）
   "欢迎光临"   // 文本内容
   );
8. CRC16Util - CRC校验工具
   java
   // 计算CRC16校验码
   int crc = CRC16Util.getCRC3(dataBytes, dataLength);
9. UrlEncoderUtil - URL编码工具
   java
   // URL参数编码
   String encodedUrl = UrlEncoderUtil.encodeUrlParams("https://example.com?carlicense=渝BS775W");
   快速开始
1. 基础使用
   java
   // 创建协议实例（默认标准横屏）
   DisplayVoiceCardProtocol protocol = new DisplayVoiceCardProtocol();

// 加载广告内容
byte[] packet = protocol.buildLoadAdsPacket(1, 1, "欢迎光临");

// 转换为十六进制字符串
String hexStr = DisplayVoiceCardProtocol.bytesToHex(packet);
2. 停车场入场场景
   java
   String plateNumber = "渝BS775W";
   int parkingSpace = 48;

// 使用Rs485Util构建入场场景
String[] entryScene = Rs485Util.buildEntryScene(plateNumber, parkingSpace);

// 生成JSON格式命令
String jsonCommand = Rs485Util.buildRs485Json(entryScene);
3. 停车场出场场景
   java
   String plateNumber = "渝BS775W";
   String amount = "15";

// 使用Rs485Util构建出场场景
String[] exitScene = Rs485Util.buildExitScene(plateNumber, amount);

// 生成JSON格式命令
String jsonCommand = Rs485Util.buildRs485Json(exitScene);
4. 彩屏显示
   java
   // 创建彩屏协议实例
   DisplayVoiceCardProtocol colorScreen = new DisplayVoiceCardProtocol(ScreenEnum.COLOR_SCREEN);

// 彩屏广告内容
byte[] colorPacket = colorScreen.buildLoadAdsPacket(1, 8, "欢迎光临");

// 彩屏专用指令
byte[] animationPacket = colorScreen.buildSetAnimationPacket(true);
屏幕类型枚举
java
public enum ScreenEnum {
MODULE_VERTICAL_LARGE_P(1, "1模组竖屏_大P"),
MODULE_VERTICAL_SMALL_P(2, "1模组竖屏_小P"),
MODULE_VERTICAL(3, "2模组_竖屏"),
STANDARD_HORIZONTAL(4, "标准横屏_式样"),
COLOR_SCREEN(5, "全彩屏_款式_式样jpg"),
SMALL_VERTICAL(6, "小竖屏");
}
颜色定义
标准屏颜色
1: 红色

2: 绿色

3: 黄色

彩屏颜色
0: 按字节随机

1-7: 具体颜色

8: 按行随机

命令列表
命令码	说明	支持的屏幕类型
0x01	查询版本	所有
0x10	设置时间	所有
0x22	播放语音	所有
0x25	加载广告内容	标准屏
0x27	下发临时显示	标准屏
0x28	加载二维码	所有
0x35	彩屏加载广告	彩屏
0x37	彩屏下发临时显示	彩屏
0xF0	设置音量	所有
0xF9	设置动画	彩屏
使用示例
示例1：完整入场流程
java
// 1. 设置屏幕类型
DisplayVoiceCardProtocol screen = new DisplayVoiceCardProtocol(ScreenEnum.STANDARD_HORIZONTAL);

// 2. 构建显示命令
String plateNumber = "渝BS775W";
int parkingSpace = 48;

// 第一行：欢迎光临
String line1 = Rs485Util.buildLoadAdCommand(1, COLOR_RED, "欢迎光临");

// 第二行：车牌号
String line2 = Rs485Util.buildLoadAdCommand(2, COLOR_RED, plateNumber);

// 第三行：提示语
String line3 = Rs485Util.buildLoadAdCommand(3, COLOR_RED, "一车一杆自动识别");

// 第四行：车位信息
String line4 = Rs485Util.buildLoadAdCommand(4, COLOR_RED, "剩余车位" + parkingSpace + "个");

// 3. 构建语音命令
String voice = Rs485Util.buildPlayVoiceCommand(plateNumber + "\u0001");

// 4. 生成JSON
String[] commands = {line1, line2, line3, line4, voice};
String json = Rs485Util.buildRs485Json(commands);
示例2：二维码支付场景
java
// 生成支付二维码
String payUrl = "https://tcc.tlgdiot.cn/wechat/pages/pay/index?carlicense=渝BS775W";
String qrcodeCmd = Rs485Util.buildQrcodeCommand(payUrl);

// 同时显示支付提示
String promptCmd = Rs485Util.buildLoadAdCommand(1, COLOR_GREEN, "请扫码支付");

// 生成JSON
String[] commands = {promptCmd, qrcodeCmd};
String json = Rs485Util.buildRs485Json(commands);
注意事项
编码问题: 中文字符请使用GBK编码

行号范围: 标准屏为1-4行，彩屏也为1-4行

颜色值: 不同屏幕类型的颜色值范围不同

CRC校验: 所有数据包都包含CRC16校验

流水号: 每个数据包有自增的流水号，避免重复

扩展开发
添加新的屏幕类型
在ScreenEnum中添加新的枚举值

在DisplayVoiceCardProtocol中添加对应的处理方法

根据需要创建专用的工具类

自定义显示格式
可以通过修改TextContext参数来调整显示效果：

DisMode: 显示模式（0-立即显示，1-从右向左移动等）

DelayTime: 停留时间

DisTimes: 显示次数

TextColor: 文本颜色

故障排除
常见问题
显示乱码: 检查字符串编码是否为GBK

命令无效: 检查屏幕类型是否匹配

CRC校验失败: 检查数据包长度和计算方式

语音不播放: 检查语音索引是否正确

调试方法
java
// 启用调试输出
String hexStr = DisplayVoiceCardProtocol.bytesToHex(packet);
System.out.println("数据包: " + hexStr);

// 验证CRC
byte[] crc = DisplayVoiceCardProtocol.calculateCRC16(data);
System.out.println("CRC校验: " + DisplayVoiceCardProtocol.bytesToHex(crc));
许可证
本项目代码为专用工具类，具体使用请参考相关协议。

技术支持
如有问题或建议，请联系开发团队。