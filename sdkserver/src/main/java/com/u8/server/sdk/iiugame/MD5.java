package com.u8.server.sdk.iiugame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * @author Linlg
 *
 */
public class MD5 {   
	//各轮左移位数
    static final int S11 = 7;   
    static final int S12 = 12;   
    static final int S13 = 17;   
    static final int S14 = 22;   
  
    static final int S21 = 5;   
    static final int S22 = 9;   
    static final int S23 = 14;   
    static final int S24 = 20;   
  
    static final int S31 = 4;   
    static final int S32 = 11;   
    static final int S33 = 16;   
    static final int S34 = 23;   
  
    static final int S41 = 6;   
    static final int S42 = 10;   
    static final int S43 = 15;   
    static final int S44 = 21;   
  

  
//用于bits填充的缓冲区  64字节
    static final byte[] PADDING = 
{ -128, 0, 0, 0, 0, 0, 0, 0, 0,   
     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,   
     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,   
     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };  
 
   
    private long[] state = new long[4]; //A  B C D 四个寄存器
    private long[] count = new long[2]; //存储原始信息的bits数长度,不包括填充的bits，最长为 2^64 bits
    private byte[] buffer = new byte[64]; // 存放输入的信息的缓冲区，512 bits 
      
  //digestHexStr是MD5的唯一一个公共成员，是最新一次计算结果的16进制ASCII表示.  
    public String digestHexStr;   
      
   //digest,是最新一次计算结果的2进制内部表示，表示128bit的MD5值.  
    private byte[] digest = new byte[16];   
      
  
    // 构造函数,初始化数据
    public MD5() {   

          md5Init();    
    }   
    // md5Init是一个初始化函数，初始化核心变量
    private void md5Init() {   
          count[0] = 0L;   
          count[1] = 0L;   
           
  //初始化四个寄存器的初值
          state[0] = 0x67452301L;   
          state[1] = 0xefcdab89L;   
          state[2] = 0x98badcfeL;   
          state[3] = 0x10325476L;   
    }   
 
  //四轮的辅助函数
    private long F(long x, long y, long z) {   
          return (x & y) | ((~x) & z);   
  
    }   
    private long G(long x, long y, long z) {   
          return (x & z) | (y & (~z));   
  
    }   
    private long H(long x, long y, long z) {   
          return x ^ y ^ z;   
    }   
  
    private long I(long x, long y, long z) {   
          return y ^ (x | (~z));   
    }   
      

  //四轮的运算函数 x表示第x个分组， s表示循环左移s位  ac是给定的常数
    private long FF(long a, long b, long c, long d, long x, long s,   
          long ac) {   
          a += F (b, c, d) + x + ac;   
          a = ((int) a << s) | ((int) a >>> (32 - s));//左移s位  
          a += b;   
          return a;   
    }   
  
