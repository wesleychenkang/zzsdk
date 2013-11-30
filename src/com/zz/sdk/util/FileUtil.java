package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @功能: 文件的操作的工具类
 * @author yangting
 * @2013-8-13
 * @下午5:41:20
 * @version SocialClient 1.0.0
 */
public class FileUtil {

	public final static String FILE_EXTENSION_SEPARATOR = ".";

	/**
	 * 读取指定的文件的里的文本内容
	 * 
	 * @param filePath
	 * @return
	 */
	public static StringBuilder readFile(String filePath) {
		try {
			return readFile(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readFileByteArray(String filePath) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				arrayOutputStream.write(buffer, 0, length);
			}

			return arrayOutputStream.toByteArray();
		} catch (Exception e) {
		} finally {
			try {
				if (arrayOutputStream != null)
					arrayOutputStream.close();
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return new byte[0];
	}

	/**
	 * 读取指定的文件的里的文本内容
	 * 
	 * @param filePath
	 * @return
	 */
	public static StringBuilder readFile(InputStream stream) {
		StringBuilder fileContent = new StringBuilder("");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!fileContent.toString().equals("")) {
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
		return null;
	}

	/**
	 * 将字符串写入指定的文件的中
	 * 
	 * @param filePath
	 *            文件路径
	 * @param content
	 *            要写入的内容
	 * @param append
	 *            写入文件末尾处
	 * @return
	 */
	public static boolean writeFile(String filePath, String content, boolean append) {
		FileWriter fileWriter = null;
		try {
			makeFile(filePath);
			fileWriter = new FileWriter(filePath, append);
			fileWriter.write(content);
			fileWriter.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * 将指定的流写入指定的路径
	 * 
	 * @param filePath
	 *            要写入的文件路径
	 * @param stream
	 *            流
	 * @return
	 */
	public static boolean writeFile(String filePath, InputStream stream) {
		OutputStream o = null;
		try {
			// deleteFile(filePath);
			// 标识是临时文件
			File tempFile = new File(filePath + ".temp");
			if (!tempFile.exists())
				makeFile(tempFile.toString());
			o = new FileOutputStream(tempFile);
			byte data[] = new byte[1024];
			int length = -1;
			while ((length = stream.read(data)) != -1) {
				o.write(data, 0, length);
			}
			o.flush();
			// 下载完成后，更改后缀名
			return tempFile.renameTo(new File(filePath));
		} catch (FileNotFoundException e) {
			// throw new RuntimeException("FileNotFoundException occurred. ", e);
		} catch (IOException e) {
			// throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (o != null) {
				try {
					o.close();
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	/**
	 * 根据一个路径读取文件里的内容分行存入List中
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> readFileToList(String filePath) {
		File file = new File(filePath);
		List<String> fileContent = new ArrayList<String>();
		if (file != null && file.isFile()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					fileContent.add(line);
				}
				reader.close();
				return fileContent;
			} catch (IOException e) {
				throw new RuntimeException("IOException occurred. ", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeException("IOException occurred. ", e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据一个流里的内容分行存入List中
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> readInputStreamToList(InputStream input) {
		List<String> fileContent = new ArrayList<String>();
		if (input != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(input));
				String line = null;
				while ((line = reader.readLine()) != null) {
					fileContent.add(line);
				}
				reader.close();
				return fileContent;
			} catch (IOException e) {
				throw new RuntimeException("IOException occurred. ", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeException("IOException occurred. ", e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获得文件名(以文件的第一个.)
	 * 
	 * <pre>
	 *      getFileNameWithoutExtension(null)               =   null
	 *      getFileNameWithoutExtension("")                 =   ""
	 *      getFileNameWithoutExtension("   ")              =   "   "
	 *      getFileNameWithoutExtension("abc")              =   "abc"
	 *      getFileNameWithoutExtension("a.mp3")            =   "a"
	 *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
	 *      getFileNameWithoutExtension("c:\\")              =   ""
	 *      getFileNameWithoutExtension("c:\\a")             =   "a"
	 *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
	 *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
	 *      getFileNameWithoutExtension("/home/admin")      =   "admin"
	 *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, not include suffix
	 * @see
	 */
	public static String getFileNameWithoutExtension(String filePath) {

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1) {
			return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
		} else {
			if (extenPosi == -1) {
				return filePath.substring(filePosi + 1);
			} else {
				return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
			}
		}
	}

	/**
	 * 获得文件名(以文件的最后的.)
	 * 
	 * <pre>
	 *      getFileName(null)               =   null
	 *      getFileName("")                 =   ""
	 *      getFileName("   ")              =   "   "
	 *      getFileName("a.mp3")            =   "a.mp3"
	 *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
	 *      getFileName("abc")              =   "abc"
	 *      getFileName("c:\\")              =   ""
	 *      getFileName("c:\\a")             =   "a"
	 *      getFileName("c:\\a.b")           =   "a.b"
	 *      getFileName("c:a.txt\\a")        =   "a"
	 *      getFileName("/home/admin")      =   "admin"
	 *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, include suffix
	 */
	public static String getFileName(String filePath) {

		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1) {
			return filePath;
		}
		return filePath.substring(filePosi + 1);
	}

	/**
	 * 根据路径获取上一级的目录文件夹
	 * 
	 * <pre>
	 *      getFolderName(null)               =   null
	 *      getFolderName("")                 =   ""
	 *      getFolderName("   ")              =   ""
	 *      getFolderName("a.mp3")            =   ""
	 *      getFolderName("a.b.rmvb")         =   ""
	 *      getFolderName("abc")              =   ""
	 *      getFolderName("c:\\")              =   "c:"
	 *      getFolderName("c:\\a")             =   "c:"
	 *      getFolderName("c:\\a.b")           =   "c:"
	 *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
	 *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
	 *      getFolderName("/home/admin")      =   "/home"
	 *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFolderName(String filePath) {

		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1) {
			return "";
		}
		return filePath.substring(0, filePosi);
	}

	/**
	 * 得到的文件后缀从路径
	 * 
	 * <pre>
	 *      getFileExtension(null)               =   ""
	 *      getFileExtension("")                 =   ""
	 *      getFileExtension("   ")              =   "   "
	 *      getFileExtension("a.mp3")            =   "mp3"
	 *      getFileExtension("a.b.rmvb")         =   "rmvb"
	 *      getFileExtension("abc")              =   ""
	 *      getFileExtension("c:\\")              =   ""
	 *      getFileExtension("c:\\a")             =   ""
	 *      getFileExtension("c:\\a.b")           =   "b"
	 *      getFileExtension("c:a.txt\\a")        =   ""
	 *      getFileExtension("/home/admin")      =   ""
	 *      getFileExtension("/home/admin/a.txt/b")  =   ""
	 *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileExtension(String filePath) {

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (extenPosi == -1) {
			return "";
		} else {
			if (filePosi >= extenPosi) {
				return "";
			}
			return filePath.substring(extenPosi + 1);
		}
	}

	/**
	 * Creates the directory named by the trailing filename of this file, including the complete directory path required to create this directory. <br/>
	 * <br/>
	 * <ul>
	 * <strong>Attentions�?/strong>
	 * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
	 * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
	 * </ul>
	 * 
	 * @param filePath
	 * @return true if the necessary directories have been created or the target directory already exists, false one of the directories can not be created.
	 *         <ul>
	 *         <li>if {@link FileUtil#getFolderName(String)} return null, return false</li>
	 *         <li>if target directory already exists, return true</li>
	 *         <li>return {@link java.io.File#makeFolder}</li>
	 *         </ul>
	 */
	public static boolean makeDirs(String filePath) {

		File folder = new File(filePath);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	/**
	 * 创建一个文件夹
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean makeFolders(String filePath) {
		return makeDirs(filePath);
	}

	/**
	 * 判读一个路径是否是文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * 判断一个路径是否是文件夹
	 * 
	 * @param directoryPath
	 * @return
	 */
	public static boolean isFolderExist(String directoryPath) {
		File dire = new File(directoryPath);
		return (dire.exists() && dire.isDirectory());
	}

	/**
	 * 删除一个文件或文件夹
	 * 
	 * @param path
	 *            要删除的路径
	 * @return
	 */
	public static boolean deleteFile(String path) {

		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				return file.delete();
			} else if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (f.isFile()) {
						f.delete();
					} else if (f.isDirectory()) {
						deleteFile(f.getAbsolutePath());
					}
				}
				return file.delete();
			}
			return false;
		}
		return true;
	}

	/**
	 * 获取一个文件的大小
	 * 
	 * @param path
	 * @return 长度以字节为单位
	 */
	public static long getFileSize(String path) {
		File file = new File(path);
		return (file.exists() && file.isFile() ? file.length() : -1);
	}

	/**
	 * 创建文件
	 * 
	 * @param absFilePath
	 * @return
	 */
	public static boolean makeFile(String absFilePath) {
		try {

			String newFileDirPath = getFolderName(absFilePath);
			makeDirs(newFileDirPath);

			return new File(absFilePath).createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
