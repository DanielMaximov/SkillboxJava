import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileAccess {

    private static FileSystem hdfs;
    private Path path;

    /**
     * Initializes the class, using rootPath as "/" directory
     *
     * @param containerName - the path to the root of HDFS,
     *                      for example, hdfs://localhost:32771
     */
    public FileAccess(String containerName, String port) throws URISyntaxException, IOException {
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname", "true");
        System.setProperty("HADOOP_USER_NAME", "root");
        hdfs = FileSystem.get(new URI("hdfs://localhost:8020"), configuration);
        path = new Path("hdfs://" + containerName + ":" + port + "/");
    }

    /**
     * Creates empty file or directory
     *
     * @param path
     */
    public String create(String path) throws IOException {
        FSDataOutputStream dataOutputStream = hdfs.create(new Path(path));
        return "success load, size: " + dataOutputStream.size();
    }

    /**
     * Appends content to the file
     *
     * @param path
     * @param content
     */
    public void append(String path, String content) throws IOException {
        FSDataOutputStream fileOutputStream = null;
        if (hdfs.exists(new Path(path))) {
            log.info("write process start into {}", path);
            fileOutputStream = hdfs.append(new Path(path));
            fileOutputStream.writeBytes(content);
            log.info("write process finish");
        } else {
            log.info("create new file because does not exist yet", path);
            fileOutputStream = hdfs.create(new Path(path));
            fileOutputStream.writeBytes(content);
            log.info("write process finish");
        }
    }

    /**
     * Returns content of the file
     *
     * @param path
     * @return
     */
    public String read(String path) throws IOException {
        FSDataInputStream inputStream = hdfs.open(new Path(path));
        String out = IOUtils.toString(inputStream, "UTF-8");
        return out;
    }

    /**
     * Deletes file or directory
     *
     * @param path
     */
    public void delete(String path) throws IOException {
        hdfs.delete(new Path(path), true);
    }

    /**
     * Checks, is the "path" is directory or file
     *
     * @param path
     * @return
     */
    public boolean isDirectory(String path) throws IOException {
        FileStatus fileStatus = hdfs.getFileStatus(new Path(path));
        if (fileStatus.isDirectory()) {
            log.info("is directory {}", path);
            return true;
        } else {
            log.info("not is directory {}", path);
            return false;
        }
    }

    /**
     * Return the list of files and subdirectories on any directory
     *
     * @param path
     * @return
     */
    public List<String> getFilesList(String path) throws IOException {
        List<String> fileList = new ArrayList<String>();
        FileStatus[] fileStatus = hdfs.listStatus(new Path(path));
        for (FileStatus fileStat : fileStatus) {
            if (fileStat.isDirectory()) {
                String filePath = fileStat.getPath().toString();
                fileList.addAll(getFilesList(filePath));
            } else {
                fileList.add(fileStat.getPath().toString());
            }
        }
        return fileList;
    }
}