    private long GG(long a, long b, long c, long d, long x, long s,   
          long ac) {   
          a += G (b, c, d) + x + ac;   
          a = ((int) a << s) | ((int) a >>> (32 - s));   
          a += b;   
          return a;   
    }   
    private long HH(long a, long b, long c, long d, long x, long s,   
          long ac) {   
          a += H (b, c, d) + x + ac;   
          a = ((int) a << s) | ((int) a >>> (32 - s));   
          a += b;   
          return a;   
    }   
    private long II(long a, long b, long c, long d, long x, long s,   
          long ac) {   
          a += I (b, c, d) + x + ac;   
          a = ((int) a << s) | ((int) a >>> (32 - s));   
          a += b;   
          return a;   
    }   
    
   
/*
对512bits信息(即block缓冲区)进行一次处理，每次处理包括四轮
state[4]:用于保存对512bits信息加密的中间结果或者最终结果
block[64]:欲加密的512bits信息
*/
    private void md5Transform (byte block[]) { 
          long a = state[0], b = state[1], c = state[2], d = state[3];  //state[4]用于保存对512bits信息加密的中间结果或者最终结果
          long[] x = new long[16];//低32位存放一个字(每块512比特分为16个字)，即32位
          Decode (x, block, 64);  //将分块的原始字节Block（byte型，要加密的512比特信息）转化为long型，长度为64
  
          /* 第一轮 */  
          a = FF (a, b, c, d, x[0], S11, 0xd76aa478L); /* 1 */  
          d = FF (d, a, b, c, x[1], S12, 0xe8c7b756L); /* 2 */  
          c = FF (c, d, a, b, x[2], S13, 0x242070dbL); /* 3 */  
          b = FF (b, c, d, a, x[3], S14, 0xc1bdceeeL); /* 4 */  
          a = FF (a, b, c, d, x[4], S11, 0xf57c0fafL); /* 5 */  
          d = FF (d, a, b, c, x[5], S12, 0x4787c62aL); /* 6 */  
          c = FF (c, d, a, b, x[6], S13, 0xa8304613L); /* 7 */  
          b = FF (b, c, d, a, x[7], S14, 0xfd469501L); /* 8 */  
          a = FF (a, b, c, d, x[8], S11, 0x698098d8L); /* 9 */  
          d = FF (d, a, b, c, x[9], S12, 0x8b44f7afL); /* 10 */  
          c = FF (c, d, a, b, x[10], S13, 0xffff5bb1L); /* 11 */  
          b = FF (b, c, d, a, x[11], S14, 0x895cd7beL); /* 12 */  
          a = FF (a, b, c, d, x[12], S11, 0x6b901122L); /* 13 */  
          d = FF (d, a, b, c, x[13], S12, 0xfd987193L); /* 14 */  
          c = FF (c, d, a, b, x[14], S13, 0xa679438eL); /* 15 */  
          b = FF (b, c, d, a, x[15], S14, 0x49b40821L); /* 16 */  
  
          /* 第二轮 */  
          a = GG (a, b, c, d, x[1], S21, 0xf61e2562L); /* 17 */  
          d = GG (d, a, b, c, x[6], S22, 0xc040b340L); /* 18 */  
          c = GG (c, d, a, b, x[11], S23, 0x265e5a51L); /* 19 */  
          b = GG (b, c, d, a, x[0], S24, 0xe9b6c7aaL); /* 20 */  
          a = GG (a, b, c, d, x[5], S21, 0xd62f105dL); /* 21 */  
          d = GG (d, a, b, c, x[10], S22, 0x2441453L); /* 22 */  
          c = GG (c, d, a, b, x[15], S23, 0xd8a1e681L); /* 23 */  
          b = GG (b, c, d, a, x[4], S24, 0xe7d3fbc8L); /* 24 */  
          a = GG (a, b, c, d, x[9], S21, 0x21e1cde6L); /* 25 */  
          d = GG (d, a, b, c, x[14], S22, 0xc33707d6L); /* 26 */  
          c = GG (c, d, a, b, x[3], S23, 0xf4d50d87L); /* 27 */  
          b = GG (b, c, d, a, x[8], S24, 0x455a14edL); /* 28 */  
          a = GG (a, b, c, d, x[13], S21, 0xa9e3e905L); /* 29 */  
          d = GG (d, a, b, c, x[2], S22, 0xfcefa3f8L); /* 30 */  
          c = GG (c, d, a, b, x[7], S23, 0x676f02d9L); /* 31 */  
          b = GG (b, c, d, a, x[12], S24, 0x8d2a4c8aL); /* 32 */  
  
          /* 第三轮 */  
          a = HH (a, b, c, d, x[5], S31, 0xfffa3942L); /* 33 */  
          d = HH (d, a, b, c, x[8], S32, 0x8771f681L); /* 34 */  
          c = HH (c, d, a, b, x[11], S33, 0x6d9d6122L); /* 35 */  
          b = HH (b, c, d, a, x[14], S34, 0xfde5380cL); /* 36 */  
          a = HH (a, b, c, d, x[1], S31, 0xa4beea44L); /* 37 */  
          d = HH (d, a, b, c, x[4], S32, 0x4bdecfa9L); /* 38 */  
          c = HH (c, d, a, b, x[7], S33, 0xf6bb4b60L); /* 39 */  
          b = HH (b, c, d, a, x[10], S34, 0xbebfbc70L); /* 40 */  
          a = HH (a, b, c, d, x[13], S31, 0x289b7ec6L); /* 41 */  
          d = HH (d, a, b, c, x[0], S32, 0xeaa127faL); /* 42 */  
          c = HH (c, d, a, b, x[3], S33, 0xd4ef3085L); /* 43 */  
          b = HH (b, c, d, a, x[6], S34, 0x4881d05L); /* 44 */  
          a = HH (a, b, c, d, x[9], S31, 0xd9d4d039L); /* 45 */  
          d = HH (d, a, b, c, x[12], S32, 0xe6db99e5L); /* 46 */  
          c = HH (c, d, a, b, x[15], S33, 0x1fa27cf8L); /* 47 */  
          b = HH (b, c, d, a, x[2], S34, 0xc4ac5665L); /* 48 */  
  
          /* 第四轮 */  
          a = II (a, b, c, d, x[0], S41, 0xf4292244L); /* 49 */  
          d = II (d, a, b, c, x[7], S42, 0x432aff97L); /* 50 */  
          c = II (c, d, a, b, x[14], S43, 0xab9423a7L); /* 51 */  
          b = II (b, c, d, a, x[5], S44, 0xfc93a039L); /* 52 */  
          a = II (a, b, c, d, x[12], S41, 0x655b59c3L); /* 53 */  
          d = II (d, a, b, c, x[3], S42, 0x8f0ccc92L); /* 54 */  
          c = II (c, d, a, b, x[10], S43, 0xffeff47dL); /* 55 */  
          b = II (b, c, d, a, x[1], S44, 0x85845dd1L); /* 56 */  
          a = II (a, b, c, d, x[8], S41, 0x6fa87e4fL); /* 57 */  
          d = II (d, a, b, c, x[15], S42, 0xfe2ce6e0L); /* 58 */  
          c = II (c, d, a, b, x[6], S43, 0xa3014314L); /* 59 */  
          b = II (b, c, d, a, x[13], S44, 0x4e0811a1L); /* 60 */  
          a = II (a, b, c, d, x[4], S41, 0xf7537e82L); /* 61 */  
          d = II (d, a, b, c, x[11], S42, 0xbd3af235L); /* 62 */  
          c = II (c, d, a, b, x[2], S43, 0x2ad7d2bbL); /* 63 */  
          b = II (b, c, d, a, x[9], S44, 0xeb86d391L); /* 64 */  
  
          state[0] += a;   
          state[1] += b;   
          state[2] += c;   
          state[3] += d;   
  
    }   
      
