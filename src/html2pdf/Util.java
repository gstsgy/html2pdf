package html2pdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Util {
	public static String downloadPage(String u, String mkd,String title1) throws UnsupportedEncodingException, IOException {
        URL url = new URL(u);
        String uri = url.getHost();
        System.out.println("URI:" + uri);
        Document doc = Jsoup.connect(u).get();
        Elements ele = doc.getElementsByTag("title");
        String title = ele.text();
        System.out.println("url：" + u);
        System.out.println("网页标题：" + title);
        File mk = new File(mkd);
        if (mk.exists()) {
            System.out.println("目录" + mk + "已经存在");
        } else {
            mk.mkdir();
            System.out.println("创建目录" + mk + "成功");
        }
        System.out.println("MK:" + mk);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf8"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(mk + "/" + title1)), "utf8"));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
            }
            br.close();
            bw.close();
            System.out.println("下载完毕！");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
       return doc.outerHtml();
    }

    private static final String toPdfTool = "D:\\Program Files\\Wkhtmltopdf\\bin\\wkhtmltopdf.exe";

    /**
     * html转pdf
     * @param srcPath html路径，可以是硬盘上的路径，也可以是网络路径
     * @param destPath pdf保存路径
     * @return 转换成功返回true
     */
    public static boolean convert(String srcPath, String destPath){
        File file = new File(destPath);
        File parent = file.getParentFile();
        //如果pdf保存路径不存在，则创建路径
        if(!parent.exists()){
            parent.mkdirs();
        }

        StringBuilder cmd = new StringBuilder();
        cmd.append(toPdfTool);
        cmd.append(" ");
        cmd.append("  --header-line");//页眉下面的线
        cmd.append("  --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
        //cmd.append("  --margin-top 30mm ");//设置页面上边距 (default 10mm)
        cmd.append(" --header-spacing 10 ");//    (设置页眉和内容的距离,默认0)
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);

        boolean result = true;
        try{
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new Util().new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output =new Util(). new HtmlToPdfInterceptor(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }

        return result;
    }


    public static File mulFile2One(List<File> files, String targetPath) throws IOException{
        // pdf合并工具类
         PDFMergerUtility mergePdf = new PDFMergerUtility();
         for (File f : files) {
             if(f.exists() && f.isFile()){
                 // 循环添加要合并的pdf
                 mergePdf.addSource(f);
             }
         }        // 设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(targetPath);        // 合并pdf
         mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
         return new File(targetPath);    
         }

    
    
    public class HtmlToPdfInterceptor extends Thread {
        private InputStream is;

        public HtmlToPdfInterceptor(InputStream is){
            this.is = is;
        }

        public void run(){
            try{
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line.toString()); //输出内容
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
