package com.example.higerpoint.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.example.higerpoint.fastdfs.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author edz
 * @date 2019/8/5
 * @time 9:58
 */
@Slf4j
public class EasyPoiUtils {
    /**
     * 导出excel
     *
     * @param pojoClass
     * @param dataSet
     * @param path
     * @param filename
     * @throws IOException
     */
    public static String exportExcel(Class<?> pojoClass, Collection<?> dataSet, String path, String filename) throws IOException {

        File savefile = new File(path);
        if (!savefile.exists()) {
            boolean boo = savefile.mkdirs();
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, dataSet);
        FileOutputStream fos = new FileOutputStream(path + filename);
        workbook.write(fos);
        savefile = new File(path + filename);
        FileInputStream in = new FileInputStream(savefile);
        MultipartFile multipartFile = new MockMultipartFile(savefile.getName(), savefile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), in);
        String s = FastDFSClient.saveFile(multipartFile);
        in.close();
        fos.close();
        boolean boo = savefile.delete();
        return s;
    }

    /**
     * 根据Map创建对应的Excel(一个excel 创建多个sheet)
     *
     * @param list              list 多个Map key title 对应表格Title key entity 对应表格对应实体 key data
     *                          *             Collection 数据
     * @param path              路径
     * @param filename&emsp;文件名
     * @throws IOException
     */
    public static void exportExcel(List<Map<String, Object>> list, String path, String filename) throws IOException {
        File savefile = new File(path);
        if (!savefile.exists()) {
            boolean boo = savefile.mkdirs();
        }
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);

        FileOutputStream fos = new FileOutputStream(path + filename);
        workbook.write(fos);
        fos.close();
    }


    /**
     * 导入excel
     *
     * @param file
     * @param pojoClass
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(File file, Class<?> pojoClass, ImportParams params) {
        long start = System.currentTimeMillis();
        List<T> list = ExcelImportUtil.importExcel(file, pojoClass, params);
        return list;
    }

    /**
     * 导入excel
     *
     * @param mfile
     * @param pojoClass
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String path, MultipartFile mfile, Class<?> pojoClass, ImportParams params) {
        //把spring文件上传的MultipartFile转换成CommonsMultipartFile类型
        File file = new File(path);
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!file.exists()) {
            boolean boo = file.mkdirs();
        }
        //新建一个文件
        File file1 = new File(path + mfile.getOriginalFilename());
        //将上传的文件写入新建的文件中
        try {
            file1.createNewFile();
            FileOutputStream out = new FileOutputStream(file1);
            out.write(mfile.getBytes());
            out.close();
        } catch (Exception e) {
            log.error("出现异常{}", e);
        }
        long start = System.currentTimeMillis();
        List<T> list = ExcelImportUtil.importExcel(file1, pojoClass, params);
        file1.delete();
        return list;
    }
}
