package com.syh.screenadapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author shenyonghe
 * 
 *         2015-12-8
 */
public class UnZipUtil {

	/**
	 * 解压到指定目录
	 * 
	 * @param zipPath 
	 * @param descDir
	 * @author isea533
	 */
	public static void unZipFiles(final String zipPath, final String descDir)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					unZipFiles(new File(zipPath), descDir);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 解压文件到指定目录
	 * 
	 * @param zipFile 解压文件
	 * @param descDir 解压后的目的目录
	 * @author isea533
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(final File zipFile, final String descDir)
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				File pathFile = new File(descDir);
				if (!pathFile.exists())
				{
					pathFile.mkdirs();
				}
				try
				{
					ZipFile zip = new ZipFile(zipFile);
					for (Enumeration entries = zip.entries(); entries
							.hasMoreElements();)
					{
						ZipEntry entry = (ZipEntry) entries.nextElement();
						String zipEntryName = entry.getName();
						InputStream in = zip.getInputStream(entry);
						String outPath = (descDir + zipEntryName).replaceAll(
								"\\*", "/");
						;
						// 判断路径是否存在,不存在则创建文件路径
						File file = new File(outPath.substring(0,
								outPath.lastIndexOf('/')));
						if (!file.exists())
						{
							file.mkdirs();
						}
						// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
						if (new File(outPath).isDirectory())
						{
							continue;
						}
						// 输出文件路径信息
						System.out.println(outPath);

						OutputStream out = new FileOutputStream(outPath);
						byte[] buf1 = new byte[1024];
						int len;
						while ((len = in.read(buf1)) > 0)
						{
							out.write(buf1, 0, len);
						}
						in.close();
						out.close();
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				System.out
						.println("******************解压完毕********************");
			}
		}).start();

	}

	public static void main(String[] args)
	{
		/**
		 * 解压文件
		 */
		final File zipFile = new File("core.zip");
		final String path = "/Users/yongheshen/Desktop/";
		unZipFiles(zipFile, path);
	}

}
