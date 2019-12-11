package html2pdf;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