    /*Encode把long数组按顺序拆成byte数组，因为java的long类型是64bit的，  
    只拆低32bit 
	将4字节的整数copy到字符形式的缓冲区中
	output：用于输出的字符缓冲区
	input：欲转换的四字节的整数形式的数组
	len：output缓冲区的长度，要求是4的整数倍
	*/
    private void Encode (byte[] output, long[] input, int len) {   
          int i, j;   
  
          for (i = 0, j = 0; j < len; i++, j += 4) {   
                output[j] = (byte)(input[i] & 0xffL);   
                output[j + 1] = (byte)((input[i] >>> 8) & 0xffL);   
                output[j + 2] = (byte)((input[i] >>> 16) & 0xffL);   
                output[j + 3] = (byte)((input[i] >>> 24) & 0xffL);   
          }   
    }   
  
    /*Decode把byte数组按顺序合成成long数组，因为java的long类型是64bit的，  
    只合成低32bit，高32bit清零
	output：保存转换出的整数
	input：欲转换的字符缓冲区
	len：输入的字符缓冲区的长度，要求是4的整数倍
	*/
    private void Decode (long[] output, byte[] input, int len) {   
          int i, j;   
  
          for (i = 0, j = 0; j < len; i++, j += 4)   
                output[i] = b2iu(input[j]) |   
                    (b2iu(input[j + 1]) << 8) |   
                    (b2iu(input[j + 2]) << 16) |   
                    (b2iu(input[j + 3]) << 24);   
    }   
      
    /*  
      b2iu是一个把byte按照不考虑正负号的原则的＂升位＂程序，因为java没有unsigned运算  
    */  
    public static long b2iu(byte b) {   
          return b < 0 ? b & 0x7F + 128 : b;   //计算机中负数的二进制码是是负数的绝对值取反，然后加1.此处若为负数则取绝对值

    }   
      
   //此函数用来把一个byte类型的数转换成十六进制的ASCII表示，  
    public static String byteHEX(byte b) {   
          char[] Digit = { '0','1','2','3','4','5','6','7','8','9',   
          'A','B','C','D','E','F' };   
          char [] ob = new char[2];   
          ob[0] = Digit[(b >>> 4) & 0X0F];  //左移四位使高四位和第四位调换，高四位置0，得十六进制的高位（>>> 右移，左边空出的位以0填充）
          ob[1] = Digit[b & 0X0F];			//原数据的高四位置零，得到十六进制的低位
          String s = new String(ob);		//将十六进制的高位和低位拼接成十六进制数（字符串）
          return s;   
    }   


