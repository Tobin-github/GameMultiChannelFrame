package com.u8.server.utils;

/**
 * @author kris
 * @create 2018-01-10 11:54
 * 订单号压缩工具类
 */
public class U8OrderIDHexUtils {
    private static final long RADIX = 70L;


    /**
     * 将long类型的订单号生成不超过11位的字符串
     * @param ten
     * @return
     */
    public static String encode(long ten){

        long divVal;
        long resVal;
        String hex = "";

        do {
            divVal = ten / RADIX;
            resVal = ten % RADIX;
            hex = encodeSingle((int) resVal) + hex;
            ten = divVal;
        }while (ten >= RADIX);

        if(ten != 0){
            hex = encodeSingle((int) ten) + hex;
        }

        return hex;
    }

    /**
     * 将11位字符串形式的订单号还原成纯数字的订单号
     * @param hex
     * @return
     */
    public static long decode(String hex){

        if(hex == null || hex.length() == 0) return 0;

        long t = 0L;
        for(int i=0; i<hex.length(); i++){

            char c = hex.charAt(i);
            long ten = decodeSingle(c);
            t = ten * (long)Math.pow(RADIX, hex.length()-i-1) + t;

        }

        return t;

    }

    private static String encodeSingle(int ten)
    {
        switch (ten)
        {
            case 0: return "0";
            case 1: return "1";
            case 2: return "2";
            case 3: return "3";
            case 4: return "4";
            case 5: return "5";
            case 6: return "6";
            case 7: return "7";
            case 8: return "8";
            case 9: return "9";
            case 10: return "a";
            case 11: return "b";
            case 12: return "c";
            case 13: return "d";
            case 14: return "e";
            case 15: return "f";
            case 16: return "g";
            case 17: return "h";
            case 18: return "i";
            case 19: return "j";
            case 20: return "k";
            case 21: return "l";
            case 22: return "m";
            case 23: return "n";
            case 24: return "o";
            case 25: return "p";
            case 26: return "q";
            case 27: return "r";
            case 28: return "s";
            case 29: return "t";
            case 30: return "u";
            case 31: return "v";
            case 32: return "w";
            case 33: return "x";
            case 34: return "y";
            case 35: return "z";
            case 36: return "A";
            case 37: return "B";
            case 38: return "C";
            case 39: return "D";
            case 40: return "E";
            case 41: return "F";
            case 42: return "G";
            case 43: return "H";
            case 44: return "I";
            case 45: return "J";
            case 46: return "K";
            case 47: return "L";
            case 48: return "M";
            case 49: return "N";
            case 50: return "O";
            case 51: return "P";
            case 52: return "Q";
            case 53: return "R";
            case 54: return "S";
            case 55: return "T";
            case 56: return "U";
            case 57: return "V";
            case 58: return "W";
            case 59: return "X";
            case 60: return "Y";
            case 61: return "Z";
            case 62: return "*";
            case 63: return "?";
            case 64: return "^";
            case 65: return "@";
            case 66: return "=";
            case 67: return "(";
            case 68: return "%";
            case 69: return "!";
            default: return "";
        }
    }


    private static int decodeSingle(char c)
    {
        switch (c)
        {
            case '0':return 0;
            case '1':return 1;
            case '2':return 2;
            case '3':return 3;
            case '4':return 4;
            case '5':return 5;
            case '6':return 6;
            case '7':return 7;
            case '8':return 8;
            case '9':return 9;
            case 'a':return 10;
            case 'b':return 11;
            case 'c':return 12;
            case 'd':return 13;
            case 'e':return 14;
            case 'f':return 15;
            case 'g':return 16;
            case 'h':return 17;
            case 'i':return 18;
            case 'j':return 19;
            case 'k':return 20;
            case 'l':return 21;
            case 'm':return 22;
            case 'n':return 23;
            case 'o':return 24;
            case 'p':return 25;
            case 'q':return 26;
            case 'r':return 27;
            case 's':return 28;
            case 't':return 29;
            case 'u':return 30;
            case 'v':return 31;
            case 'w':return 32;
            case 'x':return 33;
            case 'y':return 34;
            case 'z':return 35;
            case 'A': return 36;
            case 'B': return 37;
            case 'C': return 38;
            case 'D': return 39;
            case 'E': return 40;
            case 'F': return 41;
            case 'G': return 42;
            case 'H': return 43;
            case 'I': return 44;
            case 'J': return 45;
            case 'K': return 46;
            case 'L': return 47;
            case 'M': return 48;
            case 'N': return 49;
            case 'O': return 50;
            case 'P': return 51;
            case 'Q': return 52;
            case 'R': return 53;
            case 'S': return 54;
            case 'T': return 55;
            case 'U': return 56;
            case 'V': return 57;
            case 'W': return 58;
            case 'X': return 59;
            case 'Y': return 60;
            case 'Z': return 61;
            case '*': return 62;
            case '?': return 63;
            case '^': return 64;
            case '@': return 65;
            case '=': return 66;
            case '(': return 67;
            case '%': return 68;
            case '!': return 69;
            default: return 0;

        }
    }
}
