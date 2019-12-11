# html2pdf
html转pdf
以 [开源世界旅行手册](https://i.linuxtoy.org/docs/guide/index.html "开源世界旅行手册") 这本书为例

1.第一步 从该网站爬取所有html网页
```java
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

	        Pattern pattern = Pattern.compile("<a accesskey=\"n\" href=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])\">下一页</a>");
	        Matcher m = pattern.matcher(text);
	        String str = "";
	        if (m.find()) {
	            str = m.group(1);
	        }
	        return str;

	    }
}
```
2.第二步，把html转换成pdf
这里用的是wkhtmltopdf软件，百度该软件下载安装
```java
public class ToPDF {
	public static void main(String[] args) {
		File rootPath = new File("./html");
		String pdfPath = "./pdf//";

		File[] files = rootPath.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
			}

			public boolean equals(Object obj) {
				return true;
			}

		});

		for (File f : files) {
			if (f.isFile() && f.getName().indexOf("html") != -1) {
				String fileName = f.getName();
				String pdfName = fileName.replace("html", "pdf");
				Util.convert("./html//" + fileName, pdfPath + pdfName);
				// Thread.sleep(500);
			}

		}

	}
}
```
3.将多个pdf合并成一个pdf文件
```java
public class PDFCombine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File rootPath = new File("./pdf");
        File[] files = rootPath.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        
        
        try{
            File f =Util. mulFile2One(Arrays.asList(files), "./开源世界旅行手册.pdf");
            System.out.println(f.length());
        }
        catch (Exception ex){

        }
	}

}
```
所用到的Util类
```java
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
```
