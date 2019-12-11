package html2pdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownBook {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String dirPath = "./html";
        String item = "index.html";
        while(item!=null&&item!=""){
            try {
             String html=   Util.downloadPage("https://i.linuxtoy.org/docs/guide/" + item, dirPath, item);
             item = getUrl(html);
            } catch (Exception ex) {

            }
        }
	}

	 static String getUrl(String text) {

	        Pattern pattern = Pattern.compile("<a accesskey=\"n\" href=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])\">ÏÂÒ»Ò³</a>");
	        Matcher m = pattern.matcher(text);
	        String str = "";
	        if (m.find()) {
	            str = m.group(1);
	        }
	        return str;

	    }
}
