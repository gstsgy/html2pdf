package html2pdf;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
