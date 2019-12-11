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
        System.out.println("url��" + u);
        System.out.println("��ҳ���⣺" + title);
        File mk = new File(mkd);
        if (mk.exists()) {
            System.out.println("Ŀ¼" + mk + "�Ѿ�����");
        } else {
            mk.mkdir();
            System.out.println("����Ŀ¼" + mk + "�ɹ�");
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
            System.out.println("������ϣ�");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
       return doc.outerHtml();
    }

    private static final String toPdfTool = "D:\\Program Files\\Wkhtmltopdf\\bin\\wkhtmltopdf.exe";

    /**
     * htmlתpdf
     * @param srcPath html·����������Ӳ���ϵ�·����Ҳ����������·��
     * @param destPath pdf����·��
     * @return ת���ɹ�����true
     */
    public static boolean convert(String srcPath, String destPath){
        File file = new File(destPath);
        File parent = file.getParentFile();
        //���pdf����·�������ڣ��򴴽�·��
        if(!parent.exists()){
            parent.mkdirs();
        }

        StringBuilder cmd = new StringBuilder();
        cmd.append(toPdfTool);
        cmd.append(" ");
        cmd.append("  --header-line");//ҳü�������
        cmd.append("  --header-center ������ҳü������ҳü������ҳü������ҳü ");//ҳü�м�����
        //cmd.append("  --margin-top 30mm ");//����ҳ���ϱ߾� (default 10mm)
        cmd.append(" --header-spacing 10 ");//    (����ҳü�����ݵľ���,Ĭ��0)
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
        // pdf�ϲ�������
         PDFMergerUtility mergePdf = new PDFMergerUtility();
         for (File f : files) {
             if(f.exists() && f.isFile()){
                 // ѭ�����Ҫ�ϲ���pdf
                 mergePdf.addSource(f);
             }
         }        // ���úϲ�����pdf�ļ�����
        mergePdf.setDestinationFileName(targetPath);        // �ϲ�pdf
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
                    System.out.println(line.toString()); //�������
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
