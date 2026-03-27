public class AesCheck {
  public static void main(String[] args) {
    String s = com.shanzhu.beadhouse.common.util.AesUtil.aesDecode(args[0]);
    System.out.println(s == null ? "null" : s);
    System.out.println(com.shanzhu.beadhouse.common.util.AesUtil.aesEncode("123456"));
  }
}