	 //md5Update是MD5的主计算过程，inbuf是要加密的字节串，inputlen是加密文件的长度
    private void md5Update(byte[] inbuf, int inputLen) {   
  
          int i, index, partLen;    //index表示不足512一组的字节数 PartLen表示凑齐64字节整数倍的欠缺字节数   
          byte[] block = new byte[64];   //每组512 bits

		 /*计算已有信息的bits长度的字节数的模64, 64bytes=512bits。
		 用于判断已有信息加上当前传过来的信息的总长度能不能达到512bits，
		 如果能够达到则对凑够的512bits进行一次处理
		 */

	     index = (int)(count[0] >>> 3) & 0x3F; //信息的比特数右移3位即除以8，得到比特数，模64得到不足64一组的比特数

         //更新已有信息的bits长度
          if ((count[0] += (inputLen << 3)) < (inputLen << 3)) //如果原有信息的比特数加上新来消息的比特数小于新消息的比特数  
                count[1]++;   //？？？？？？？？？
          count[1] += (inputLen >>> 29);   //????????????

         //计算最后已有的字节数长度还差多少字节可以 凑成64B的整倍数
          partLen = 64 - index;   
    
		  //如果当前输入的字节数大于欠缺的字节数长度，则补足64字节整倍数所差的字节数
          if (inputLen >= partLen) {   

			   //用当前输入的内容拷贝到buffer的内容补足512bits
                md5Memcpy(buffer, inbuf, index, 0, partLen); 

				//用基本函数对填充满的512bits（已经保存到buffer中）做一次转换，转换结果保存到state中
                md5Transform(buffer);

				 /*
			对当前输入的剩余字节做转换（如果剩余的字节<在输入的inbuft缓冲区中>大于512bits的话 ），
			转换结果保存到state中
				*/

                for (i = partLen; i + 63 < inputLen; i += 64) {   //64*8=512
  
                    md5Memcpy(block, inbuf, 0, i, 64);    
                    md5Transform (block);   
                }   
                index = 0;   
  
          } 
		  else  
  
                i = 0; 
				
		  //将输入缓冲区中的不足填充满512bits的剩余内容填充到buffer中，留待以后再作处理
          md5Memcpy(buffer, inbuf, index, i, inputLen - i);   
  
    }
	   
        
    /* md5Memcpy是一个内部使用的byte数组的块拷贝函数，从input的inpos开始把len长度的  
　　　　　 字节拷贝到output的outpos位置开始  
    */  
  
    private void md5Memcpy (byte[] output, byte[] input, int outpos, int inpos, int len)   
    {   
          int i;   
          for (i = 0; i < len; i++)   
                output[outpos + i] = input[inpos + i];   
    }   
      
  
     // md5Final整理和填写输出结果   
    private void md5Final () {   
          byte[] bits = new byte[8];   
          int index, padLen;   
  
          //将要被转换的信息(所有的)的bits长度拷贝到bits中，把long型数组count按顺序拆成byte数组bits
          Encode (bits, count, 8);   
  
          //计算所有的bits长度的字节数的模64, 64bytes=512bits 信息的比特数右移3位即除以8，得到比特数，模64得到不足64一组的比特数
          index = (int)(count[0] >>> 3) & 0x3f;   

		  /*计算需要填充的字节数，padLen的取值范围在1-64之间
		  */

		  //判断最后一组的是否小于448比特（56字节），若大于则此组填512-index*8 比特，下一组填充448比特，即共填充960-index*8 比特 ，即120-index 字节
          padLen = (index < 56) ? (56 - index) : (120 - index);  

          //这一次函数调用不会再导致MD5Transform的被调用，因为这一次不会填满512bits
          md5Update (PADDING, padLen);   
  
          //补上原始信息的bits长度，这一次能够恰巧凑够512bits，不会多也不会少
          md5Update(bits, 8);   
  
         //将最终的结果以十六进制保存到digest中
          Encode (digest, state, 16);  
  
    }   

 
    //调用加密函数，输入要加密的字符串即明文，得到密文
    public String getMD5ofStr(String inbuf) {   
          md5Init();   
          md5Update(inbuf.getBytes(), inbuf.length());    //对欲加密的字符进行加密
          md5Final();    //获得最终结果
          digestHexStr = "";   
          for (int i = 0; i < 16; i++) {   
                digestHexStr += byteHEX(digest[i]);   
          }   
          return digestHexStr.toLowerCase();   
  
    }

    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected static MessageDigest messagedigest = null;

    /**
     * MessageDigest初始化
     *
     * @author
     */
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5FileUtil messagedigest初始化失败");
            e.printStackTrace();
        }
    }

    /**
     * 对文件进行MD5加密
     *
     * @author
     */
    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 对字符串进行MD5加密
     *
     * @author
     */
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 对byte类型的数组进行MD5加密
     *
     * @author
     */
    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            char c0 = hexDigits[(bytes[l] & 0xf0) >> 4];
            char c1 = hexDigits[bytes[l] & 0xf];
            stringbuffer.append(c0);
            stringbuffer.append(c1);
        }
        return stringbuffer.toString();
    }
}
